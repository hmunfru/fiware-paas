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
import com.telefonica.euro_iaas.paasmanager.exception.ProductReconfigurationException;
import com.telefonica.euro_iaas.paasmanager.model.Artifact;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.sdc.model.dto.ChefClient;

public interface ProductInstallator {

    /**
     * Operation that installs the productInstance
     * 
     * @param productInstance
     * @return
     * @throws ProductInstallatorException
     * @throws OpenStackException
     */
    // ProductInstance install (ProductInstance productInstance) throws
    // ProductInstallatorException;

    ProductInstance install(ClaudiaData claudiaData, EnvironmentInstance environmentInstance,
            TierInstance tierInstance, ProductRelease productRelease)
            throws ProductInstallatorException, OpenStackException;

    /**
     * Operation that installs an artefact in the productInstance
     * 
     * @param productInstance
     * @return
     * @throws ProductInstallatorException
     * @throws OpenStackException
     */

    void installArtifact(ClaudiaData claudiaData, ProductInstance productInstance, Artifact artifact)
            throws ProductInstallatorException, OpenStackException;

    /**
     * Operation that uninstalls an artefact in the productInstance
     * 
     * @param productInstance
     * @return
     * @throws ProductInstallatorException
     * @throws OpenStackException
     */

    void uninstallArtifact(ClaudiaData claudiaData, ProductInstance productInstance, Artifact artifact)
            throws ProductInstallatorException, OpenStackException;

    /*
     * Operation that installs the productInstance
     */

    void uninstall(ClaudiaData claudiaData, ProductInstance productInstance) throws ProductInstallatorException,
            OpenStackException;

    void configure(ClaudiaData claudiaData, ProductInstance productInstance, List<Attribute> properties)
            throws ProductInstallatorException, EntityNotFoundException, ProductReconfigurationException,
            OpenStackException;

    /*
     * Operation that deletes a chefClien from the node manager (Chef server in
     * SDC, for instance)
     */

    void deleteNode(ClaudiaData claudiaData, String vdc, String nodeName) throws ProductInstallatorException,
            OpenStackException;

    /*
     * Operation that load a chefClient from the node manager (Chef server in
     * SDC, for instance)
     */

    ChefClient loadNode(ClaudiaData claudiaData, String vdc, String nodeName) throws ProductInstallatorException,
            EntityNotFoundException, OpenStackException;

}
