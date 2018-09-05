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

package org.killbill.billing.util.tag;

import java.util.List;

import org.killbill.billing.ObjectType;
import org.killbill.billing.util.entity.Entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

// TODO: needs to surface created date, created by
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface TagDefinition extends Entity {

    String getName();

    String getDescription();

    Boolean isControlTag();

    List<ObjectType> getApplicableObjectTypes();
}
