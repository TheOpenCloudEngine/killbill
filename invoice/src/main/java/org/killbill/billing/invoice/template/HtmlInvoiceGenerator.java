/*
 * Copyright 2010-2013 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
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

package org.killbill.billing.invoice.template;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Nullable;

import org.killbill.billing.account.api.Account;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.currency.api.CurrencyConversionApi;
import org.killbill.billing.invoice.api.Invoice;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatter;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatterFactory;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory.ResourceBundleType;
import org.killbill.billing.invoice.template.translator.DefaultInvoiceTranslator;
import org.killbill.billing.tenant.api.TenantInternalApi;
import org.killbill.billing.util.LocaleUtils;
import org.killbill.billing.util.callcontext.InternalCallContextFactory;
import org.killbill.billing.util.email.templates.TemplateEngine;
import org.killbill.billing.util.io.IOUtils;
import org.killbill.billing.util.template.translation.TranslatorConfig;
import org.killbill.xmlloader.UriAccessor;
import org.uengine.garuda.killbill.invoice.model.NotificationConfig;
import org.uengine.garuda.killbill.invoice.model.NotificationType;
import org.uengine.garuda.killbill.invoice.model.Organization;
import org.uengine.garuda.killbill.invoice.model.Template;
import org.uengine.garuda.killbill.invoice.service.InvoiceExtService;
import org.uengine.garuda.killbill.invoice.util.JsonUtils;

import com.google.common.base.Strings;
import com.google.inject.Inject;

public class HtmlInvoiceGenerator {

    private final InvoiceFormatterFactory factory;
    private final TranslatorConfig config;
    private final CurrencyConversionApi currencyConversionApi;
    private final TemplateEngine templateEngine;
    private final TenantInternalApi tenantApi;
    private final ResourceBundleFactory bundleFactory;

    @Inject
    public HtmlInvoiceGenerator(final InvoiceFormatterFactory factory,
                                final TemplateEngine templateEngine,
                                final TranslatorConfig config,
                                final CurrencyConversionApi currencyConversionApi,
                                final ResourceBundleFactory bundleFactory,
                                final TenantInternalApi tenantInternalApi) {
        this.factory = factory;
        this.config = config;
        this.currencyConversionApi = currencyConversionApi;
        this.templateEngine = templateEngine;
        this.bundleFactory = bundleFactory;
        this.tenantApi = tenantInternalApi;
    }

    public HtmlInvoice generateInvoice(final Account account, @Nullable final Invoice invoice, final boolean manualPay, final InternalTenantContext context) throws IOException {
        // Don't do anything if the invoice is null
        if (invoice == null) {
            return null;
        }

        final String accountLocale = Strings.emptyToNull(account.getLocale());
        final Locale locale = accountLocale == null ? Locale.getDefault() : LocaleUtils.toLocale(accountLocale);

        final HtmlInvoice invoiceData = new HtmlInvoice();
        final Map<String, Object> data = new HashMap<String, Object>();

        data.put("account", account);

        final InvoiceFormatter formattedInvoice = factory.createInvoiceFormatter(config, invoice, locale, currencyConversionApi, bundleFactory, context);
        data.put("invoice", formattedInvoice);

        InvoiceExtService invoiceExtService = null;
        Organization organization = null;

        String property = System.getProperty("org.killbill.catalog.mode");
        if ("dynamic".equals(property)) {
            invoiceExtService = new InvoiceExtService();
            organization = invoiceExtService.selectOrganizationFromAccountId(account.getId().toString());
        }

        Template currentTemplate = null;
        if (organization != null) {
            NotificationConfig notificationConfig = invoiceExtService.selectConfigByOrgId(organization.getId());
            String configuration = notificationConfig.getConfiguration();
            Map configMap = JsonUtils.unmarshal(configuration);

            //organization 에 인보이스 발송 설정이 금지일 경우 보내지 않는다.
            final boolean send = (Boolean) configMap.get(NotificationType.INVOICE.toString());
            if (!send) {
                return null;
            }

            List<Template> templates = invoiceExtService.selectByOrgIdAndType(organization.getId(), NotificationType.INVOICE.toString());

            //템플릿 중, 어카운트의 로케일과 같은 것을 찾는다.
            for (final Template template : templates) {
                if (template.getLocale().equals(account.getLocale())) {
                    currentTemplate = template;
                }
            }
            //템플릿이 없다면, 디폴트 템플릿을 찾는다.
            if (currentTemplate == null) {
                for (final Template template : templates) {
                    if ("Y".equals(template.getIs_default())) {
                        currentTemplate = template;
                    }
                }
            }
        }

        //조직이 없거나, currentTemplate 을 찾을 수 없는 경우 원래 로직대로 처리한다.
        if (organization == null || currentTemplate == null) {
            final ResourceBundle invoiceBundle = accountLocale != null ?
                                                 bundleFactory.createBundle(LocaleUtils.toLocale(accountLocale), config.getInvoiceTemplateBundlePath(), ResourceBundleType.INVOICE_TRANSLATION, context) : null;
            final ResourceBundle defaultInvoiceBundle = bundleFactory.createBundle(Locale.getDefault(), config.getInvoiceTemplateBundlePath(), ResourceBundleType.INVOICE_TRANSLATION, context);
            final DefaultInvoiceTranslator invoiceTranslator = new DefaultInvoiceTranslator(invoiceBundle, defaultInvoiceBundle);

            data.put("text", invoiceTranslator);

            invoiceData.setSubject(invoiceTranslator.getInvoiceEmailSubject());
            final String templateText = getTemplateText(locale, manualPay, context);
            invoiceData.setBody(templateEngine.executeTemplateText(templateText, data));
            return invoiceData;
        }

        // 1. 인보이스 제목을 템플릿의 subject 에서 가져온다.
        // 2. 인보이스 바디를 템플릿의 body 에서 가져온다.
        else {
            //조직 데이터 추가
            data.put("organization", organization);
            invoiceData.setSubject(templateEngine.executeTemplateText(currentTemplate.getSubject(), data));
            invoiceData.setBody(templateEngine.executeTemplateText(currentTemplate.getBody(), data));
            return invoiceData;
        }
    }

    private String getTemplateText(final Locale locale, final boolean manualPay, final InternalTenantContext context) throws IOException {

        if (context.getTenantRecordId() == InternalCallContextFactory.INTERNAL_TENANT_RECORD_ID) {
            return getDefaultTemplate(manualPay ? config.getManualPayTemplateName() : config.getTemplateName());
        }
        final String template = manualPay ?
                                tenantApi.getManualPayInvoiceTemplate(locale, context) :
                                tenantApi.getInvoiceTemplate(locale, context);
        return template == null ?
               getDefaultTemplate(manualPay ? config.getManualPayTemplateName() : config.getTemplateName()) :
               template;
    }

    private String getDefaultTemplate(final String templateName) throws IOException {
        try {
            final InputStream templateStream = UriAccessor.accessUri(templateName);
            return IOUtils.toString(templateStream);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}
