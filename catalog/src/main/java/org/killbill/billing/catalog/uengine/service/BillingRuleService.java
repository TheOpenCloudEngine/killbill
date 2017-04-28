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

package org.killbill.billing.catalog.uengine.service;

import org.apache.ibatis.session.SqlSession;
import org.killbill.billing.catalog.uengine.model.BillingRule;
import org.killbill.billing.catalog.uengine.mybatis.MyBatisConnectionFactory;
import org.killbill.billing.catalog.uengine.repository.BillingRuleRepository;

/**
 * Created by uengine on 2017. 2. 6..
 */
public class BillingRuleService {

    public SqlSession getSqlSessionFactory() {
        return MyBatisConnectionFactory.getSqlSessionFactory()
                                       .openSession(true);
    }

    public BillingRule selectRuleByTenantRecordId(Long id) {
        SqlSession sessionFactory = getSqlSessionFactory();
        try {
            BillingRuleRepository mapper = sessionFactory.getMapper(BillingRuleRepository.class);
            return mapper.selectRuleByTenantRecordId(id);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }
}
