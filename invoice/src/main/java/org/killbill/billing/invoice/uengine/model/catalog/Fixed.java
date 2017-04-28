package org.killbill.billing.invoice.uengine.model.catalog;

import java.util.List;

import org.killbill.billing.invoice.uengine.model.Vendor;

/**
 * Created by uengine on 2017. 2. 3..
 */
public class Fixed {
    private List<Price> fixedPrice;
    private List<Vendor> overrideVendors;

    public List<Price> getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(List<Price> fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public List<Vendor> getOverrideVendors() {
        return overrideVendors;
    }

    public void setOverrideVendors(List<Vendor> overrideVendors) {
        this.overrideVendors = overrideVendors;
    }
}
