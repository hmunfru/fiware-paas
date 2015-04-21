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

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.SubNetwork;

/**
 * @author henar
 */
public interface SubNetworkManager {
    /**
     * Create a subnetwork.
     * 
     * @param subNetwork
     * @return the tierInstance created
     * @throws InfrastructureException
     * @throws AlreadyExistsEntityException
     */
    SubNetwork create(SubNetwork subNetwork) throws InvalidEntityException, AlreadyExistsEntityException;

    /**
     * Delete a subnetwork.
     * 
     * @param subNetwork
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     */
    void delete(SubNetwork subNetwork) throws EntityNotFoundException, InvalidEntityException;

    /**
     * Retrieve all Network created in the system.
     * 
     * @return the existent networks.
     */
    List<SubNetwork> findAll();

    /**
     * Load the SubNetwork.
     * 
     * @return the subnetwork.
     */
    SubNetwork load(String name, String vdc, String region) throws EntityNotFoundException;

    /**
     * Update a subNetwork.
     * 
     * @param subNetwork
     * @return the subnet updated
     */
    SubNetwork update(SubNetwork subNetwork) throws InvalidEntityException;

}
