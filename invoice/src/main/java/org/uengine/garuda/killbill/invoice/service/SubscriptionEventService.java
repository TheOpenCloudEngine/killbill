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

package org.uengine.garuda.killbill.invoice.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.uengine.garuda.killbill.invoice.model.ProductDaoVersion;
import org.uengine.garuda.killbill.invoice.model.SubscriptionEventsExt;
import org.uengine.garuda.killbill.invoice.mybatis.InvoiceConnectionFactory;
import org.uengine.garuda.killbill.invoice.repository.SubscriptionEventRepository;

/**
 * Created by uengine on 2017. 2. 6..
 */
public class SubscriptionEventService {

    public SqlSession getSqlSessionFactory() {
        return InvoiceConnectionFactory.getSqlSessionFactory()
                                       .openSession(true);
    }

    public SubscriptionEventsExt selectLastSubscriptionExt(String subscription_id) {
        SqlSession sessionFactory = getSqlSessionFactory();
        try {
            SubscriptionEventRepository mapper = sessionFactory.getMapper(SubscriptionEventRepository.class);
            return mapper.selectLastSubscriptionExt(subscription_id);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }

    public List<ProductDaoVersion> selectVersionByProductId(String product_id) {
        SqlSession sessionFactory = getSqlSessionFactory();
        try {
            SubscriptionEventRepository mapper = sessionFactory.getMapper(SubscriptionEventRepository.class);
            return mapper.selectVersionByProductId(product_id);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }
}
