package org.killbill.billing.invoice.uengine.model.catalog;

import java.util.List;

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
