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
import com.telefonica.euro_iaas.paasmanager.claudia.NetworkClient;
import com.telefonica.euro_iaas.paasmanager.dao.NetworkInstanceDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.manager.NetworkInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.RouterManager;
import com.telefonica.euro_iaas.paasmanager.manager.SubNetworkInstanceManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.Port;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * @author henar
 */
public class NetworkInstanceManagerImpl implements NetworkInstanceManager {

    private NetworkInstanceDao networkInstanceDao = null;
    private NetworkClient networkClient = null;
    private SubNetworkInstanceManager subNetworkInstanceManager = null;
    private RouterManager routerManager = null;
    private SystemPropertiesProvider systemPropertiesProvider;

    private static String NETWORK_CIDR = "10.%s.%s.0/24";
    public static int MAX_NETWORK = 254;
    public static int NET_ELEMENT = 2;

    private static Logger log = LoggerFactory.getLogger(NetworkInstanceManagerImpl.class);

    /**
     * It checks if the network exists in the DB and Openstack.
     * @param claudiaData
     * @param networkInstance
     * @param region
     * @return
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */
    public boolean exists (ClaudiaData claudiaData, NetworkInstance networkInstance, String region)
        throws InvalidEntityException, EntityNotFoundException, AlreadyExistsEntityException {
    	if (existsInDB(networkInstance.getNetworkName(), claudiaData.getVdc(), region)) {
    		networkInstance = networkInstanceDao.load(networkInstance.getNetworkName(), claudiaData.getVdc(), region);
            if (!existsInOpenstack (claudiaData, networkInstance, region)) {
            	log.warn ("The network "+  networkInstance.getNetworkName()+ " with id "
                    + networkInstance.getIdNetwork() + " does no exists in Openstack") ;
            	this.deleteInDb(networkInstance);
            	return false;

            } else {
            	log.info("The network "+  networkInstance.getNetworkName()+ " with id "
                    + networkInstance.getIdNetwork() + " already exists");
            	return true;
            }


        } else {
            if (!existsInOpenstack (claudiaData, networkInstance, region)) {
                log.warn ("The network "+  networkInstance.getNetworkName()+ " with id " +
                    networkInstance.getIdNetwork() + " does no exists in Openstack") ;
                return false;

            } else {
                createInBD(claudiaData, networkInstance, region);
                return true;
            }
        }
    }


    /**
     * It creates a network in DB and Openstack.
     * @param claudiaData
     * @param networkInstance
     * @param region
     * @return
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     * @throws AlreadyExistsEntityException
     */
    public NetworkInstance create (ClaudiaData claudiaData, NetworkInstance networkInstance, String region)
            throws InfrastructureException, EntityNotFoundException, InvalidEntityException, AlreadyExistsEntityException {
    	networkClient.deployNetwork(claudiaData, networkInstance, region);
        log.info("Network isntance " + networkInstance.getNetworkName() + " with vdc " + claudiaData.getVdc()
            + ": " + networkInstance.getIdNetwork() + " and federated " + networkInstance.getFederatedRange() +
            " " + networkInstance.getfederatedNetwork()+  " deployed");
        try {
            createSubNetworksInstance(claudiaData,networkInstance, region);
            networkClient.addNetworkToPublicRouter(claudiaData, networkInstance, region);
            log.info("Storing network instance " + networkInstance.getNetworkName() + " vdc "
                    + claudiaData.getVdc());
            networkInstance = networkInstanceDao.create(networkInstance);
        } catch (InfrastructureException e) {
            log.warn("There is an error to deploy an subNet " + e.getMessage());
            restoreNetwork(claudiaData, networkInstance, region);
            throw new InfrastructureException("There is an error to deploy a subNet " + e.getMessage());
        }
        return networkInstance;
    }

    /**
     * @param claudiaData
     * @param networkInstance
     * @throws InvalidEntityException
     * @throws AlreadyExistsEntityException
     * @throws EntityNotFoundException
     * @throws InfrastructureException
     */
    public void createFederatedNetwork(ClaudiaData claudiaData, List<NetworkInstance> networkInstance)
            throws InvalidEntityException, AlreadyExistsEntityException, EntityNotFoundException,
            InfrastructureException {
        log.info("Create federated network  for vdc " + claudiaData.getVdc() + " with netwrks "
                + networkInstance.get(0).getNetworkName() + " and " + networkInstance.get(1).getNetworkName());

        networkClient.joinNetworks(claudiaData, networkInstance.get(0), networkInstance.get(1));
    }

    public NetworkInstance createInDB(NetworkInstance networkInstance) throws InvalidEntityException,
            AlreadyExistsEntityException {
        return networkInstanceDao.create(networkInstance);

    }

    /**
     * It restore the situation if there is a failure.
     *
     * @param claudiaData
     * @param networkInstance
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     */
    private void restoreNetwork(ClaudiaData claudiaData, NetworkInstance networkInstance, String region)
            throws EntityNotFoundException, InvalidEntityException, InfrastructureException {
        for (SubNetworkInstance subNet : networkInstance.getSubNets()) {
            if (subNetworkInstanceManager.isSubNetworkDeployed(claudiaData, subNet, region)) {
                subNetworkInstanceManager.delete(claudiaData, subNet, region);
            }
        }
        log.info("Deleting the network");
        networkClient.destroyNetwork(claudiaData, networkInstance, region);

        networkInstance.setSubNets(null);
        networkInstanceDao.update(networkInstance);
        networkInstanceDao.remove(networkInstance);

    }

    /**
     * It creates a subnet in the network.
     */
    private void createSubNetworksInstance(ClaudiaData claudiaData, NetworkInstance networkInstance, String region)
            throws InvalidEntityException, AlreadyExistsEntityException, InfrastructureException

    {
        Set<SubNetworkInstance> subNetAxu = networkInstance.cloneSubNets();
        for (SubNetworkInstance subNet : subNetAxu) {
        	String cidr = null;
            log.info("SubNetwork " + subNet.getName() + " id net " + subNet.getIdNetwork());
            if (networkInstance.getfederatedNetwork()) {
            	cidr = networkInstance.getFederatedRange();
            }
            else {
                cidr = getDefaultCidr(claudiaData, region );
            }
            subNet.setIdNetwork(networkInstance.getIdNetwork());
            subNet.setCidr(cidr);
            networkInstance.updateSubNet(subNet);
            subNet = subNetworkInstanceManager.create(claudiaData, subNet, region);

            log.info("SubNetwork " + subNet.getName() + " id net " + subNet.getIdNetwork() + " in network  "
                    + networkInstance.getNetworkName() + " deployed");
        }
    }

    /**
     * To remove a network.
     *
     * @params claudiaData
     * @params network
     */
    public void delete(ClaudiaData claudiaData,
                       NetworkInstance networkInstance, String region)
            throws InvalidEntityException, InfrastructureException {
        log.info("Destroying network " + networkInstance.getNetworkName()
            + " region " + region);

        if (!canBeDeleted(claudiaData, networkInstance, region)) {
            log.info("The network cannot be deleted due to existing ports");
            return;
        }

        log.info("Deleting the public interface");
        try {
            log.info("Loading network " + networkInstance.getNetworkName()
                + "  " + claudiaData.getVdc() + " " + region);
            networkInstance = networkInstanceDao.load(networkInstance.getNetworkName(),
                claudiaData.getVdc(), region);
        } catch (Exception e) {
            log.error("It is not possible to find the network "
                + e.getMessage());
            throw new InvalidEntityException(networkInstance);
        }
        try {
            networkClient.deleteNetworkToPublicRouter(claudiaData,
                networkInstance, region);
        } catch (Exception e) {
            log.warn ("It is not possible to delete the public interface. "
                + "It is not exist: " + e.getMessage());
        }

        log.info("Deleting the subnets");
        Set<SubNetworkInstance> subNetAux = networkInstance.cloneSubNets();
        networkInstance.getSubNets().clear();
        networkInstanceDao.update(networkInstance);
        for (SubNetworkInstance subNet : subNetAux) {
            subNetworkInstanceManager.delete(claudiaData, subNet, region);
        }
        log.info("Deleting the network");
        networkClient.destroyNetwork(claudiaData, networkInstance, region);
        try {
            networkInstanceDao.remove(networkInstance);
        } catch (Exception e) {
            log.error("Error to remove the network in BD " + e.getMessage());
            throw new InvalidEntityException(networkInstance);
        }

    }

    private void deleteInDb(NetworkInstance networkInstance)
        throws InvalidEntityException {
    log.info("Destroying network in DB" + networkInstance.getNetworkName()
        + " " + networkInstance.getSubNets().size() );

    Set<SubNetworkInstance> subNetAux = networkInstance.cloneSubNets();
    log.info("Deleting the subnets " + subNetAux.size());
    networkInstance.getSubNets().clear();
    networkInstanceDao.update(networkInstance);
    for (SubNetworkInstance subNet : subNetAux) {
    	log.info ("Delete subnet " + subNet.getName());
    	subNetworkInstanceManager.deleteInBD(subNet);
    }
    log.info("Deleting the network");
    try {
    	networkInstanceDao.remove(networkInstance);
    } catch (Exception e) {
    	log.error("Error to remove the network in BD " + e.getMessage());
    	throw new InvalidEntityException(networkInstance);
    }

}

    /**
     * To remove a network.
     *
     * @params claudiaData
     * @params network
     */
    public boolean canBeDeleted(ClaudiaData claudiaData,
                                NetworkInstance networkInstance, String region)
            throws InvalidEntityException, InfrastructureException {
        log.info("Obtaining ports from network"
            + networkInstance.getNetworkName());

        List<Port> ports = networkClient.listPortsFromNetwork(claudiaData, region,
                networkInstance.getIdNetwork());
        log.info("List ports " + ports.size());
        if (ports.size() == 0) {
            return true;
        } else {
            String strPorts = "";
            for (Port port : ports) {
                strPorts = strPorts + " " + port.getNetworkId();
            }
            log.info("It is not possible to undeply the network since there "
                + " are VMs associated to it " + strPorts);
            return false;

        }
    }

    /**
     * It join two networks.
     * @param claudiaData: credentials
     * @param net1
     * @param net2
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     */
    public void joinNetwork(ClaudiaData claudiaData, NetworkInstance net1,
                            NetworkInstance net2)
            throws EntityNotFoundException, InvalidEntityException,
        InfrastructureException {
        log.info("joining netowrk " + net1.getNetworkName()
            + " " + net2.getNetworkName());
        networkClient.joinNetworks(claudiaData, net1, net2);

    }

    /**
     * To obtain the list of networks.
     * @return the network list
     */
    public List<NetworkInstance> findAll() {
        return networkInstanceDao.findAll();
    }

    /**
     * To obtain the list of networks.
     * @return the network list
     */
    private boolean existsInDB(String networkInstance,
                               String vdc, String region) {
        try {
            networkInstanceDao.load(networkInstance, vdc, region);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private NetworkInstance createInBD (ClaudiaData data,
                                        NetworkInstance netInstance,
                                        String region)
        throws EntityNotFoundException, AlreadyExistsEntityException {

        netInstance = networkClient.loadNetwork(data, netInstance, region);
        Set<SubNetworkInstance> subNetAxu = netInstance.cloneSubNets();
        netInstance.clearSubNets();
        for (SubNetworkInstance subNet : subNetAxu) {
            subNet = subNetworkInstanceManager.createInBD(subNet);
            netInstance.addSubNet(subNet);

        }
        netInstance = networkInstanceDao.create(netInstance);
        return netInstance;
    }

    private boolean existsInOpenstack (ClaudiaData claudiaData,
                                       NetworkInstance network, String region) {
    	try {
			networkClient.loadNetwork(claudiaData, network, region);
			return true;
		} catch (EntityNotFoundException e) {
			return false;

		}
    }

    public List<NetworkInstance> listNetworks(ClaudiaData claudiaData,
                                              String region)
        throws InfrastructureException {
        List<NetworkInstance> networkInstances =
            networkClient.loadAllNetwork(claudiaData, region);
        return networkInstances;
    }

    /**
     * To obtain the network.
     * @param networkName
     * @return the network
     */
    public NetworkInstance load(String networkName, String vdc, String region)
        throws EntityNotFoundException {
        return networkInstanceDao.load(networkName, vdc, region);
    }

    public void setNetworkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public void setNetworkInstanceDao(NetworkInstanceDao networkInstanceDao) {
        this.networkInstanceDao = networkInstanceDao;
    }

    public void setRouterManager(RouterManager routerManager) {
        this.routerManager = routerManager;
    }

    public void setSubNetworkInstanceManager(SubNetworkInstanceManager
                                                subNetworkInstanceManager) {
        this.subNetworkInstanceManager = subNetworkInstanceManager;
    }

    /**
     * @param systemPropertiesProvider
     *            the systemPropertiesProvider to set
     */
    public void setSystemPropertiesProvider(SystemPropertiesProvider
                                                systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

    public NetworkInstance update(NetworkInstance networkInstance)
        throws InvalidEntityException {
        return networkInstanceDao.update(networkInstance);
    }

    /**
     * It obtains the number of networks deployed in a concrete region.
     * This is used for obtaining a valid CIDR.
     * @param claudiaData: credentials.
     * @param region: the region
     * @return the number of regions
     * @throws InfrastructureException
     */
    public int getNumberDeployedNetwork(ClaudiaData claudiaData,
                                        String region)
        throws InfrastructureException {
        return networkClient.loadAllNetwork(claudiaData, region).size();
    }

    /**
     * Checks if the CIDR is valid, so that, if it does not exist.
     * @param claudiaData: credentials.
     * @param region: the region
     * @param cidr: the CIDR to check.
     * @return
     * @throws InfrastructureException
     */
    public boolean checkCidrValid(ClaudiaData claudiaData,
                                  String region, String cidr)
        throws InfrastructureException {
        List<SubNetworkInstance> subNets =
            networkClient.loadAllSubNetworks(claudiaData, region);

        for (SubNetworkInstance subNet: subNets) {
            if (subNet.getCidr().equals(cidr)) {
                return false;
            }
        }
        return true;
    }

    private String getValidCidr (ClaudiaData claudiaData, String region,
                                int subNetElement)
        throws InfrastructureException {
        String cidr = String.format(NETWORK_CIDR, NET_ELEMENT, subNetElement);
        if (checkCidrValid(claudiaData, region, cidr)) {
            return cidr;
        }
        else {
            return getValidCidr (claudiaData, region,subNetElement+1);
        }
    }

    /**
     * It gets the defaults Cidr.
     * @param claudiaData
     * @param region
     * @return
     * @throws InvalidEntityException
     * @throws AlreadyExistsEntityException
     * @throws InfrastructureException
     */
    public String getDefaultCidr(ClaudiaData claudiaData, String region)
        throws InvalidEntityException, AlreadyExistsEntityException,
        InfrastructureException {
        int subNetElement = 1;
        int cidrOpenstack = 0;
        if (!(claudiaData.getVdc() == null || claudiaData.getVdc().isEmpty())) {
            // Two networks cannot overlap the same CIDR. It should be unique
            cidrOpenstack = getNumberDeployedNetwork(claudiaData, region) + 1;
        }
        if (cidrOpenstack > MAX_NETWORK) {
            NET_ELEMENT += 1;
            subNetElement = cidrOpenstack / NET_ELEMENT;
        } else {
            subNetElement = cidrOpenstack;
        }
        return getValidCidr (claudiaData, region, subNetElement);
    }


}
