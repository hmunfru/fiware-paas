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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.claudia.NetworkClient;
import com.telefonica.euro_iaas.paasmanager.dao.SubNetworkInstanceDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.manager.SubNetworkInstanceManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;

/**
 * @author henar
 */
public class SubNetworkInstanceManagerImpl implements SubNetworkInstanceManager {

    private SubNetworkInstanceDao subNetworkInstanceDao = null;
    private NetworkClient networkClient = null;
    private static Logger log = LoggerFactory.getLogger(SubNetworkInstanceManagerImpl.class);

    /**
     * To create a network.
     * 
     * @throws AlreadyExistsEntityException
     *             , InfrastructureException, InvalidEntityException
     * @params claudiaData
     * @params network
     */
    public SubNetworkInstance create(ClaudiaData claudiaData, SubNetworkInstance subNetwork, String region)
            throws InvalidEntityException, InfrastructureException, AlreadyExistsEntityException {
        log.info("Create subnetwork instance " + subNetwork.getName());
        if (!subNetworkInstanceDao.exists(subNetwork.getName(), claudiaData.getVdc(), region)) {
            networkClient.deploySubNetwork(claudiaData, subNetwork, region);
            log.info("SubNetwork " + subNetwork.getName() + " in network " + subNetwork.getIdNetwork()
                    + " deployed with id " + subNetwork.getIdSubNet());
            subNetwork = subNetworkInstanceDao.create(subNetwork);
        } else {
            log.warn("Subred already created " + subNetwork.getName());
            throw new AlreadyExistsEntityException(subNetwork);
        }
        return subNetwork;
    }

    /**
     * To remove a subnetwork.
     * 
     * @params claudiaData
     * @params subNetwork
     */
    public void delete(ClaudiaData claudiaData, SubNetworkInstance subNetworkInstance, String region)
            throws InvalidEntityException, InfrastructureException {
        log.info("Destroying the subnetwork " + subNetworkInstance.getName());
        try {
            networkClient.destroySubNetwork(claudiaData, subNetworkInstance, region);
        } catch (Exception e) {
            log.error("Error to remove the subnetwork in BD " + e.getMessage());
            throw new InvalidEntityException(subNetworkInstance);
        }

    }


    /**
     * It deletes the subnet in the DB.
     * @param subNetworkInstance
     */
    public void deleteInBD( SubNetworkInstance subNetworkInstance) {
    	log.info("Destroying in bd the subnetwork " + subNetworkInstance.getName());
        subNetworkInstanceDao.remove(subNetworkInstance);
    }

    /**
     * It creates the subNet in BD.
     * @param subNetworkInstance
     */
    public SubNetworkInstance createInBD( SubNetworkInstance subNetworkInstance) throws AlreadyExistsEntityException {
        log.info("Creating in bd the subnetwork " + subNetworkInstance.getName());
        return subNetworkInstanceDao.create(subNetworkInstance);
    }

    /**
     * To obtain the list of subnetworks.
     * 
     * @return the subnetwork list
     */
    public List<SubNetworkInstance> findAll() {
        return subNetworkInstanceDao.findAll();
    }

    /**
     * Is the subNetwork deployed.
     * 
     * @param claudiaData
     * @param subNet
     * @return
     */
    public boolean isSubNetworkDeployed(ClaudiaData claudiaData, SubNetworkInstance subNet, String region) {
        try {
            networkClient.loadSubNetwork(claudiaData, subNet.getIdSubNet(), region);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }

    /**
     * To obtain the subnetwork.
     */
    public SubNetworkInstance load(String name, String vdc, String region) throws EntityNotFoundException {
        return subNetworkInstanceDao.load(name, vdc, region);
    }

    public void setNetworkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public void setSubNetworkInstanceDao(SubNetworkInstanceDao subNetworkInstanceDao) {
        this.subNetworkInstanceDao = subNetworkInstanceDao;
    }

    /**
     * To update the subnetwork.
     */
    public SubNetworkInstance update(SubNetworkInstance subNetworkInstance) throws InvalidEntityException {
        return subNetworkInstanceDao.update(subNetworkInstance);
    }

}
