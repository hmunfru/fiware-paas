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

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;

/**
 * @author henar
 */
public interface RouterManager {

    /**
     * It adds a network to the router.
     */
    void addNetwork(ClaudiaData claudiaData, RouterInstance router, NetworkInstance network)
            throws InfrastructureException;

    /**
     * Create a router.
     * 
     * @param claudiaData
     * @param router
     * @param network
     * @throws InfrastructureException
     */

    void create(ClaudiaData claudiaData, RouterInstance router, NetworkInstance network, String region)
            throws InvalidEntityException, InfrastructureException;

    /**
     * Delete a router.
     * 
     * @param claudiaData
     * @param router
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     */
    void delete(ClaudiaData claudiaData, RouterInstance router, NetworkInstance network, String region)
            throws EntityNotFoundException, InvalidEntityException, InfrastructureException;

    /**
     * Retrieve all router created in the system.
     * 
     * @return the existent networks.
     */
    List<RouterInstance> findAll();

    /**
     * Load the router.
     * 
     * @return the router.
     */
    RouterInstance load(String name) throws EntityNotFoundException;

    /**
     * Update a router.
     * 
     * @param router
     * @return the router updated
     */
    RouterInstance update(RouterInstance router) throws InvalidEntityException;

}
