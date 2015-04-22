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

package com.telefonica.euro_iaas.paasmanager.installator;

import java.util.List;
import java.util.Set;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.openstack.auth.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.installator.sdc.util.SDCUtil;
import com.telefonica.euro_iaas.paasmanager.model.Artifact;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;
import com.telefonica.euro_iaas.sdc.client.SDCClient;
import com.telefonica.euro_iaas.sdc.model.dto.ChefClient;

public class ProductInstallatorDummySdcImpl implements ProductInstallator {

    private SDCClient sDCClient;
    private SystemPropertiesProvider systemPropertiesProvider;
    private SDCUtil sDCUtil;

    // //////////// I.O.C /////////////
    /**
     * @param sDCClient
     *            the sDCClient to set
     */
    public void setSDCClient(SDCClient sDCClient) {
        this.sDCClient = sDCClient;
    }

    /**
     * @param systemPropertiesProvider
     *            the systemPropertiesProvider to set
     */
    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

    public void setSDCUtil(SDCUtil sDCUtil) {
        this.sDCUtil = sDCUtil;
    }

    public ProductInstance install(ClaudiaData claudiaData, EnvironmentInstance environmentInstance,
            TierInstance tierInstance, ProductRelease productRelease)
            throws ProductInstallatorException {

        ProductInstance productInstance = new ProductInstance();
        productInstance.setStatus(Status.INSTALLED);
        productInstance.setName(tierInstance.getName() + "_" + productRelease.getProduct() + "_"
                + productRelease.getVersion());
        productInstance.setProductRelease(productRelease);
        // productInstance.setTierInstance(tierInstance);
        productInstance.setVdc(tierInstance.getVdc());
        return productInstance;

    }

    public void configure(ClaudiaData data, ProductInstance productInstance, List<Attribute> properties) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteNode(ClaudiaData claudiaData, String vdc, String nodeName) throws ProductInstallatorException {
        // TODO Auto-generated method stub

    }

    @Override
    public void installArtifact(ClaudiaData claudiaData, ProductInstance productInstance, Artifact artifact)
            throws ProductInstallatorException, OpenStackException {
        // TODO Auto-generated method stub

    }

    @Override
    public ChefClient loadNode(ClaudiaData claudiaData, String vdc, String nodeName)
            throws ProductInstallatorException, EntityNotFoundException, OpenStackException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void uninstall(ClaudiaData claudiaData, ProductInstance productInstance) throws ProductInstallatorException,
            OpenStackException {
        // TODO Auto-generated method stub

    }

    @Override
    public void uninstallArtifact(ClaudiaData claudiaData, ProductInstance productInstance, Artifact artifact)
            throws ProductInstallatorException, OpenStackException {
        // TODO Auto-generated method stub

    }

}
