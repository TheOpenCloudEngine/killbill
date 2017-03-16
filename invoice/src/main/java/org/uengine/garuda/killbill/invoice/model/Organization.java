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
 * Created by uengine on 2016. 12. 28..
 */
public class Organization {
    private String id;
    private String name;
    private String tenant_id;
    private String tenant_external_key;
    private String tenant_api_key;
    private String tenant_api_secret;
    private String tenant_api_salt;
    private String fax;
    private String website;
    private String language_code;
    private String time_zone;
    private String date_format;
    private String currency;
    private String is_active;
    private String address1;
    private String address2;
    private String company_name;
    private String city;
    private String state_or_province;
    private String country;
    private String postal_code;
    private String phone;
    private String notes;
    private Date reg_dt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getTenant_external_key() {
        return tenant_external_key;
    }

    public void setTenant_external_key(String tenant_external_key) {
        this.tenant_external_key = tenant_external_key;
    }

    public String getTenant_api_key() {
        return tenant_api_key;
    }

    public void setTenant_api_key(String tenant_api_key) {
        this.tenant_api_key = tenant_api_key;
    }

    public String getTenant_api_secret() {
        return tenant_api_secret;
    }

    public void setTenant_api_secret(String tenant_api_secret) {
        this.tenant_api_secret = tenant_api_secret;
    }

    public String getTenant_api_salt() {
        return tenant_api_salt;
    }

    public void setTenant_api_salt(String tenant_api_salt) {
        this.tenant_api_salt = tenant_api_salt;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getDate_format() {
        return date_format;
    }

    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState_or_province() {
        return state_or_province;
    }

    public void setState_or_province(String state_or_province) {
        this.state_or_province = state_or_province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getReg_dt() {
        return reg_dt;
    }

    public void setReg_dt(Date reg_dt) {
        this.reg_dt = reg_dt;
    }
}
