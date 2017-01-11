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

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.catalog.CatalogUpdater;
import org.killbill.billing.catalog.StandaloneCatalog;
import org.killbill.billing.catalog.VersionedCatalog;
import org.killbill.billing.catalog.api.BillingAlignment;
import org.killbill.billing.catalog.api.BillingMode;
import org.killbill.billing.catalog.api.BillingPeriod;
import org.killbill.billing.catalog.api.CatalogApiException;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.catalog.api.ProductCategory;
import org.killbill.billing.catalog.api.SimplePlanDescriptor;
import org.killbill.billing.catalog.api.TimeUnit;
import org.killbill.billing.catalog.api.user.DefaultSimplePlanDescriptor;
import org.killbill.clock.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
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
        logger.info("CatalogTestPluginApi : Initialized Dynamic Catalog with static catalog " + DEFAULT_CATALOG_NAME);

        Long tenantRecordId = tenantContext.getTenantRecordId();
        Long accountRecordId = tenantContext.getAccountRecordId();
        logger.info("{}, {} ", tenantRecordId, accountRecordId);

        //TODO
        // 1.tenantRecordId, accountRecordId 로 특정인이 구매한 플랜들의 version 들을 uengine billing 에서 받음.
        // 2.uengine-billing 의 organization 의 currency, plan-price-list 받음.
        // 3.versionedCatalog 조합.

        //Case. Large Catalog Test.
        int count = 12;
        for (int i = 1; i <= count; i++) {
            versionedCatalog.add(buildLargeCatalog(i));
        }
        return versionedCatalog;
    }

    private StandaloneCatalog buildLargeCatalog(int month) throws CatalogApiException {
        this.recurringBillingMode = BillingMode.IN_ADVANCE;

        //final CatalogUpdater catalogUpdater = new CatalogUpdater(DEFAULT_CATALOG_NAME, recurringBillingMode, new DateTime(2016, month, 8, 0, 0), null);
        final CatalogUpdater catalogUpdater = new CatalogUpdater(DEFAULT_CATALOG_NAME, recurringBillingMode, new DateTime(2016, month, 8, 0, 0), BillingAlignment.SUBSCRIPTION, null);

        int MAX_PLANS = 10;
        for (int i = 1; i <= MAX_PLANS; i++) {
            final SimplePlanDescriptor desc = new DefaultSimplePlanDescriptor("foo-monthly-" + i + "-pl", "Foo", ProductCategory.BASE, Currency.USD, BigDecimal.valueOf(100), BillingPeriod.MONTHLY, 0, TimeUnit.UNLIMITED, ImmutableList.<String>of());
            catalogUpdater.addSimplePlanDescriptor(desc);
            if (i % 1000 == 0) {
                System.err.println("++++++++++++  Iteration = " + i);
            }
        }

        return catalogUpdater.getCatalog();
    }
}
