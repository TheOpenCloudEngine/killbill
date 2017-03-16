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
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatter;
import org.killbill.billing.invoice.api.formatters.InvoiceFormatterFactory;
import org.killbill.billing.invoice.api.formatters.InvoiceItemFormatter;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory;
import org.killbill.billing.invoice.api.formatters.ResourceBundleFactory.ResourceBundleType;
import org.killbill.billing.invoice.template.formatters.DefaultInvoiceItemFormatter;
import org.killbill.billing.invoice.template.translator.DefaultInvoiceTranslator;
import org.killbill.billing.tenant.api.TenantInternalApi;
import org.killbill.billing.util.LocaleUtils;
import org.killbill.billing.util.callcontext.InternalCallContextFactory;
import org.killbill.billing.util.email.templates.TemplateEngine;
import org.killbill.billing.util.io.IOUtils;
import org.killbill.billing.util.template.translation.TranslatorConfig;
import org.killbill.xmlloader.UriAccessor;

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

        final ResourceBundle invoiceBundle = accountLocale != null ?
                                             bundleFactory.createBundle(LocaleUtils.toLocale(accountLocale), config.getInvoiceTemplateBundlePath(), ResourceBundleType.INVOICE_TRANSLATION, context) : null;
        final ResourceBundle defaultInvoiceBundle = bundleFactory.createBundle(Locale.getDefault(), config.getInvoiceTemplateBundlePath(), ResourceBundleType.INVOICE_TRANSLATION, context);
        final DefaultInvoiceTranslator invoiceTranslator = new DefaultInvoiceTranslator(invoiceBundle, defaultInvoiceBundle);

        data.put("text", invoiceTranslator);
        data.put("account", account);

        final InvoiceFormatter formattedInvoice = factory.createInvoiceFormatter(config, invoice, locale, currencyConversionApi, bundleFactory, context);
        data.put("invoice", formattedInvoice);



        //TODO. 인보이스 트랜스레이션을 조회해서, 트랜스레이션이 있다면
        // 1. data 의 text 항목을 교체하도록 한다.
        // 2. 조직을 조회해서 조직 항목을 넣도록 한다.


        //TODO. 인보이스 템플릿 조회해서, 템플릿이 있다면.
        // 1. 인보이스 제목을 템플릿의 subject 에서 가져온다.
        // 2. 인보이스 바디를 템플릿의 body 에서 가져온다.
        // 3. 포맷된 인보이스를 Map 변경한다.
        // 4. 포맷된 인보이스의 인보이스 아이템중, 플랜네임이 있다면, 디스플레이 네임에 추가하도록 한다.
        // 5. 플랜네임이 없다면, description 을 디스플레이 네임으로 카피하도록 한다.


//        List<InvoiceItem> invoiceItems = formattedInvoice.getInvoiceItems();
//        for (final InvoiceItem invoiceItem : invoiceItems) {
//            InvoiceItemFormatter invoiceItemFormatter = (InvoiceItemFormatter) invoiceItem;
//
//        }

        //TODO 인보이스 제목을 템플릿의 제목에서 뽑아오도록.
        invoiceData.setSubject(invoiceTranslator.getInvoiceEmailSubject());
        final String templateText = getTemplateText(locale, manualPay, context);
        invoiceData.setBody(templateEngine.executeTemplateText(templateText, data));
        return invoiceData;
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
