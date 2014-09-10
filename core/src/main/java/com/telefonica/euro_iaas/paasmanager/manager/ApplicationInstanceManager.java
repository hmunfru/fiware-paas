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
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductReleaseNotFoundException;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ApplicationInstanceSearchCriteria;

/**
 * Manager class of the Application Instance.
 */
public interface ApplicationInstanceManager {

    /**
     * Install a list of applications in a given environment.
     * @param data
     * @param environmentInstance
     * @param application
     * @return
     * @throws ProductReleaseNotFoundException
     * @throws InvalidEntityException
     * @throws AlreadyExistsEntityException
     * @throws ProductInstallatorException
     */
    ApplicationInstance install(ClaudiaData data,  EnvironmentInstance environmentInstance,
            ApplicationRelease application) throws ProductReleaseNotFoundException, InvalidEntityException,
            AlreadyExistsEntityException,  ProductInstallatorException;

    /**
     * Find all ApplicationInstances.
     * 
     * @return the applicationInstances
     */
    List<ApplicationInstance> findAll();

    /**
     * Find the application instances that match with the given criteria.
     * 
     * @param criteria
     *            the search criteria
     * @return the list of elements that match with the criteria.
     */
    List<ApplicationInstance> findByCriteria(ApplicationInstanceSearchCriteria criteria);

    /**
     * Load an applicationInstance
     * 
     * @param vdc
     * @param name
     * @return the applicationInstance
     */
    ApplicationInstance load(String vdc, String name) throws EntityNotFoundException;

    /**
     * UnInstall an list in a given environment
     * 
     * @param org
     *            the org where the instance will be uninstalled
     * @param vdc
     *            the vdc where the instance will be uninstalled
     * @param environmentInstanceName
     *            the environmentInstance where the instance will be installed
     * @param application
     *            the application to install
     * @return the of installed product.
     * @throws InvalidEntityException
     * @throws AlreadyExistsEntityException
     * @throws ApplicationTypeNotFoundException
     */
    void uninstall(ClaudiaData data, EnvironmentInstance environmentInstanceName,
            ApplicationInstance applicationInstance) throws ProductInstallatorException;

}
