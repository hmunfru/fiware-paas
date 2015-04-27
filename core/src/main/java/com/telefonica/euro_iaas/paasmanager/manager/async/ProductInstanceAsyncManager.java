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

package com.telefonica.euro_iaas.paasmanager.manager.async;

import java.util.List;
import java.util.Set;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.NotUniqueResultException;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;

/**
 * Defines the interface to work with async requests.
 * 
 * @author Jesus M Movilla
 */
public interface ProductInstanceAsyncManager {

    /**
     * Install a product release in a given vm.
     * 
     * @param tierInstance
     *            the tierInstance where instance will be running in
     * @param vdc
     *            the vdc where the instance will be installed
     * @param product
     *            the product to install
     * @param attributes
     *            the configuration
     * @param task
     *            the task which contains the information about the async execution
     * @param callback
     *            if not empty, contains the url where the result of the execution will be sent
     * @throws EntityNotFoundException 
     */
    void install(TierInstance tierInstance, ClaudiaData claudiaData, String envName, String vdc, ProductRelease product,
            Set<Attribute> attributes, Task task, String callback) throws EntityNotFoundException;

    /**
     * Configure an installed product
     * 
     * @param productInstance
     *            the installed product to configure
     * @param configuration
     *            the configuration parameters.
     * @param task
     *            the task which contains the information about the async execution
     * @param callback
     *            if not empty, contains the url where the result of the
     */
    // void configure(ProductInstance productInstance,
    // List<Attribute> configuration, Task task, String callback);

    /**
     * Upgrade a ProductInstance
     * 
     * @param productInstance
     *            the installed product to upgrade
     * @param productRelease
     *            the productRelease to upgrade to.
     * @param task
     *            the task which contains the information about the async execution
     * @param callback
     *            if not empty, contains the url where the result of the execution will be sent
     */
    // void upgrade(ProductInstance productInstance,
    // ProductRelease productRelease, Task task, String callback);

    /**
     * Uninstall a previously installed product Release
     * 
     * @param productInstance
     *            the candidate to uninstall
     * @param task
     *            the task which contains the information about the async execution
     * @param callback
     *            if not empty, contains the url where the result of the execution will be sent
     */
    void uninstall(ClaudiaData data, ProductInstance productInstance, Task task, String callback);

    /**
     * Find the ProductInstance using the given id.
     * 
     * @param vdc
     *            the vdc
     * @param name
     *            the productInstance name
     * @return the productInstance
     * @throws EntityNotFoundException
     *             if the product instance does not exists
     */
    ProductInstance load(String vdc, String name) throws EntityNotFoundException;

    /**
     * Find the ProductInstance that match with the given criteria.
     * 
     * @param criteria
     *            the search criteria
     * @return the productInstance
     * @throws EntityNotFoundException
     *             if the product instance does not exists
     * @throws NotUniqueResultException
     *             if there are more than a product that match with the given criteria
     */
    // ProductInstance loadByCriteria(ProductInstanceSearchCriteria criteria)
    // throws EntityNotFoundException, NotUniqueResultException;

    /**
     * Find the product instances that match with the given criteria.
     * 
     * @param criteria
     *            the search criteria
     * @return the list of elements that match with the criteria.
     */
    // List<ProductInstance> findByCriteria(ProductInstanceSearchCriteria
    // criteria);

}
