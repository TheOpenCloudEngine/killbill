package org.killbill.billing.catalog.uengine.model.catalog;

import java.util.List;

import org.killbill.billing.catalog.uengine.model.Vendor;

/**
 * Created by uengine on 2017. 2. 3..
 */
public class Fixed {
    private List<Price> fixedPrice;

    public List<Price> getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(List<Price> fixedPrice) {
        this.fixedPrice = fixedPrice;
    }
}
