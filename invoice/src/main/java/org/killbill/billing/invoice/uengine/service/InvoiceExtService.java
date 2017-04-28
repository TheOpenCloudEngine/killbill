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

package org.killbill.billing.invoice.uengine.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.killbill.billing.invoice.uengine.model.NotificationConfig;
import org.killbill.billing.invoice.uengine.model.Organization;
import org.killbill.billing.invoice.uengine.model.ProductDaoVersion;
import org.killbill.billing.invoice.uengine.model.SubscriptionEventsExt;
import org.killbill.billing.invoice.uengine.model.Template;
import org.killbill.billing.invoice.uengine.mybatis.InvoiceConnectionFactory;
import org.killbill.billing.invoice.uengine.repository.InvoiceExtRepository;

/**
 * Created by uengine on 2017. 2. 6..
 */
public class InvoiceExtService {

    public SqlSession getSqlSessionFactory() {
        return InvoiceConnectionFactory.getSqlSessionFactory()
                                       .openSession(true);
    }

    public NotificationConfig selectConfigByOrgId(String organization_id) {
        SqlSession sessionFactory = getSqlSessionFactory();
        try {
            InvoiceExtRepository mapper = sessionFactory.getMapper(InvoiceExtRepository.class);
            return mapper.selectConfigByOrgId(organization_id);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }

    //selectByOrgIdAndType
    public List<Template> selectByOrgIdAndType(String organization_id, String notification_type) {
        SqlSession sessionFactory = getSqlSessionFactory();
        try {
            InvoiceExtRepository mapper = sessionFactory.getMapper(InvoiceExtRepository.class);
            Map map = new HashMap();
            map.put("organization_id", organization_id);
            map.put("notification_type", notification_type);
            return mapper.selectByOrgIdAndType(map);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }

    public Organization selectOrganizationFromAccountId(String account_id) {
        SqlSession sessionFactory = getSqlSessionFactory();
        try {
            InvoiceExtRepository mapper = sessionFactory.getMapper(InvoiceExtRepository.class);
            return mapper.selectOrganizationFromAccountId(account_id);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }

    public SubscriptionEventsExt selectLastSubscriptionExt(String subscription_id) {
        SqlSession sessionFactory = getSqlSessionFactory();
        try {
            InvoiceExtRepository mapper = sessionFactory.getMapper(InvoiceExtRepository.class);
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
            InvoiceExtRepository mapper = sessionFactory.getMapper(InvoiceExtRepository.class);
            return mapper.selectVersionByProductId(product_id);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }
}
