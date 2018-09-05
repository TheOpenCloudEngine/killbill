/*
 * Copyright 2010-2013 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
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

package org.killbill.billing.account.api;

import java.util.UUID;

import org.joda.time.DateTimeZone;
import org.killbill.billing.catalog.api.Currency;

/**
 * The interface <code>AccountData</code> specifies all the fields on the <code>Account</code>.
 */
public interface AccountData {

    /**
     * @return the account externalKey
     */
    public String getExternalKey();

    /**
     * The first and last name when that applies are combined into one name.
     *
     * @return the name of the account
     * @see AccountData#getFirstNameLength()
     */
    public String getName();

    /**
     * @return the length of the first name that can be extracted from the name
     */
    public Integer getFirstNameLength();

    /**
     * @return the primary account email
     */
    public String getEmail();

    /**
     * The billCycleDay should be interpreted in the account timezone.
     * The billCycleDay is used to determine when to bill an account
     * <p/>
     * Its is either set at account creation time or automatically set by the system.
     *
     * @return the billCycleDay for that account
     * @see org.killbill.billing.catalog.api.BillingAlignment
     */
    public Integer getBillCycleDayLocal();

    /**
     * @return the currency on the account
     */
    public Currency getCurrency();

    /**
     * @return the UUID of the current default paymentMethod
     */
    public UUID getPaymentMethodId();

    /**
     * @return the timezone for that account
     */
    public DateTimeZone getTimeZone();

    /**
     * @return the locale for that account
     */
    public String getLocale();

    /**
     * @return the address for that account (first line)
     */
    public String getAddress1();

    /**
     * @return the address for that account (second line)
     */
    public String getAddress2();

    /**
     * @return the company for that account
     */
    public String getCompanyName();

    /**
     * @return the city for that account
     */
    public String getCity();

    /**
     * @return the state or province for that account
     */
    public String getStateOrProvince();

    /**
     * @return the postal code for that account
     */
    public String getPostalCode();

    /**
     * @return the country for that account
     */
    public String getCountry();

    /**
     * @return the phone number for that account
     */
    public String getPhone();

    /**
     * @return whether or not that account was migrated into the system
     */
    public Boolean isMigrated();

    /**
     * @return whether or not that account will receive invoice notifications
     */
    public Boolean isNotifiedForInvoices();

    /**
     * @return the UUID of the parent Account
     */
    public UUID getParentAccountId();

    /**
     * @return whether or not the child account payment is delegated to Parent
     */
    public Boolean isPaymentDelegatedToParent();

    /**
     * @return the notes for that account
     */
    public String getNotes();
}
