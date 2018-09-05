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

package org.killbill.billing.catalog.api;

import org.killbill.billing.ErrorCode;

public class CurrencyValueNull extends CatalogApiException {

    private static final long serialVersionUID = 1L;

    public CurrencyValueNull(final Throwable cause, final Object... args) {
        super(cause, ErrorCode.CAT_PRICE_VALUE_NULL_FOR_CURRENCY, args);
    }

    public CurrencyValueNull(final Object... args) {
        super(ErrorCode.CAT_PRICE_VALUE_NULL_FOR_CURRENCY, args);
    }

}
