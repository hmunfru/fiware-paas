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

package com.telefonica.euro_iaas.paasmanager.manager;

import java.util.List;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentRequestException;
import com.telefonica.euro_iaas.paasmanager.model.Rule;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;

public interface SecurityGroupManager {

    /**
     * Create an securityGroup.
     * 
     * @param securityGroup
     * @return the securityGroup.
     */
    SecurityGroup create(String region, String token, String vdc, SecurityGroup securityGroup)
            throws InvalidEntityException, InvalidEnvironmentRequestException, AlreadyExistsEntityException,
            InfrastructureException;

    /**
     * Destroy a previously creted securityGroup.
     * 
     * @param securityGroup
     *            the candidate to securityGroup
     */
    void destroy(String region, String token, String vdc, SecurityGroup securityGroup) throws InvalidEntityException,
            InfrastructureException;

    /**
     * Find the securityGroup using the given name.
     * 
     * @param name
     *            the name
     * @return the securityGroup
     * @throws EntityNotFoundException
     *             if the product instance does not exists
     */
    SecurityGroup load(String name) throws EntityNotFoundException;

    /**
     * Add rule to security group.
     * 
     * @param region
     * @param token
     * @param vdc
     * @param securityGroup
     * @param rule
     * @throws InvalidEntityException
     * @throws AlreadyExistsEntityException
     * @throws InfrastructureException
     */
    void addRule(String region, String token, String vdc, SecurityGroup securityGroup, Rule rule)
            throws InvalidEntityException, AlreadyExistsEntityException, InfrastructureException;

    /**
     * Retrieve all Environment created in the system.
     * 
     * @return the existent environments.
     */
    List<SecurityGroup> findAll();

    /**
     * Updates the securityGroup.
     * 
     * @param securityGroup
     * @return
     */
    SecurityGroup update(SecurityGroup securityGroup) throws InvalidEntityException;

}
