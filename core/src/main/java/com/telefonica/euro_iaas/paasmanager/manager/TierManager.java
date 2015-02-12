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
import com.telefonica.euro_iaas.paasmanager.exception.InvalidSecurityGroupRequestException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;

public interface TierManager {

    /**
     * Update an tierInstance
     * 
     * @param tierInstance
     * @return the tierInstance created
     */
    Tier update(Tier tier) throws InvalidEntityException;

    Tier create(ClaudiaData claudiaData, String envName, Tier tier) throws InvalidEntityException,
            InvalidSecurityGroupRequestException, InfrastructureException, EntityNotFoundException, AlreadyExistsEntityException;

    /**
     * Find the Environment using the given name.
     * 
     * @param name
     *            the name
     * @return the environment
     * @throws EntityNotFoundException
     *             if the product instance does not exists
     */

    Tier load(String name, String vdc, String environmentNamae) throws EntityNotFoundException;
    
    /**
     * 
     * @param name
     * @param vdc
     * @param environmentNamae
     * @return
     * @throws EntityNotFoundException
     */
    Tier loadTierWithNetworks(String name, String vdc, String environmentNamae) throws EntityNotFoundException;

    /**
     * Find de Tier with ProductRelease and metadata info.
     * 
     * @return a valid Tier object with productRelease list and metadata
     * @throws EntityNotFoundException
     */
    Tier loadTierWithProductReleaseAndMetadata(String tierName, String environmentName, String vdc)
            throws EntityNotFoundException;

    /**
     * Retrieve all Environment created in the system.
     * 
     * @return the existent environments.
     */
    List<Tier> findAll();

    /**
     * Delete a TierInstance
     * 
     * @param claudiaData
     * @param tier
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     */
    void delete(ClaudiaData claudiaData, Tier tier) throws EntityNotFoundException, InvalidEntityException,
            InfrastructureException;

    /**
     * Find the TierInstance by Environment
     * 
     * @param environment
     * @return
     */
    List<Tier> findByEnvironment(Environment environment);

    /**
     * Find the Tier Instance by a certain criteria
     * 
     * @param criteria
     * @return
     * @throws EntityNotFoundException
     */
    List<Tier> findByCriteria(TierSearchCriteria criteria) throws EntityNotFoundException;

    /**
     * Add a Security Group to a ProductRelease
     * 
     * @param claudiaData
     * @param tier
     * @param productRelease
     * @throws InfrastructureException
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     */
    void addSecurityGroupToProductRelease(ClaudiaData claudiaData, Tier tier, ProductRelease productRelease)
            throws InvalidEntityException, AlreadyExistsEntityException, InfrastructureException;
    
    /**
     * Update tier
     * @param tierold
     * @param tiernew
     * @throws InvalidEntityException
     */
    void updateTier(ClaudiaData data, Tier tierold, Tier tiernew) throws InvalidEntityException, EntityNotFoundException, AlreadyExistsEntityException;

    /**
     * Load with networks, products, security groups from database
     * @param tier
     * @return
     */
    Tier loadComplete(Tier tier) throws EntityNotFoundException;
}
