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

package org.killbill.billing.invoice.uengine.model.catalog;

import java.util.List;

import org.killbill.billing.invoice.uengine.model.Vendor;

/**
 * Created by uengine on 2017. 2. 2..
 */
public class Plan {

    private String name;
    private String display_name;
    private String is_active;
    private List<Phase> initialPhases;
    private Phase finalPhase;
    private Long number_of_subscriptions_referenced_by_version;
    private Long number_of_subscriptions;
    private List<Vendor> overwriteVendors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public List<Phase> getInitialPhases() {
        return initialPhases;
    }

    public void setInitialPhases(List<Phase> initialPhases) {
        this.initialPhases = initialPhases;
    }

    public Phase getFinalPhase() {
        return finalPhase;
    }

    public void setFinalPhase(Phase finalPhase) {
        this.finalPhase = finalPhase;
    }

    public Long getNumber_of_subscriptions_referenced_by_version() {
        return number_of_subscriptions_referenced_by_version;
    }

    public void setNumber_of_subscriptions_referenced_by_version(Long number_of_subscriptions_referenced_by_version) {
        this.number_of_subscriptions_referenced_by_version = number_of_subscriptions_referenced_by_version;
    }

    public Long getNumber_of_subscriptions() {
        return number_of_subscriptions;
    }

    public void setNumber_of_subscriptions(Long number_of_subscriptions) {
        this.number_of_subscriptions = number_of_subscriptions;
    }

    public List<Vendor> getOverwriteVendors() {
        return overwriteVendors;
    }

    public void setOverwriteVendors(final List<Vendor> overwriteVendors) {
        this.overwriteVendors = overwriteVendors;
    }
}
