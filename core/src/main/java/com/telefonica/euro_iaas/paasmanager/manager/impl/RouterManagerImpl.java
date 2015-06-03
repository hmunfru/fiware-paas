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
import com.telefonica.euro_iaas.paasmanager.dao.RouterDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.manager.RouterManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;

/**
 * @author henar
 */
public class RouterManagerImpl implements RouterManager {

    private RouterDao routerDao = null;
    private NetworkClient networkClient = null;
    private static Logger log = LoggerFactory.getLogger(RouterManagerImpl.class);

    /**
     * It adds a network to the router.
     * 
     * @throws InfrastructureException
     * @params claudiaData
     * @params router
     * @network
     */

    public void addNetwork(ClaudiaData claudiaData, RouterInstance router, NetworkInstance network)
            throws InfrastructureException {
        networkClient.addNetworkToRouter(claudiaData, router, network);
        network.addRouter(router);
    }

    /**
     * To create a router.
     * 
     * @params claudiaData
     * @params router
     */

    public void create(ClaudiaData claudiaData, RouterInstance router, NetworkInstance network, String region)
            throws InvalidEntityException, InfrastructureException {
        log.info("Create router " + router.getName());

        try {
            routerDao.load(router.getName());

        } catch (EntityNotFoundException e1) {

            try {
                networkClient.deployRouter(claudiaData, router, region);
                addNetwork(claudiaData, router, network);
                router = routerDao.create(router);
            } catch (InfrastructureException e) {
                String msm = "Error to deploy the router " + e.getMessage();
                log.error(msm);
                throw new InfrastructureException(msm, e);
            } catch (AlreadyExistsEntityException e) {
                String msm = "Error to deploy the router. It already exists in the database " + e.getMessage();
                log.error(msm);
                throw new InvalidEntityException(router);
            } catch (Exception e) {
                String msm = "Error to deploy the router. Error in the database " + e.getMessage();
                log.error(msm);
                throw new InvalidEntityException(router);
            }
        }
    }

    /**
     * To remove a router.
     * 
     * @params claudiaData
     * @params router
     */
    public void delete(ClaudiaData claudiaData, RouterInstance router, NetworkInstance network, String region)
            throws EntityNotFoundException, InvalidEntityException, InfrastructureException {
        log.info("Destroying router " + router.getName());
        deleteInterfacesNetworks(claudiaData, router, network, region);
        try {
            networkClient.destroyRouter(claudiaData, router, region);
            routerDao.remove(router);
        } catch (Exception e) {
            log.error("Error to remove the router in BD " + e.getMessage());
            throw new InvalidEntityException(router);
        }

    }

    /**
     * It adds a network to the router.
     * 
     * @throws InfrastructureException
     * @throws InvalidEntityException
     * @params claudiaData
     * @params router
     * @network
     */
    private void deleteInterfacesNetworks(ClaudiaData claudiaData, RouterInstance router, NetworkInstance net,
            String region) throws InfrastructureException, InvalidEntityException {
        log.info("Removing the network interface " + net.getNetworkName() + " " + net.getIdNetRouter()
                + " from router " + router.getName());
        networkClient.deleteNetworkFromRouter(claudiaData, router, net, region);
        routerDao.update(router);

    }

    /**
     * To obtain the list of router.
     * 
     * @return the router list
     */
    public List<RouterInstance> findAll() {
        return routerDao.findAll();
    }

    /**
     * To obtain the router.
     */
    public RouterInstance load(String name) throws EntityNotFoundException {
        return routerDao.load(name);
    }

    public void setNetworkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public void setRouterDao(RouterDao routerDao) {
        this.routerDao = routerDao;
    }

    /**
     * To update the subnetwork.
     */
    public RouterInstance update(RouterInstance subNetwork) throws InvalidEntityException {
        // TODO Auto-generated method stub
        return null;
    }

}
