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

package org.uengine.garuda.killbill.invoice.model;

import java.sql.Date;

/**
 * Created by uengine on 2017. 1. 25..
 */
public class ProductDaoVersionWithPlan {

    private String plan_name;
    private String product_id;
    private Long version;
    private String category;
    private Date effective_date;
    private String plans;

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(final String plan_name) {
        this.plan_name = plan_name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(final String product_id) {
        this.product_id = product_id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public Date getEffective_date() {
        return effective_date;
    }

    public void setEffective_date(final Date effective_date) {
        this.effective_date = effective_date;
    }

    public String getPlans() {
        return plans;
    }

    public void setPlans(final String plans) {
        this.plans = plans;
    }
}
