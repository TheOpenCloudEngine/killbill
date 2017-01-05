/*
 * Copyright 2014-2015 Groupon, Inc
 * Copyright 2014-2015 The Billing Project, LLC
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

package org.killbill.billing.catalog.plugin;

import java.util.List;

import javax.inject.Inject;

import org.killbill.billing.callcontext.InternalTenantContext;
import org.killbill.billing.catalog.StandaloneCatalog;
import org.killbill.billing.catalog.StandaloneCatalogWithPriceOverride;
import org.killbill.billing.catalog.VersionedCatalog;
import org.killbill.billing.catalog.api.CatalogApiException;
import org.killbill.billing.catalog.override.PriceOverride;
import org.killbill.billing.catalog.plugin.api.StandalonePluginCatalog;
import org.killbill.billing.catalog.plugin.api.VersionedPluginCatalog;
import org.killbill.billing.util.callcontext.InternalCallContextFactory;
import org.killbill.clock.Clock;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class VersionedCatalogMapper {

    private final Clock clock;

    private final PriceOverride priceOverride;
    private final InternalCallContextFactory internalCallContextFactory;

    @Inject
    public VersionedCatalogMapper(final Clock clock, final PriceOverride priceOverride, final InternalCallContextFactory internalCallContextFactory) {
        this.clock = clock;
        this.priceOverride = priceOverride;
        this.internalCallContextFactory = internalCallContextFactory;
    }

    public VersionedCatalog toVersionedCatalog(final VersionedPluginCatalog pluginCatalog, final InternalTenantContext internalTenantContext) throws CatalogApiException {
        final VersionedCatalog result = new VersionedCatalog(clock);
        for (final StandalonePluginCatalog cur : pluginCatalog.getStandalonePluginCatalogs()) {
            result.add(toStandaloneCatalogWithPriceOverride(pluginCatalog, cur, internalTenantContext));
        }
        return result;
    }

    /**
     * 다이나믹 하게 구성한 versionedCatalog 에 clock 을 배정한다.
     * @param versionedCatalog
     * @param internalTenantContext
     * @return
     * @throws CatalogApiException
     */
    public VersionedCatalog toVersionedCatalog(final VersionedCatalog versionedCatalog, final InternalTenantContext internalTenantContext) throws CatalogApiException {
        final VersionedCatalog result = new VersionedCatalog(clock);
        for (final StandaloneCatalog standaloneCatalog : versionedCatalog.getVersions()) {
            result.add(toStandaloneCatalogWithPriceOverride(standaloneCatalog, internalTenantContext));
        }
        return result;
    }

    private StandaloneCatalogWithPriceOverride toStandaloneCatalogWithPriceOverride(final VersionedPluginCatalog pluginCatalog, final StandalonePluginCatalog input, final InternalTenantContext internalTenantContext) {
        final StandaloneCatalogMapper mapper = new StandaloneCatalogMapper(pluginCatalog.getCatalogName(), pluginCatalog.getRecurringBillingMode());
        final StandaloneCatalog catalog = mapper.toStandaloneCatalog(input, null);
        final StandaloneCatalogWithPriceOverride result = new StandaloneCatalogWithPriceOverride(catalog, priceOverride, internalTenantContext.getTenantRecordId(), internalCallContextFactory);
        return result;
    }

    /**
     * 다이나믹 하게 구성한 versionedCatalog 의 StandaloneCatalog 에 priceOverride 를 배정한다.
     * Api 호출시 priceOverride 를 구성할 경우에 해당된다.
     * @param catalog
     * @param internalTenantContext
     * @return
     */
    private StandaloneCatalogWithPriceOverride toStandaloneCatalogWithPriceOverride(final StandaloneCatalog catalog, final InternalTenantContext internalTenantContext) {
        final StandaloneCatalogWithPriceOverride result = new StandaloneCatalogWithPriceOverride(catalog, priceOverride, internalTenantContext.getTenantRecordId(), internalCallContextFactory);
        return result;
    }
}
