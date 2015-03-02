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
import java.util.Set;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidProductInstanceRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.NotUniqueResultException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductReconfigurationException;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ProductInstanceSearchCriteria;

/**
 * Defines the operations the system shall be able to do with Products
 * 
 * @author Jesus M. Movilla
 */
public interface ProductInstanceManager {

    /**
     * Install a list of products in a given vm.
     * 
     * @param vm
     * @param org
     * @param vdc
     * @param product
     * @param set
     * @return
     * @throws ProductInstallatorException
     * @throws InvalidEntityException
     * @throws NotUniqueResultException
     * @throws InvalidEntityException
     */
    ProductInstance install(TierInstance tierInstance, ClaudiaData claudiaData,
            EnvironmentInstance environmentInstance, ProductRelease product)
            throws ProductInstallatorException, InvalidProductInstanceRequestException,
            InvalidEntityException;

    /**
     * Configure an installed product
     * 
     * @param productInstance
     *            the installed product to configure
     * @param configuration
     *            the configuration parameters.
     * @return the configured product.
     * @throws FSMViolationException
     *             if try to make a forbidden transition
     */
    /*
     * ProductInstance configure(ProductInstance productInstance,
     * List<Attribute> configuration) throws NodeExecutionException,
     * FSMViolationException ;
     */

    /**
     * Upgrade a ProductInstance
     * 
     * @param productInstance
     *            the installed product to upgrade
     * @param productRelease
     *            the productRelease to upgrade to.
     * @return the productInstance upgraded.
     * @throws NotTransitableException
     *             if the selected version is not compatible with the installed
     *             product
     * @throws NodeExecutionException
     *             if any error happen during the upgrade in node
     * @throws FSMViolationException
     *             if try to make a forbidden transition
     * @throws ApplicationIncompatibleException
     *             if any application which is installed on the upgraded product
     *             is not compatible with the new version
     */
    /*
     * ProductInstance upgrade(ProductInstance productInstance, ProductRelease
     * productRelease) throws NotTransitableException, NodeExecutionException,
     * ApplicationIncompatibleException, FSMViolationException;
     */

    /**
     * Uninstall a previously installed product.
     * 
     * @param productInstance
     *            the candidate to uninstall
     * @throws NodeExecutionException
     *             if any error happen during the uninstallation in node
     * @throws ApplicationInstalledException
     *             if the product has some applications which depend on it
     * @throws FSMViolationException
     *             if try to make a forbidden transition
     */
    void uninstall(ClaudiaData claudiaData, ProductInstance productInstance) throws ProductInstallatorException;

    // throws NodeExecutionException, ApplicationInstalledException,
    // FSMViolationException;

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

    ProductInstance load(String name) throws EntityNotFoundException;

    /**
     * Find the ProductInstance that match with the given criteria.
     * 
     * @param criteria
     *            the search criteria
     * @return the productInstance
     * @throws EntityNotFoundException
     *             if the product instance does not exists
     * @throws NotUniqueResultException
     *             if there are more than a product that match with the given
     *             criteria
     */
    ProductInstance loadByCriteria(ProductInstanceSearchCriteria criteria) throws EntityNotFoundException,
            NotUniqueResultException;

    /**
     * Retrieve all ProductInstance created in the system.
     * 
     * @return the existent product instances.
     */
    List<ProductInstance> findAll();

    /**
     * Find the product instances that match with the given criteria.
     * 
     * @param criteria
     *            the search criteria
     * @return the list of elements that match with the criteria.
     */
    List<ProductInstance> findByCriteria(ProductInstanceSearchCriteria criteria);

    /**
     * Updates a product instance and persist it in the database∫
     * 
     * @param productInstance
     * @return
     * @throws InvalidProductInstanceRequestException
     */
    // ProductInstance update(ProductInstance productInstance);

    ProductInstance create(ClaudiaData data, ProductInstance productInstance) throws InvalidEntityException,
            AlreadyExistsEntityException, InvalidProductInstanceRequestException;

    void remove(ProductInstance productInstance);

    void configure(ClaudiaData claudiaData, ProductInstance productInstance, List<Attribute> properties)
            throws InvalidEntityException, AlreadyExistsEntityException, ProductInstallatorException,
            EntityNotFoundException, ProductReconfigurationException;

}
