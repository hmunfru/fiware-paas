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
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;

public interface TierManager {

    /**
     * Update an tier
     * 
     * @param tier
     * @return the tier updated
     */
    Tier update(Tier tier) throws InvalidEntityException;

    /**
     * Create a Tier
     * 
     * @param claudiaData
     * @param envName
     * @param tier
     * @return the created tier
     * @throws InvalidEntityException
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */
    Tier create(ClaudiaData claudiaData, String envName, Tier tier) throws InvalidEntityException,
            InfrastructureException, EntityNotFoundException, AlreadyExistsEntityException;

    /**
     * Find the Tier.
     * 
     * @param name
     *            the name
     * @return the environment
     * @throws EntityNotFoundException
     *             if the product instance does not exists
     */

    Tier load(String name, String vdc, String environmentNamae) throws EntityNotFoundException;

    /**
     * Load the Tier with Network Information
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
     * Delete a Tier
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
     * Find the Tiers by Environment
     * 
     * @param environment
     * @return list of tiers
     */
    List<Tier> findByEnvironment(Environment environment);

    /**
     * Find the Tier by a certain criteria
     * 
     * @param criteria
     * @return
     * @throws EntityNotFoundException
     */
    List<Tier> findByCriteria(TierSearchCriteria criteria) throws EntityNotFoundException;

    /**
     * Update tier
     * 
     * @param tierold
     * @param tiernew
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */
    void updateTier(ClaudiaData data, Tier tierold, Tier tiernew) throws InvalidEntityException,
            EntityNotFoundException, AlreadyExistsEntityException;

    /**
     * Load with networks, products, security groups from database
     * 
     * @param tier
     * @return
     */
    Tier loadComplete(Tier tier) throws EntityNotFoundException;
}
