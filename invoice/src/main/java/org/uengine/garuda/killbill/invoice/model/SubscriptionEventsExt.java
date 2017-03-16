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
public class SubscriptionEventsExt {

    private Long id;
    private String subscription_id;
    private String event_type;
    private String user_type;
    private String plan_name;
    private String product_id;
    private Long version;
    private String account_id;
    private String organization_id;
    private String tenant_id;
    private Long account_record_id;
    private Long tenant_record_id;
    private Date reg_dt;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getSubscription_id() {
        return subscription_id;
    }

    public void setSubscription_id(final String subscription_id) {
        this.subscription_id = subscription_id;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(final String event_type) {
        this.event_type = event_type;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(final String user_type) {
        this.user_type = user_type;
    }

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

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(final String account_id) {
        this.account_id = account_id;
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

    public Long getAccount_record_id() {
        return account_record_id;
    }

    public void setAccount_record_id(final Long account_record_id) {
        this.account_record_id = account_record_id;
    }

    public Long getTenant_record_id() {
        return tenant_record_id;
    }

    public void setTenant_record_id(final Long tenant_record_id) {
        this.tenant_record_id = tenant_record_id;
    }

    public Date getReg_dt() {
        return reg_dt;
    }

    public void setReg_dt(final Date reg_dt) {
        this.reg_dt = reg_dt;
    }
}
