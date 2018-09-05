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

package org.killbill.billing.catalog.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * The interface {@code StaticCatalog} gives the view of that {@code Catalog} at a given time.
 * This represents a specific version of the {@code Catalog}
 */
public interface StaticCatalog {

    /**
     * @return the {@code Catalog} name
     */
    public String getCatalogName();

    /**
     * @return the recurring billing mode for this catalog
     */
    public BillingMode getRecurringBillingMode();

    /**
     * @return the date at which this version of {@code Catalog} becomes effective
     * @throws CatalogApiException
     */
    public Date getEffectiveDate() throws CatalogApiException;

    /**
     * @return an array of supported {@code Currency}
     * @throws CatalogApiException
     */
    public Currency[] getCurrentSupportedCurrencies() throws CatalogApiException;

    /**
     * @return an array of supported {@code Product}
     * @throws CatalogApiException
     */
    public Collection<Product> getCurrentProducts() throws CatalogApiException;

    /**
     * @return an array of supported {@code Unit}
     * @throws CatalogApiException
     */
    public Unit[] getCurrentUnits() throws CatalogApiException;

    /**
     * @return an array of supported {@code Plan}
     * @throws CatalogApiException
     */
    public Collection<Plan> getCurrentPlans() throws CatalogApiException;

    /**
     * @param spec          the specification for the {@code Plan} to be used
     * @param overrides     the price override for each phase and for a specific currency
     * @return the {@code Plan}
     * @throws CatalogApiException if not such {@code Plan} can be found
     */
    public Plan createOrFindCurrentPlan(PlanSpecifier spec, PlanPhasePriceOverridesWithCallContext overrides) throws CatalogApiException;

    /**
     * @param name the name of the {@Plan}
     * @return the {@code Plan}
     * @throws CatalogApiException if not such {@code Plan} can be found
     */
    public Plan findCurrentPlan(String name) throws CatalogApiException;

    /**
     * @param name the name of the {@code Product}
     * @return the {@code Product}
     * @throws CatalogApiException if no such {@code Product} exists
     */
    public Product findCurrentProduct(String name) throws CatalogApiException;

    /**
     * @param name the name of the {@code PlanPhase}
     * @return the {@code PlanPhase}
     * @throws CatalogApiException if no such {@code PlanPhase} exists
     */
    public PlanPhase findCurrentPhase(String name) throws CatalogApiException;

    /**
     * @param name the name of the {@code PriceList}
     * @return the {@code PriceList}
     * @throws CatalogApiException if no such {@code PriceList} exists
     */
    public PriceList findCurrentPricelist(String name) throws CatalogApiException;

    // TODO private APIs ?

    public BillingActionPolicy planChangePolicy(PlanPhaseSpecifier from,
                                                PlanSpecifier to) throws CatalogApiException;

    public PlanChangeResult planChange(PlanPhaseSpecifier from,
                                       PlanSpecifier to) throws CatalogApiException;

    public BillingActionPolicy planCancelPolicy(PlanPhaseSpecifier planPhase) throws CatalogApiException;

    public PlanAlignmentCreate planCreateAlignment(PlanSpecifier specifier) throws CatalogApiException;

    public BillingAlignment billingAlignment(PlanPhaseSpecifier planPhase) throws CatalogApiException;

    public PlanAlignmentChange planChangeAlignment(PlanPhaseSpecifier from,
                                                   PlanSpecifier to) throws CatalogApiException;

    public boolean canCreatePlan(PlanSpecifier specifier) throws CatalogApiException;

    public List<Listing> getAvailableBasePlanListings() throws CatalogApiException;

    public List<Listing> getAvailableAddOnListings(String baseProductName, String priceListName) throws CatalogApiException;

    boolean compliesWithLimits(String phaseName, String unit, double value) throws CatalogApiException;

}
