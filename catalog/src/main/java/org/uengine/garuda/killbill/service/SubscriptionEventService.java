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

package org.uengine.garuda.killbill.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.uengine.garuda.killbill.model.ProductDaoVersionWithPlan;
import org.uengine.garuda.killbill.model.SubscriptionEventsExt;
import org.uengine.garuda.killbill.mybatis.MyBatisConnectionFactory;
import org.uengine.garuda.killbill.repository.SubscriptionEventRepository;

/**
 * Created by uengine on 2017. 2. 6..
 */
public class SubscriptionEventService {

    public SqlSession getSqlSessionFactory() {
        return MyBatisConnectionFactory.getSqlSessionFactory()
                                       .openSession(true);
    }

    public SubscriptionEventsExt selectById(Long id) {
        SqlSession sessionFactory = getSqlSessionFactory();
        try {
            SubscriptionEventRepository mapper = sessionFactory.getMapper(SubscriptionEventRepository.class);
            return mapper.selectById(id);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }

    public List<ProductDaoVersionWithPlan> selectVersionReferencedByAccount(Long account_record_id, Long tenant_record_id) {
        Map map = new HashMap();
        map.put("account_record_id", account_record_id);
        map.put("tenant_record_id", tenant_record_id);

        SqlSession sessionFactory = getSqlSessionFactory();
        try {
            SubscriptionEventRepository mapper = sessionFactory.getMapper(SubscriptionEventRepository.class);
            return mapper.selectVersionReferencedByAccount(map);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }
}
