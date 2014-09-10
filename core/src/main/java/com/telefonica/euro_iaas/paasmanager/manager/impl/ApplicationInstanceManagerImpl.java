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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.ApplicationInstanceDao;
import com.telefonica.euro_iaas.paasmanager.dao.ApplicationReleaseDao;
import com.telefonica.euro_iaas.paasmanager.dao.ArtifactDao;
import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductReleaseNotFoundException;
import com.telefonica.euro_iaas.paasmanager.installator.ProductInstallator;
import com.telefonica.euro_iaas.paasmanager.manager.ApplicationInstanceManager;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.Artifact;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ApplicationInstanceSearchCriteria;

/**
 * Application Instance Manager operations: install.
 * 
 * @author Henar Munoz
 */
public class ApplicationInstanceManagerImpl implements ApplicationInstanceManager {

    private ApplicationReleaseDao applicationReleaseDao;
    private ApplicationInstanceDao applicationInstanceDao;
    private ProductReleaseDao productReleaseDao;
    private ArtifactDao artifactDao;
    private ProductInstallator productInstallator;
    private static Logger log = LoggerFactory.getLogger(ApplicationInstanceManagerImpl.class.getName());

    /**
     * Install the applicationIntance on a EnvironmetInstance Precondition: 1) The EnvironmentInstance is already
     * installed 2) The ApplicationRelease is described in the request.
     * @param data
     * @param environmentInstance
     * @param applicationRelease
     * @return
     * @throws ProductReleaseNotFoundException
     * @throws InvalidEntityException
     * @throws AlreadyExistsEntityException
     * @throws ProductInstallatorException
     */
    public ApplicationInstance install(ClaudiaData data, EnvironmentInstance environmentInstance,
            ApplicationRelease applicationRelease) throws ProductReleaseNotFoundException, InvalidEntityException,
            AlreadyExistsEntityException, ProductInstallatorException {

        log.info("Install aplication " + applicationRelease.getName() + " " + applicationRelease.getVersion() + " on "
                + " enviornment " + environmentInstance + " with number of artifacts "
                + applicationRelease.getArtifacts().size());

        if (!(canInstall(environmentInstance, applicationRelease.getArtifacts()))) {
        	log.warn("The Products included in "
                    + "ApplicationRelease does NOT correspond with the "
                    + "productInstances present in the Environment Instance");
            throw new InvalidEntityException(applicationRelease);
        }

        // Install The applicationRelease=n-Artifacts
        for (Artifact artifact : applicationRelease.getArtifacts()) {
            log.info("Installing artifact " + artifact.getName() + " version " + artifact.getPath() + " product "
                    + artifact.getProductRelease());
            // Install the artifact in the product instance associated
            // Obtain the VMs from EnvInstance where productRelease is installed
            ProductInstance productInstance = getProductInstanceFromEnvironment(artifact, environmentInstance);
            log.info("Installing artifact " + artifact.getName() + " in product instance " + productInstance.getName());
            try {
                productInstallator.installArtifact(data, productInstance, artifact);
                log.info("Artifact installed" + artifact.getName() + " in product instance "
                        + productInstance.getName());
            } catch (OpenStackException e) {
                String errorMessage = "Error to install the product " + e.getMessage();
                log.warn(errorMessage);
                new ProductInstallatorException(errorMessage);
            }

        }

        // Create the Corresponding ApplicationInstance Object
        ApplicationInstance applicationInstance = new ApplicationInstance(applicationRelease, environmentInstance);

        applicationInstance.setVdc(data.getVdc());
        applicationInstance.setStatus(Status.ARTEFACT_DEPLOYED);

        // Insert ApplicationInstance in DB
        try {

            applicationInstance = insertApplicationInstanceDB(
                    applicationInstance,
                    environmentInstance.getName(),
                    environmentInstance.getVdc()
            );

        } catch (InvalidEntityException e3) {
            throw new InvalidEntityException("Error to insert the application instante in the database: "
                    + e3.getMessage());
        } catch (AlreadyExistsEntityException e3) {
            throw new InvalidEntityException("Error to insert the application instante in the database: "
                    + e3.getMessage());
        }

        return applicationInstance;
    }

    public ApplicationInstance load(String vdc,  String name) throws EntityNotFoundException {
        ApplicationInstance instance = applicationInstanceDao.load(name, vdc);
        if (!instance.getVdc().equals(vdc)) {
            throw new EntityNotFoundException(EnvironmentInstance.class, "vdc", vdc);
        }
        return instance;
    }

    /**
     * Find all applicationInstances in teh database
     */
    public List<ApplicationInstance> findAll() {
        return applicationInstanceDao.findAll();
    }

    public List<ApplicationInstance> findByCriteria(ApplicationInstanceSearchCriteria criteria) {
        return applicationInstanceDao.findByCriteria(criteria);
    }

    public void uninstall(ClaudiaData data, EnvironmentInstance environmentInstance,
            ApplicationInstance applicationInstance) throws ProductInstallatorException {
        // Install The applicationRelease=n-Artifacts
        for (int i = 0; i < applicationInstance.getApplicationRelease().getArtifacts().size(); i++) {
            // Install the artifact in the product instance associated
            // Obtain the VMs from EnvInstance where productRelease is installed
            ProductInstance productInstance = getProductInstanceFromEnvironment(applicationInstance
                    .getApplicationRelease().getArtifacts().get(i), environmentInstance);
            try {
                productInstallator.uninstallArtifact(data, productInstance, applicationInstance.getApplicationRelease()
                        .getArtifacts().get(i));
            } catch (OpenStackException e) {
                String errorMessage = "Error to configure the product " + e.getMessage();
                new ProductInstallatorException(errorMessage);
            }

        }

        applicationInstanceDao.remove(applicationInstance);

    }

    /**
     * Insert/update ApplicationRelease in the paas-manager database
     * 
     * @param application
     * @return
     * @throws ProductReleaseNotFoundException
     * @throws ApplicationTypeNotFoundException
     * @throws InvalidEntityException
     * @throws AlreadyExistsEntityException
     */
    private ApplicationInstance insertApplicationInstanceDB(ApplicationInstance application, String env, String vdc)
            throws ProductReleaseNotFoundException,  InvalidEntityException,
            AlreadyExistsEntityException {
        log.info("Inser application " + application.getName() + " in DB");

        ApplicationInstance applicationInstance = null;
        Artifact artifact;
        List<Artifact> artifactsDB = new ArrayList();
        List<ProductRelease> allProductReleases = new ArrayList<ProductRelease>();
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();

        List<Artifact> artifactsIn = new ArrayList<Artifact>();

        try {
            applicationInstance = applicationInstanceDao.load(application.getName(), vdc);
        } catch (EntityNotFoundException e) {
            artifactsIn = application.getApplicationRelease().getArtifacts();

            for (int i = 0; i < artifactsIn.size(); i++) {
                ProductRelease productRelease = artifactsIn.get(i).getProductRelease();
                try {
                    productRelease = productReleaseDao.load(productRelease.getProduct() + "-"
                            + productRelease.getVersion());
                } catch (EntityNotFoundException e1) {
                    throw new ProductReleaseNotFoundException(productRelease, e1.getMessage());
                }

                try {
                    artifact = artifactDao.load(artifactsIn.get(i).getName());
                } catch (EntityNotFoundException e1) {

                    try {
                        artifact = artifactDao.create(new Artifact(artifactsIn.get(i).getName(), artifactsIn.get(i)
                                .getPath(), productRelease));
                        artifactsDB.add(artifact);
                    } catch (AlreadyExistsEntityException e3) {
                        throw new AlreadyExistsEntityException(e3);
                    }
                }

                if (!isPRInAllProductReleases(productRelease, allProductReleases)) {
                    productReleases.add(productRelease);
                }
                allProductReleases.add(productRelease);
            }

            ApplicationRelease applicationRelease = null;

            try {
                applicationRelease = applicationReleaseDao.create(
                        new ApplicationRelease(
                                application.getApplicationRelease().getName(),
                                application.getApplicationRelease().getVersion(),
                                application.getApplicationRelease().getDescription(),
                                application.getApplicationRelease().getTransitableReleases(),
                                artifactsDB)
                        );

            } catch (AlreadyExistsEntityException e3) {
                throw new AlreadyExistsEntityException(e3);
            }

            try {
                applicationInstance = applicationInstanceDao.create(new ApplicationInstance(applicationRelease,
                        application.getEnvironmentInstance(), application.getVdc(), application.getStatus()));
            } catch (AlreadyExistsEntityException e3) {
                throw new AlreadyExistsEntityException(ApplicationInstance.class, e3);
            }
        }
        return applicationInstance;
    }

    /**
     * Get the VMs where the artifact/ProductRelease should be installed
     * 
     * @param artifact
     * @param envInstance
     * @return the VMs where the artifact/ProductRelease should be installed
     */
    private ProductInstance getProductInstanceFromEnvironment(Artifact artifact, EnvironmentInstance envInstance) {

        //
        ProductInstance productInstance = null;

        List<TierInstance> tierInstances = envInstance.getTierInstances();
        // For each tier instance
        for (TierInstance tierInstance : tierInstances) {
            // Check product instnaces where to install the artifacts
            for (ProductInstance deployedProductInstance : tierInstance.getProductInstances()) {
                if (deployedProductInstance.getProductRelease().getProduct()
                        .equals(artifact.getProductRelease().getProduct()))

                {
                    productInstance = deployedProductInstance;
                }
            }

        }
        return productInstance;
    }

    private boolean isPRInAllProductReleases(ProductRelease productRelease, List<ProductRelease> allProductReleases) {
        for (ProductRelease prod: allProductReleases) {
            if (productRelease.getProduct().equals(prod.getProduct())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if any productRelease coming in the Application Release Object is not in the EnvironmentInstance. In this
     * case the method return false
     * 
     * @param envInstance
     * @return true/false
     */
    private boolean canInstall(EnvironmentInstance envInstance, List<Artifact> applicationArtifact) {

        if (applicationArtifact == null) {
            log.info("There is not any product release to install");
            return false;
        }
        List<ProductRelease> productReleasesApp = new ArrayList<ProductRelease>();
        for (Artifact artifact : applicationArtifact) {
            productReleasesApp.add(artifact.getProductRelease());
        }

        List<ProductRelease> productReleasesEnv = new ArrayList<ProductRelease>();
        for (int i = 0; i < envInstance.getTierInstances().size(); i++) {
            TierInstance tInstance = envInstance.getTierInstances().get(i);

            for (int j = 0; j < tInstance.getProductInstances().size(); j++) {
                ProductInstance pInstance = tInstance.getProductInstances().get(j);
                productReleasesEnv.add(pInstance.getProductRelease());
            }

        }

        for (int i = 0; i < productReleasesApp.size(); i++) {
            if (!(isProductReleaseinProductList(productReleasesApp.get(i), productReleasesEnv))) {
                log.info("The artifacts cannot be deployed in the environmetn instance, since the product release "
                        + productReleasesApp.get(i) + " is not in the environment");
                return false;
            }
        }
        log.info("The artifacts can be deployed in the environmetn instance");
        return true;

    }

    /**
     * Evaluates if a productRelease is included in a productReleaseList by looking at the productRelease Name.
     * 
     * @param pRelease
     * @param productList
     * @return true/false
     */
    private boolean isProductReleaseinProductList(ProductRelease pRelease, List<ProductRelease> productList) {

        List<String> pnameList = new ArrayList<String>();

        for (int i = 0; i < productList.size(); i++) {
            pnameList.add(productList.get(i).getProduct());
        }
        return pnameList.contains(pRelease.getProduct());
    }

    // //////////// I.O.C /////////////
    /**
     * @param applicationReleaseDao
     *            the applicationReleaseDao to set
     */
    public void setApplicationReleaseDao(ApplicationReleaseDao applicationReleaseDao) {
        this.applicationReleaseDao = applicationReleaseDao;
    }

    /**
     * @param productReleaseDao
     *            the productReleaseDao to set
     */
    public void setProductReleaseDao(ProductReleaseDao productReleaseDao) {
        this.productReleaseDao = productReleaseDao;
    }

    /**
     * @param applicationInstanceDao
     *            the applicationInstanceDao to set
     */
    public void setApplicationInstanceDao(ApplicationInstanceDao applicationInstanceDao) {
        this.applicationInstanceDao = applicationInstanceDao;
    }

    /**
     * @param artifactDao
     *            the artifactDao to set
     */
    public void setArtifactDao(ArtifactDao artifactDao) {
        this.artifactDao = artifactDao;
    }

    /**
     * @param productInstallator
     *            the productInstallator to set
     */
    public void setProductInstallator(ProductInstallator productInstallator) {
        this.productInstallator = productInstallator;
    }

}
