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
package org.uengine.garuda.repository;

import java.util.List;
import java.util.Map;

import org.uengine.garuda.model.ProductDaoVersionWithPlan;
import org.uengine.garuda.model.SubscriptionEventsExt;

/**
 * @author Seungpil PARK
 */
public interface SubscriptionEventRepository {

    String NAMESPACE = SubscriptionEventRepository.class.getName();

    List<Map> selectSubscriptionCountByProductVersion(Map map);

    List<ProductDaoVersionWithPlan> selectVersionReferencedByAccount(Map map);

    SubscriptionEventsExt selectById(Long id);

    SubscriptionEventsExt insert(SubscriptionEventsExt subscriptionEventsExt);

    int delete(Long id);
}
