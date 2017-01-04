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

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;

import org.joda.time.DateTime;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.catalog.CatalogUpdater;
import org.killbill.billing.catalog.DefaultDuration;
import org.killbill.billing.catalog.DefaultInternationalPrice;
import org.killbill.billing.catalog.DefaultPlan;
import org.killbill.billing.catalog.DefaultPlanPhase;
import org.killbill.billing.catalog.DefaultPrice;
import org.killbill.billing.catalog.DefaultPriceList;
import org.killbill.billing.catalog.DefaultPriceListSet;
import org.killbill.billing.catalog.DefaultProduct;
import org.killbill.billing.catalog.DefaultRecurring;
import org.killbill.billing.catalog.StandaloneCatalog;
import org.killbill.billing.catalog.VersionedCatalog;
import org.killbill.billing.catalog.api.BillingMode;
import org.killbill.billing.catalog.api.BillingPeriod;
import org.killbill.billing.catalog.api.CatalogApiException;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.catalog.api.PhaseType;
import org.killbill.billing.catalog.api.Plan;
import org.killbill.billing.catalog.api.Product;
import org.killbill.billing.catalog.api.ProductCategory;
import org.killbill.billing.catalog.api.SimplePlanDescriptor;
import org.killbill.billing.catalog.api.TimeUnit;
import org.killbill.billing.catalog.api.user.DefaultSimplePlanDescriptor;
import org.killbill.billing.catalog.plugin.api.VersionedPluginCatalog;
import org.killbill.xmlloader.XMLLoader;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Created by uengine on 2017. 1. 4..
 */
public class DynamicVersionedCatalog {

    private final Logger logger = LoggerFactory.getLogger(DynamicVersionedCatalog.class);

    private static final String DEFAULT_CATALOG_NAME = "LargeOrders";
    private List<StandaloneCatalog> versions;

    private BillingMode recurringBillingMode;
    private VersionedCatalog versionedCatalog;

    private InternalTenantContext tenantContext;

    public DynamicVersionedCatalog(InternalTenantContext tenantContext) {
        this.tenantContext = tenantContext;
        this.versionedCatalog = new VersionedCatalog();
    }

    public VersionedCatalog getVersionedCatalog() throws CatalogApiException{
        logger.info("CatalogTestPluginApi : Initialized Dynamic Catalog with static catalog " + DEFAULT_CATALOG_NAME);

        Long tenantRecordId = tenantContext.getTenantRecordId();
        Long accountRecordId = tenantContext.getAccountRecordId();

        //TODO tenantRecordId, accountRecordId 로 특정인이 구매한 플랜만으로 이루어진 Tiny Catalog 리턴.

//        versions = new ArrayList<StandaloneCatalog>();
//        versions.add(buildLargeCatalog());

        //Case. Large Catalog Test.
        versionedCatalog.add(buildLargeCatalog());
        return versionedCatalog;
    }

    private StandaloneCatalog buildLargeCatalog() throws CatalogApiException {
        this.recurringBillingMode = BillingMode.IN_ADVANCE;

        final CatalogUpdater catalogUpdater = new CatalogUpdater(DEFAULT_CATALOG_NAME, BillingMode.IN_ADVANCE, new DateTime(2011, 10, 8, 0, 0), null);
        int MAX_PLANS = 15000;
        for (int i = 1; i <= MAX_PLANS; i++) {
            final SimplePlanDescriptor desc = new DefaultSimplePlanDescriptor("foo-monthly-" + i + "-pl", "Foo", ProductCategory.BASE, Currency.USD, BigDecimal.TEN, BillingPeriod.MONTHLY, 0, TimeUnit.UNLIMITED, ImmutableList.<String>of());
            catalogUpdater.addSimplePlanDescriptor(desc);
            if (i % 1000 == 0) {
                System.err.println("++++++++++++  Iteration = " + i);
            }
        }
        return catalogUpdater.getCatalog();
    }
}
