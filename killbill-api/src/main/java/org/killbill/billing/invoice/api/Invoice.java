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

package org.killbill.billing.invoice.api;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.util.entity.Entity;

public interface Invoice extends Entity {

    /**
     * @param item the invoice ietm to add
     * @return true if successful
     */
    boolean addInvoiceItem(InvoiceItem item);

    /**
     * @param items the list of ietms to add
     * @return true is successful
     */
    boolean addInvoiceItems(Collection<InvoiceItem> items);

    /**
     * @return the list of items on that invoice
     */
    List<InvoiceItem> getInvoiceItems();

    /**
     * @param clazz the filter class for the items
     * @param <T>   a InvoiceItem type
     * @return the list of invoice ietms on that invoice for that type
     */
    public <T extends InvoiceItem> List<InvoiceItem> getInvoiceItems(Class<T> clazz);

    /**
     * @return the number of items on that invoice
     */
    int getNumberOfItems();

    /**
     * @param payment the successful payment for that invoice
     * @return true if we were able to add the payment
     */
    boolean addPayment(InvoicePayment payment);

    /**
     * @param payments the list of payments to add on that invoice
     * @return true if we were able to add the payments
     */
    boolean addPayments(Collection<InvoicePayment> payments);

    /**
     * @return the list of payments associated with that invoice
     */
    List<InvoicePayment> getPayments();

    /**
     * @return the number of payments on that invoice
     */
    int getNumberOfPayments();

    /**
     * @return the accountId
     */
    UUID getAccountId();

    /**
     * @return the invoice number
     */
    Integer getInvoiceNumber();

    /**
     * @return the day the invoice was generated, in the account timezone
     */
    LocalDate getInvoiceDate();

    /**
     * The target day is the latest day to consider for billing events.
     *
     * @return the target day in the account timezone
     */
    LocalDate getTargetDate();

    /**
     * @return the currency associated with that invoice
     */
    Currency getCurrency();

    /**
     * @return the sum of all successful payment amounts for that invoice
     */
    BigDecimal getPaidAmount();

    /**
     * @return the sum of all EXTERNAL_CHARGE, FIXED and RECURRING item amounts when the invoice was created
     */
    BigDecimal getOriginalChargedAmount();

    /**
     * @return the sum of all charges (EXTERNAL_CHARGE, FIXED, RECURRING) and adjustments (item or invoice adjustment) amounts
     */
    BigDecimal getChargedAmount();

    /**
     * @return the sum of all CBA_ADJ items
     */
    BigDecimal getCreditedAmount();

    /**
     * @return the sum of all refunds and chargebacks for payments associated with that invoice
     */
    BigDecimal getRefundedAmount();

    /**
     * @return the current balance on that invoice
     */
    BigDecimal getBalance();

    /**
     * @return true if this is a migration invoice
     */
    boolean isMigrationInvoice();

    /**
     * @return the current status of the invoice
     */
    InvoiceStatus getStatus();

    /**
     * @return true if this is a parent invoice
     */
    boolean isParentInvoice();
}
