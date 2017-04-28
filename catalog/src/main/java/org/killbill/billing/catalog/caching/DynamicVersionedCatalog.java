/*
 * Copyright 2014-2017 Groupon, Inc
 * Copyright 2014-2017 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.catalog.caching;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.joda.time.DateTime;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.catalog.CatalogUpdater;
import org.killbill.billing.catalog.DefaultDuration;
import org.killbill.billing.catalog.DefaultFixed;
import org.killbill.billing.catalog.DefaultInternationalPrice;
import org.killbill.billing.catalog.DefaultPlan;
import org.killbill.billing.catalog.DefaultPlanPhase;
import org.killbill.billing.catalog.DefaultPrice;
import org.killbill.billing.catalog.DefaultPriceList;
import org.killbill.billing.catalog.DefaultPriceListSet;
import org.killbill.billing.catalog.DefaultProduct;
import org.killbill.billing.catalog.DefaultRecurring;
import org.killbill.billing.catalog.DefaultTier;
import org.killbill.billing.catalog.DefaultTieredBlock;
import org.killbill.billing.catalog.DefaultUnit;
import org.killbill.billing.catalog.DefaultUsage;
import org.killbill.billing.catalog.StandaloneCatalog;
import org.killbill.billing.catalog.VersionedCatalog;
import org.killbill.billing.catalog.api.BillingActionPolicy;
import org.killbill.billing.catalog.api.BillingAlignment;
import org.killbill.billing.catalog.api.BillingMode;
import org.killbill.billing.catalog.api.BillingPeriod;
import org.killbill.billing.catalog.api.BlockType;
import org.killbill.billing.catalog.api.CatalogApiException;
import org.killbill.billing.catalog.api.CatalogUserApi;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.catalog.api.PhaseType;
import org.killbill.billing.catalog.api.PlanAlignmentChange;
import org.killbill.billing.catalog.api.PlanAlignmentCreate;
import org.killbill.billing.catalog.api.Product;
import org.killbill.billing.catalog.api.ProductCategory;
import org.killbill.billing.catalog.api.SimplePlanDescriptor;
import org.killbill.billing.catalog.api.TimeUnit;
import org.killbill.billing.catalog.api.UsageType;
import org.killbill.billing.catalog.api.user.DefaultSimplePlanDescriptor;
import org.killbill.billing.catalog.rules.DefaultCaseBillingAlignment;
import org.killbill.billing.catalog.rules.DefaultCaseCancelPolicy;
import org.killbill.billing.catalog.rules.DefaultCaseChangePlanAlignment;
import org.killbill.billing.catalog.rules.DefaultCaseChangePlanPolicy;
import org.killbill.billing.catalog.rules.DefaultCaseCreateAlignment;
import org.killbill.billing.catalog.rules.DefaultPlanRules;
import org.killbill.clock.Clock;
import org.killbill.xmlloader.ValidationErrors;
import org.killbill.xmlloader.XMLSchemaGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.killbill.billing.catalog.uengine.model.BillingRule;
import org.killbill.billing.catalog.uengine.model.ProductDaoVersionWithPlan;
import org.killbill.billing.catalog.uengine.model.catalog.Phase;
import org.killbill.billing.catalog.uengine.model.catalog.Plan;
import org.killbill.billing.catalog.uengine.model.catalog.Price;
import org.killbill.billing.catalog.uengine.model.catalog.Tier;
import org.killbill.billing.catalog.uengine.model.catalog.Usage;
import org.killbill.billing.catalog.uengine.service.BillingRuleService;
import org.killbill.billing.catalog.uengine.service.SubscriptionEventService;
import org.killbill.billing.catalog.uengine.util.JsonUtils;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

/**
 * Created by uengine on 2017. 1. 4..
 */
public class DynamicVersionedCatalog {

    @Inject
    protected Clock clock;

    private final Logger logger = LoggerFactory.getLogger(DynamicVersionedCatalog.class);

    private static final String DEFAULT_CATALOG_NAME = "LargeOrders";
    private BillingMode recurringBillingMode;
    private VersionedCatalog versionedCatalog;

    private InternalTenantContext tenantContext;

    public DynamicVersionedCatalog(InternalTenantContext tenantContext) {
        this.tenantContext = tenantContext;
        this.versionedCatalog = new VersionedCatalog(clock);
    }

    public VersionedCatalog getVersionedCatalog() throws CatalogApiException {
        try {
            Long tenantRecordId = tenantContext.getTenantRecordId();
            Long accountRecordId = tenantContext.getAccountRecordId();

            logger.info("CatalogTestPluginApi : Initialized Dynamic Catalog with account {}, and tenant {}" + tenantRecordId, accountRecordId);

            BillingRuleService billingRuleService = new BillingRuleService();
            BillingRule billingRule = billingRuleService.selectRuleByTenantRecordId(tenantRecordId);

            SubscriptionEventService service = new SubscriptionEventService();
            List<ProductDaoVersionWithPlan> versionWithPlans = service.selectVersionReferencedByAccount(accountRecordId, tenantRecordId);

            if (versionWithPlans == null || versionWithPlans.isEmpty()) {
                return null;
            }

            //effective date 순으로 임시저장된 카달로그 정보
            List<Map> tempList = new ArrayList<Map>();

            for (final ProductDaoVersionWithPlan versionWithPlan : versionWithPlans) {
                Map tempCatalog = new HashMap();
                Map prevCatalog = null;

                //리스트가 비어있지 않다면
                if (!tempList.isEmpty()) {
                    //리스트의 가장 나중 카달로그를 가져온다.
                    prevCatalog = tempList.get(tempList.size() - 1);

                    //tempCatalog 에 prevCatalog 의 내용을 복사한다.
                    tempCatalog = new ObjectMapper().convertValue(prevCatalog, Map.class);
                }

                //effective_date 저장
                tempCatalog.put("effective_date", versionWithPlan.getEffective_date());

                //프로덕트 저장
                if (!tempCatalog.containsKey("products")) {
                    tempCatalog.put("products", new HashMap());
                }
                ((Map) tempCatalog.get("products")).put(versionWithPlan.getProduct_id(), versionWithPlan.getCategory());

                //플랜 저장
                if (!tempCatalog.containsKey("plans")) {
                    tempCatalog.put("plans", new HashMap());
                }
                //플랜 중 같은 이름을 가진 것만 tempCatalog 에 저장
                List<Map> plans = JsonUtils.unmarshalToList(versionWithPlan.getPlans());
                if (plans != null && !plans.isEmpty()) {
                    for (final Map plan : plans) {
                        if (versionWithPlan.getPlan_name().equals(plan.get("name").toString())) {
                            //플랜에 카테고리도 포함해준다.
                            plan.put("category", versionWithPlan.getCategory());
                            ((Map) tempCatalog.get("plans")).put(versionWithPlan.getPlan_name(), plan);
                        }
                    }
                }

                if (!tempList.isEmpty()) {
                    //가장 나중 카달로그의 effective date 가 versionWithPlan 과 같다면 prevCatalog 의 내용을 tempCatalog 로 교체
                    if (((Date) prevCatalog.get("effective_date")).getTime() == versionWithPlan.getEffective_date().getTime()) {
                        tempList.set(tempList.size() - 1, tempCatalog);
                    }
                    //아니라면 tempList 에 신규 저장한다.
                    else {
                        tempList.add(tempCatalog);
                    }
                } else {
                    tempList.add(tempCatalog);
                }
            }

            //임시저장된 카달로그의 모든 플랜과 가장 이전 내용을 도출.
            Map<String, Map> allPlans = new HashMap();
            for (final Map tempCatalog : tempList) {
                Map<String, Map> plans = (Map) tempCatalog.get("plans");
                for (Map.Entry<String, Map> entry : plans.entrySet()) {
                    String plan_name = entry.getKey();

                    //가장 이전을 적용하기 위해 존재할 경우 교체하지 않는다.
                    if (!allPlans.containsKey(plan_name)) {
                        allPlans.put(plan_name, new ObjectMapper().convertValue(entry.getValue(), Map.class));
                    }
                }
            }
            //임시저장된 카달로그 앞 버젼부터, 누락된 플랜이 있다면 가장 이전 내용으로 적용.
            for (final Map tempCatalog : tempList) {
                Map<String, String> products = (Map) tempCatalog.get("products");
                Map<String, Map> plans = (Map) tempCatalog.get("plans");
                for (final Entry<String, Map> entry : allPlans.entrySet()) {
                    String plan_name = entry.getKey();
                    String product_id = plan_name.substring(0, 14);
                    String category = entry.getValue().get("category").toString();

                    if (!plans.containsKey(plan_name)) {
                        plans.put(plan_name, new ObjectMapper().convertValue(entry.getValue(), Map.class));
                        products.put(product_id, category);
                    }
                }
            }

            //임시저장된 카달로그를 versionedCatalog 로 인서트
            for (final Map tempCatalog : tempList) {
                StandaloneCatalog catalog = new StandaloneCatalog();

                //price-list-repo
                HashSet<String> catalogPriceListPlans = new HashSet<String>();

                //currency-repo
                HashSet<String> catalogCurrencies = new HashSet<String>();

                //unit-repo
                HashSet<String> catalogUnits = new HashSet<String>();

                //catalog-name
                catalog.setCatalogName(billingRule.getOrganization_id());

                //effective_date
                catalog.setEffectiveDate((Date) tempCatalog.get("effective_date"));

                /**
                 * plan rule Start
                 */
                DefaultPlanRules planRules = new DefaultPlanRules();
                Map rule = JsonUtils.unmarshal(billingRule.getRule());

                //recurringBillingMode
                if (rule.containsKey("recurringBillingMode")) {
                    catalog.setRecurringBillingMode(BillingMode.valueOf(rule.get("recurringBillingMode").toString()));
                } else {
                    catalog.setRecurringBillingMode(BillingMode.IN_ADVANCE);
                }

                //billingAlignment rule
                if (rule.containsKey("billingAlignment")) {
                    ArrayList<DefaultCaseBillingAlignment> cases = new ArrayList<DefaultCaseBillingAlignment>();
                    List<Map> billingAlignment = (List<Map>) rule.get("billingAlignment");
                    for (final Map map : billingAlignment) {
                        DefaultCaseBillingAlignment caseBillingAlignment = new DefaultCaseBillingAlignment();
                        if (map.containsKey("productCategory")) {
                            caseBillingAlignment.setProductCategory(ProductCategory.valueOf(map.get("productCategory").toString()));
                        }
                        if (map.containsKey("billingPeriod")) {
                            caseBillingAlignment.setBillingPeriod(BillingPeriod.valueOf(map.get("billingPeriod").toString()));
                        }
                        if (map.containsKey("phaseType")) {
                            caseBillingAlignment.setPhaseType(PhaseType.valueOf(map.get("phaseType").toString()));
                        }
                        if (map.containsKey("billingAlignment")) {
                            caseBillingAlignment.setAlignment(BillingAlignment.valueOf(map.get("billingAlignment").toString()));
                        }
                        cases.add(caseBillingAlignment);
                    }
                    planRules.setBillingAlignmentCase(cases.toArray(new DefaultCaseBillingAlignment[cases.size()]));
                }

                //createAlignment rule
                if (rule.containsKey("createAlignment")) {
                    ArrayList<DefaultCaseCreateAlignment> cases = new ArrayList<DefaultCaseCreateAlignment>();
                    List<Map> createAlignment = (List<Map>) rule.get("createAlignment");
                    for (final Map map : createAlignment) {
                        DefaultCaseCreateAlignment caseCreateAlignment = new DefaultCaseCreateAlignment();
                        if (map.containsKey("planAlignmentCreate")) {
                            caseCreateAlignment.setAlignment(PlanAlignmentCreate.valueOf(map.get("planAlignmentCreate").toString()));
                        }
                        cases.add(caseCreateAlignment);
                    }
                    planRules.setCreateAlignmentCase(cases.toArray(new DefaultCaseCreateAlignment[cases.size()]));
                }

                //cancelPolicy rule
                if (rule.containsKey("cancelPolicy")) {
                    ArrayList<DefaultCaseCancelPolicy> cases = new ArrayList<DefaultCaseCancelPolicy>();
                    List<Map> cancelPolicy = (List<Map>) rule.get("cancelPolicy");
                    for (final Map map : cancelPolicy) {
                        DefaultCaseCancelPolicy caseCancelPolicy = new DefaultCaseCancelPolicy();
                        if (map.containsKey("productCategory")) {
                            caseCancelPolicy.setProductCategory(ProductCategory.valueOf(map.get("productCategory").toString()));
                        }
                        if (map.containsKey("billingPeriod")) {
                            caseCancelPolicy.setBillingPeriod(BillingPeriod.valueOf(map.get("billingPeriod").toString()));
                        }
                        if (map.containsKey("phaseType")) {
                            caseCancelPolicy.setPhaseType(PhaseType.valueOf(map.get("phaseType").toString()));
                        }
                        if (map.containsKey("billingActionPolicy")) {
                            caseCancelPolicy.setPolicy(BillingActionPolicy.valueOf(map.get("billingActionPolicy").toString()));
                        }
                        cases.add(caseCancelPolicy);
                    }
                    planRules.setCancelCase(cases.toArray(new DefaultCaseCancelPolicy[cases.size()]));
                }

                //changePolicy rule
                if (rule.containsKey("changePolicy")) {
                    ArrayList<DefaultCaseChangePlanPolicy> cases = new ArrayList<DefaultCaseChangePlanPolicy>();
                    List<Map> changePolicy = (List<Map>) rule.get("changePolicy");
                    for (final Map map : changePolicy) {
                        DefaultCaseChangePlanPolicy caseChangePlanPolicy = new DefaultCaseChangePlanPolicy();
                        if (map.containsKey("phaseType")) {
                            caseChangePlanPolicy.setPhaseType(PhaseType.valueOf(map.get("phaseType").toString()));
                        }
                        if (map.containsKey("fromProductCategory")) {
                            caseChangePlanPolicy.setFromProductCategory(ProductCategory.valueOf(map.get("fromProductCategory").toString()));
                        }
                        if (map.containsKey("fromBillingPeriod")) {
                            caseChangePlanPolicy.setFromBillingPeriod(BillingPeriod.valueOf(map.get("fromBillingPeriod").toString()));
                        }
                        if (map.containsKey("toProductCategory")) {
                            caseChangePlanPolicy.setToProductCategory(ProductCategory.valueOf(map.get("toProductCategory").toString()));
                        }
                        if (map.containsKey("toBillingPeriod")) {
                            caseChangePlanPolicy.setToBillingPeriod(BillingPeriod.valueOf(map.get("toBillingPeriod").toString()));
                        }
                        if (map.containsKey("billingActionPolicy")) {
                            caseChangePlanPolicy.setPolicy(BillingActionPolicy.valueOf(map.get("billingActionPolicy").toString()));
                        }
                        cases.add(caseChangePlanPolicy);
                    }
                    planRules.setChangeCase(cases.toArray(new DefaultCaseChangePlanPolicy[cases.size()]));
                }

                //changeAlignment rule
                if (rule.containsKey("changeAlignment")) {
                    ArrayList<DefaultCaseChangePlanAlignment> cases = new ArrayList<DefaultCaseChangePlanAlignment>();
                    List<Map> changeAlignment = (List<Map>) rule.get("changeAlignment");
                    for (final Map map : changeAlignment) {
                        DefaultCaseChangePlanAlignment caseChangePlanAlignment = new DefaultCaseChangePlanAlignment();
                        if (map.containsKey("phaseType")) {
                            caseChangePlanAlignment.setPhaseType(PhaseType.valueOf(map.get("phaseType").toString()));
                        }
                        if (map.containsKey("fromProductCategory")) {
                            caseChangePlanAlignment.setFromProductCategory(ProductCategory.valueOf(map.get("fromProductCategory").toString()));
                        }
                        if (map.containsKey("fromBillingPeriod")) {
                            caseChangePlanAlignment.setFromBillingPeriod(BillingPeriod.valueOf(map.get("fromBillingPeriod").toString()));
                        }
                        if (map.containsKey("toProductCategory")) {
                            caseChangePlanAlignment.setToProductCategory(ProductCategory.valueOf(map.get("toProductCategory").toString()));
                        }
                        if (map.containsKey("toBillingPeriod")) {
                            caseChangePlanAlignment.setToBillingPeriod(BillingPeriod.valueOf(map.get("toBillingPeriod").toString()));
                        }
                        if (map.containsKey("planAlignmentChange")) {
                            caseChangePlanAlignment.setAlignment(PlanAlignmentChange.valueOf(map.get("planAlignmentChange").toString()));
                        }
                        cases.add(caseChangePlanAlignment);
                    }
                    planRules.setChangeAlignmentCase(cases.toArray(new DefaultCaseChangePlanAlignment[cases.size()]));
                }
                catalog.setPlanRules(planRules);

                /**
                 * product Start
                 */
                Map<String, String> products = (Map) tempCatalog.get("products");

                //애드온 목록을 따로 모아둔다.
                List<Product> addons = new ArrayList<Product>();
                for (Map.Entry<String, String> entry : products.entrySet()) {
                    if (entry.getValue().equals(ProductCategory.ADD_ON.toString())) {
                        DefaultProduct addon_product = new DefaultProduct();
                        //addon_product.setCategory(ProductCategory.ADD_ON);
                        addon_product.setName(entry.getKey());
                        addons.add(addon_product);
                    }
                }
                List<Product> iterableProduts = new ArrayList<Product>();
                for (Map.Entry<String, String> entry : products.entrySet()) {
                    DefaultProduct product = new DefaultProduct();
                    product.setCategory(ProductCategory.valueOf(entry.getValue()));
                    product.setName(entry.getKey());

                    //베이스 프로덕트인 경우 available 에 addon 을 넣어준다.
                    if (entry.getValue().equals(ProductCategory.BASE.toString())) {
                        product.setAvailable(addons);
                    }
                    iterableProduts.add(product);
                }
                catalog.setProducts(iterableProduts);

                /**
                 * plan Start
                 */
                Map<String, Map> plans = (Map) tempCatalog.get("plans");
                List<org.killbill.billing.catalog.api.Plan> iterablePlans = new ArrayList<org.killbill.billing.catalog.api.Plan>();
                for (Map.Entry<String, Map> entry : plans.entrySet()) {
                    String category = entry.getValue().get("category").toString();
                    entry.getValue().remove("category");
                    Plan plan = new ObjectMapper().convertValue(entry.getValue(), Plan.class);
                    DefaultPlan defaultPlan = new DefaultPlan();

                    //번들에 허용가능한 플랜 수 지정
                    defaultPlan.setPlansAllowedInBundle(10000);

                    //플랜 이름
                    defaultPlan.setName(plan.getName());

                    //프로덕트
                    defaultPlan.setProduct(
                            new DefaultProduct().setName(plan.getName().substring(0, 14))
                                                .setCatagory(ProductCategory.valueOf(category)));

                    //프라이스 리스트 네임
                    defaultPlan.setPriceListName("DEFAULT");

                    //인티얼 Phase
                    ArrayList<DefaultPlanPhase> convertedIntialPhases = new ArrayList<DefaultPlanPhase>();
                    List<Phase> initialPhases = plan.getInitialPhases();
                    if (initialPhases != null && !initialPhases.isEmpty()) {
                        for (final Phase phase : initialPhases) {
                            convertedIntialPhases.add(convertPhase(phase, defaultPlan, catalogUnits, catalogCurrencies));
                        }
                        if (!convertedIntialPhases.isEmpty()) {
                            defaultPlan.setInitialPhases(convertedIntialPhases.toArray(new DefaultPlanPhase[convertedIntialPhases.size()]));
                        }
                    }

                    //파이널 Phase
                    if (plan.getFinalPhase() != null) {
                        defaultPlan.setFinalPhase(convertPhase(plan.getFinalPhase(), defaultPlan, catalogUnits, catalogCurrencies));
                    }

                    //리스트 추가
                    iterablePlans.add(defaultPlan);

                    //TODO 여기서 카달로그의 프라이스 리스트에 플랜이름을 추가해주도록 한다.
                    catalogPriceListPlans.add(plan.getName());
                }
                catalog.setPlans(iterablePlans);

                /**
                 * Price-list
                 */
                ArrayList<org.killbill.billing.catalog.api.Plan> priceListPlans = new ArrayList<org.killbill.billing.catalog.api.Plan>();
                for (final String plan_name : catalogPriceListPlans) {
                    priceListPlans.add(new DefaultPlan().setName(plan_name));
                }

                DefaultPriceList defaultPriceList = new DefaultPriceList();
                defaultPriceList.setPlans(priceListPlans);
                defaultPriceList.setName("DEFAULT");
                DefaultPriceListSet defaultPriceListSet = new DefaultPriceListSet(defaultPriceList, null);
                catalog.setPriceLists(defaultPriceListSet);

                /**
                 * Units
                 */

                ArrayList<DefaultUnit> defaultUnits = new ArrayList<DefaultUnit>();
                for (final String catalogUnit : catalogUnits) {
                    defaultUnits.add(new DefaultUnit().setName(catalogUnit));
                }
                catalog.setUnits(defaultUnits.toArray(new DefaultUnit[defaultUnits.size()]));

                /**
                 * Currency
                 */
                ArrayList<Currency> currencies = new ArrayList<Currency>();
                for (final String catalogCurrency : catalogCurrencies) {
                    currencies.add(Currency.valueOf(catalogCurrency));
                }
                catalog.setSupportedCurrencies(currencies.toArray(new Currency[currencies.size()]));

                OutputStream output = new OutputStream() {
                    private StringBuilder string = new StringBuilder();

                    @Override
                    public void write(int b) throws IOException {
                        this.string.append((char) b);
                    }

                    //Netbeans IDE automatically overrides this toString()
                    public String toString() {
                        return this.string.toString();
                    }
                };
//                Marshaller m = marshaller(StandaloneCatalog.class);
//                m.marshal(catalog, output);
//                System.out.println(output);

                /**
                 * final
                 */
                versionedCatalog.add(catalog);
            }
            return versionedCatalog;

        } catch (Exception ex) {
            ex.printStackTrace();
            return versionedCatalog;
        }
    }

    public static Marshaller marshaller(final Class<?> clazz) throws JAXBException, SAXException, IOException, TransformerException {
        final JAXBContext context = JAXBContext.newInstance(clazz);

        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Marshaller um = context.createMarshaller();

        final Schema schema = factory.newSchema(new StreamSource(XMLSchemaGenerator.xmlSchema(clazz)));
        um.setSchema(schema);

        return um;
    }

    private DefaultPlanPhase convertPhase(Phase phase, DefaultPlan plan, HashSet<String> catalogUnits, HashSet<String> catalogCurrencies) {
        DefaultPlanPhase defaultPlanPhase = new DefaultPlanPhase();

        //plan
        defaultPlanPhase.setPlan(plan);

        //phaseType
        defaultPlanPhase.setPhaseType(PhaseType.valueOf(phase.getType()));

        //duration
        DefaultDuration defaultDuration = new DefaultDuration();
        defaultDuration.setUnit(TimeUnit.valueOf(phase.getDuration().getUnit()));
        if (phase.getDuration().getNumber() != null) {
            defaultDuration.setNumber(phase.getDuration().getNumber().intValue());
        }
        defaultPlanPhase.setDuration(defaultDuration);

        //fixed
        if (phase.getFixed() != null) {
            DefaultFixed defaultFixed = new DefaultFixed();
            defaultFixed.setFixedPrice(convertPrice(phase.getFixed().getFixedPrice(), catalogCurrencies));

            defaultPlanPhase.setFixed(defaultFixed);
        }

        //recurring
        if (phase.getRecurring() != null) {
            DefaultRecurring defaultRecurring = new DefaultRecurring();
            defaultRecurring.setBillingPeriod(BillingPeriod.valueOf(phase.getRecurring().getBillingPeriod()));
            defaultRecurring.setRecurringPrice(convertPrice(phase.getRecurring().getRecurringPrice(), catalogCurrencies));

            defaultPlanPhase.setRecurring(defaultRecurring);
        }

        //usages
        List<Usage> usages = phase.getUsages();
        if (usages != null && !usages.isEmpty()) {
            ArrayList<DefaultUsage> defaultUsages = new ArrayList<DefaultUsage>();
            for (final Usage usage : usages) {
                DefaultUsage defaultUsage = new DefaultUsage();

                //usage name
                defaultUsage.setName(usage.getName());

                //usage type
                defaultUsage.setUsageType(UsageType.valueOf(usage.getUsageType()));

                //usage billing mode
                defaultUsage.setBillingMode(BillingMode.valueOf(usage.getBillingMode()));

                //usage billing period
                defaultUsage.setBillingPeriod(BillingPeriod.valueOf(usage.getBillingPeriod()));

                //usage tiers
                List<Tier> tiers = usage.getTiers();
                ArrayList<DefaultTier> defaultTiers = new ArrayList<DefaultTier>();
                for (final Tier tier : tiers) {
                    DefaultTier defaultTier = new DefaultTier();
                    ArrayList<DefaultTieredBlock> defaultTieredBlocks = new ArrayList<DefaultTieredBlock>();
                    DefaultTieredBlock defaultTieredBlock = new DefaultTieredBlock();
                    defaultTieredBlock.setMax(tier.getMax());
                    defaultTieredBlock.setSize(tier.getSize());

                    //TODO 여기서 카달로그의 유닛 리스트에 추가해주도록 한다.
                    defaultTieredBlock.setUnit(new DefaultUnit().setName(tier.getUnit()));
                    catalogUnits.add(tier.getUnit());

                    defaultTieredBlock.setPrices(convertPrice(tier.getPrices(), catalogCurrencies));
                    defaultTieredBlock.setType(BlockType.TIERED);

                    defaultTieredBlocks.add(defaultTieredBlock);
                    defaultTier.setBlocks(defaultTieredBlocks.toArray(new DefaultTieredBlock[defaultTieredBlocks.size()]));
                    defaultTiers.add(defaultTier);
                }
                defaultUsage.setTiers(defaultTiers.toArray(new DefaultTier[defaultTiers.size()]));

                defaultUsages.add(defaultUsage);
            }
            defaultPlanPhase.setUsages(defaultUsages.toArray(new DefaultUsage[defaultUsages.size()]));
        }

        return defaultPlanPhase;
    }

    private DefaultInternationalPrice convertPrice(List<Price> prices, HashSet<String> catalogCurrencies) {
        DefaultInternationalPrice internationalPrice = new DefaultInternationalPrice();
        ArrayList<DefaultPrice> defaultPrices = new ArrayList<DefaultPrice>();
        for (final Price price : prices) {
            DefaultPrice defaultPrice = new DefaultPrice();
            defaultPrice.setCurrency(Currency.valueOf(price.getCurrency()));
            defaultPrice.setValue(price.getValue());
            defaultPrices.add(defaultPrice);

            //TODO 카달로그의 커런시 리스트에 추가해줄 수 있도록 한다.
            catalogCurrencies.add(price.getCurrency());

        }
        internationalPrice.setPrices(defaultPrices.toArray(new DefaultPrice[defaultPrices.size()]));
        return internationalPrice;
    }
}
