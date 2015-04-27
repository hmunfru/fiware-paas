/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.paasmanager.claudia;

import java.util.List;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.model.Rule;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;

public interface FirewallingClient {

    String deploySecurityGroup(String region, String token, String vdc, SecurityGroup securityGroup)
            throws InfrastructureException;

    String deployRule(String region, String token, String vdc, Rule rule) throws InfrastructureException;

    void destroyRule(String region, String token, String vdc, Rule rule) throws InfrastructureException;

    void destroySecurityGroup(String region, String token, String vdc, SecurityGroup securityGroup)
            throws InfrastructureException;

    /**
     * Load a SecurityGroup from OpenStack.
     */
    SecurityGroup loadSecurityGroup(String region, String token, String vdc, String securityGroupId)
            throws EntityNotFoundException, InfrastructureException;

    /**
     * Loads all securityGroups associated to a certain vdc.
     */
    List<SecurityGroup> loadAllSecurityGroups(String region, String token, String vdc) throws InfrastructureException;
}
