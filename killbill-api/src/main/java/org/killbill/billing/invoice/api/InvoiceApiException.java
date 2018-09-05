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

import org.killbill.billing.BillingExceptionBase;
import org.killbill.billing.ErrorCode;

public class InvoiceApiException extends BillingExceptionBase {

    public InvoiceApiException(final BillingExceptionBase cause) {
        super(cause);
    }

    public InvoiceApiException(final Throwable cause, final int code, final String msg) {
        super(cause, code, msg);
    }

    public InvoiceApiException(final Throwable cause, final ErrorCode code, final Object... args) {
        super(cause, code, args);
    }

    public InvoiceApiException(final ErrorCode code, final Object... args) {
        super(code, args);
    }
}
