package org.killbill.billing.catalog.uengine.model.catalog;

import java.util.List;

import org.killbill.billing.catalog.uengine.model.Vendor;

/**
 * Created by uengine on 2017. 2. 3..
 */
public class Recurring {
    private String billingPeriod;
    private List<Price> recurringPrice;
    private List<Vendor> overrideVendors;

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public List<Price> getRecurringPrice() {
        return recurringPrice;
    }

    public void setRecurringPrice(List<Price> recurringPrice) {
        this.recurringPrice = recurringPrice;
    }

    public List<Vendor> getOverrideVendors() {
        return overrideVendors;
    }

    public void setOverrideVendors(List<Vendor> overrideVendors) {
        this.overrideVendors = overrideVendors;
    }
}
