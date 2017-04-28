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

package org.killbill.billing.catalog.uengine.model;

import java.sql.Date;

/**
 * Created by uengine on 2017. 1. 25..
 */
public class ProductDaoVersion {

    private Long id;
    private String product_id;
    private Long version;
    private Date effective_date;
    private String organization_id;
    private String tenant_id;
    private String plans;
    private Date reg_dt;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public Date getEffective_date() {
        return effective_date;
    }

    public void setEffective_date(final Date effective_date) {
        this.effective_date = effective_date;
    }

    public String getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(final String organization_id) {
        this.organization_id = organization_id;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(final String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getPlans() {
        return plans;
    }

    public void setPlans(final String plans) {
        this.plans = plans;
    }

    public Date getReg_dt() {
        return reg_dt;
    }

    public void setReg_dt(final Date reg_dt) {
        this.reg_dt = reg_dt;
    }
}
