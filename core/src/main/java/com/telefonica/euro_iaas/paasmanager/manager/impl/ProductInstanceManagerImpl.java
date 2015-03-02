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

package com.telefonica.euro_iaas.paasmanager.manager.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.ProductInstanceDao;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidProductInstanceRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.NotUniqueResultException;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductReconfigurationException;
import com.telefonica.euro_iaas.paasmanager.installator.ProductInstallator;
import com.telefonica.euro_iaas.paasmanager.manager.ProductInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.ProductReleaseManager;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ProductInstanceSearchCriteria;

public class ProductInstanceManagerImpl implements ProductInstanceManager {

    private ProductInstanceDao productInstanceDao;
    private ProductInstallator productInstallator;
    private ProductReleaseManager productReleaseManager;

    private static Logger log = LoggerFactory.getLogger(ProductInstanceManagerImpl.class);

    public ProductInstance install(TierInstance tierInstance, ClaudiaData claudiaData,
            EnvironmentInstance environmentInstance, ProductRelease productRelease)
            throws ProductInstallatorException, InvalidProductInstanceRequestException,
            InvalidEntityException {
        log.info("Installing software " + productRelease.getProduct() + " in tier Instance " + tierInstance.getName()
                + " in vdc " + claudiaData.getVdc());

        ProductInstance productInstance = null;
        try {
            productInstance = productInstallator.install(claudiaData, environmentInstance, tierInstance,
                    productRelease);
            if (productInstance.getVdc() == null) {
                productInstance.setVdc(claudiaData.getVdc());
            }
            productInstance = create(claudiaData, productInstance);
        } catch (AlreadyExistsEntityException e) {
            log.error("The product instance " + productInstance.getName() + " already exists " + e.getMessage());
            throw new InvalidProductInstanceRequestException("Error to i" + e.getMessage(), e);
        } catch (OpenStackException e) {
            String errorMessage = "Error to configure the product " + e.getMessage();
            new ProductInstallatorException(errorMessage);
        }

        return productInstance;
    }

    public void uninstall(ClaudiaData claudiaData, ProductInstance productInstance) throws ProductInstallatorException {
        log.info("UnInstalling software " + productInstance.getProductRelease().getProduct());
        try {
            productInstallator.uninstall(claudiaData, productInstance);
        } catch (OpenStackException e) {
            String errorMessage = "Error to configure the product " + e.getMessage();
            new ProductInstallatorException(errorMessage);
        }

    }

    public ProductInstance load(String vdc, String name) throws EntityNotFoundException {
        return productInstanceDao.load(name);

    }

    public ProductInstance loadByCriteria(ProductInstanceSearchCriteria criteria) throws EntityNotFoundException,
            NotUniqueResultException {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ProductInstance> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ProductInstance> findByCriteria(ProductInstanceSearchCriteria criteria) {
        return productInstanceDao.findByCriteria(criteria);
    }

    // //////////// I.O.C /////////////
    /**
     * @param productInstanceDao
     *            the productInstanceDao to set
     */
    public void setProductInstanceDao(ProductInstanceDao productInstanceDao) {
        this.productInstanceDao = productInstanceDao;
    }

    /**
     * the productReleaseDao to set
     */
    public void setProductReleaseManager(ProductReleaseManager productReleaseManager) {
        this.productReleaseManager = productReleaseManager;
    }

    /**
     * @param productInstallator
     *            the productInstallator to set
     */
    public void setProductInstallator(ProductInstallator productInstallator) {
        this.productInstallator = productInstallator;
    }

    public ProductInstance load(String name) throws EntityNotFoundException {
        return productInstanceDao.load(name);
    }

    public void remove(ProductInstance productInstance) {
        productInstanceDao.remove(productInstance);

    }

    public ProductInstance create(ClaudiaData data, ProductInstance productInstance) throws InvalidEntityException,
            AlreadyExistsEntityException, InvalidProductInstanceRequestException {
        ProductRelease productRelease = null;
        try {

            productRelease = productReleaseManager.load(productInstance.getProductRelease().getProduct() + "-"
                    + productInstance.getProductRelease().getVersion(), data);
            productInstance.setProductRelease(productRelease);
        } catch (EntityNotFoundException e) {
            String errorMessage = "The Product Release Object " + productRelease.getId() + " is " + "NOT valid";
            throw new InvalidProductInstanceRequestException(errorMessage);
        }

        try {
            productInstance = productInstanceDao.load(productInstance.getName());
        } catch (EntityNotFoundException e) {
            try {

                productInstance = productInstanceDao.create(productInstance);

            } catch (AlreadyExistsEntityException aee) {
                String errorMessage = "The ProductRelease Object " + productInstance.getName()
                        + " already exist in Database";
                throw new InvalidProductInstanceRequestException(errorMessage);
            } catch (Exception aee) {
                String errorMessage = "The ProductRelease Object " + productInstance.getName()
                        + " already exist in Database " + aee.getMessage();
                throw new InvalidProductInstanceRequestException(errorMessage);
            }
        }

        return productInstance;
    }

    public void configure(ClaudiaData claudiaData, ProductInstance productInstance, List<Attribute> properties)
            throws InvalidEntityException, AlreadyExistsEntityException, ProductInstallatorException,
            EntityNotFoundException, ProductReconfigurationException {
        log.info("Configuring software " + productInstance.getProductRelease().getProduct() + " "
                + productInstance.getName());
        try {
            productInstallator.configure(claudiaData, productInstance, properties);
        } catch (OpenStackException e) {
            String errorMessage = "Error to configure the product " + e.getMessage();
            new ProductInstallatorException(errorMessage);
        }

    }

}
