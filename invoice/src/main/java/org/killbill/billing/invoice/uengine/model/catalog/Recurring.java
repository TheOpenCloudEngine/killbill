package org.killbill.billing.invoice.uengine.model.catalog;

import java.util.List;

/**
 * Created by uengine on 2017. 2. 3..
 */
public class Recurring {
    private String billingPeriod;
    private List<Price> recurringPrice;

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
}
