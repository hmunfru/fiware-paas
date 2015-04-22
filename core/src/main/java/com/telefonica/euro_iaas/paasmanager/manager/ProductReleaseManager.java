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
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;

/**
 * @author jesus.movilla
 */
public interface ProductReleaseManager {

    /**
     * Find the ProductRelease using the given id.
     * 
     * @param name
     *            the product identifier
     * @return the productRelease
     * @throws EntityNotFoundException
     *             if the product instance does not exists
     */
    ProductRelease load(String name, ClaudiaData data) throws EntityNotFoundException;

    /**
     * Request product release to SDC and creates.
     * 
     * @param name
     * @param data
     * @return
     * @throws EntityNotFoundException
     */
    ProductRelease loadFromSDCAndCreate(String name, ClaudiaData data) throws EntityNotFoundException;

    /**
     * Retrieve all ProductRelease created in the system.
     * 
     * @return the existent product instances.
     */
    List<ProductRelease> findAll();

    /**
     * Retrieve a Product release for a given product and version.
     * 
     * @param product
     *            the product
     * @param version
     *            the version
     * @return the product release that match with the criteria
     * @throws EntityNotFoundException
     *             if the product release does not exists
     */
    ProductRelease load(String productName, String productVersion) throws EntityNotFoundException;

    /**
     * @param productName
     * @param productVersion
     * @return
     * @throws EntityNotFoundException
     */

    ProductRelease loadWithMetadata(String name) throws EntityNotFoundException;

    ProductRelease create(ProductRelease tomcat6) throws InvalidEntityException, AlreadyExistsEntityException;

    /**
     * Retrieve a Product release for a given product and version.
     * 
     * @param productRelease
     *            the productRelease to be updated
     * @return the product release updated
     * @throws InvalidEntityException
     *             if the product release object is not valid
     */
    ProductRelease update(ProductRelease productRelease) throws InvalidEntityException;
}
