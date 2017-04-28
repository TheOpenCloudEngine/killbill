package org.killbill.billing.catalog.uengine.model;

import java.math.BigDecimal;

/**
 * Created by uengine on 2017. 4. 28..
 */
public class Vendor {
    private String account_id;
    private BigDecimal ratio;

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }
}
