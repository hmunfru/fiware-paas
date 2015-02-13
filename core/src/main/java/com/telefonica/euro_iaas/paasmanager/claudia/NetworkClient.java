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

package com.telefonica.euro_iaas.paasmanager.claudia;

import java.util.List;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.Port;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;
import org.json.JSONException;

/**
 * @author henar
 */
public interface NetworkClient {

    /**
     * Add the network to the public router.
     */
    void addNetworkToPublicRouter(ClaudiaData claudiaData, NetworkInstance net, String region)
            throws InfrastructureException;

    /**
     * Add the network to the router.
     */

    void addNetworkToRouter(ClaudiaData claudiaData, RouterInstance router, NetworkInstance network)
            throws InfrastructureException;

    /**
     * Remove the network from the router.
     * 
     * @param claudiaData
     * @param router
     * @param idNet
     */

    void deleteNetworkFromRouter(ClaudiaData claudiaData, RouterInstance router, NetworkInstance idNet, String region)
            throws InfrastructureException;

    /**
     * Deploy the network in the infrastructure.
     * 
     * @param claudiaData
     * @param network
     */
    void deployNetwork(ClaudiaData claudiaData, NetworkInstance network, String region) throws InfrastructureException;

    /**
     * Deploy the router.
     * 
     * @param claudiaData
     * @param router
     * @return
     * @throws InfrastructureException
     */
    void deployRouter(ClaudiaData claudiaData, RouterInstance router, String region) throws InfrastructureException;

    /**
     * Deploy the subnetwork in the infrastructure.
     * 
     * @param claudiaData
     * @param subNet
     */
    void deploySubNetwork(ClaudiaData claudiaData, SubNetworkInstance subNet, String region)
        throws InfrastructureException;

    /**
     * Destroy the network in the infrastructure.
     * 
     * @param claudiaData
     * @param networkInstance
     */
    void destroyNetwork(ClaudiaData claudiaData, NetworkInstance networkInstance, String region)
        throws InfrastructureException;

    /**
     * It delete a router in Openstack.
     * 
     * @param claudiaData
     * @param router
     * @throws InfrastructureException
     */
    void destroyRouter(ClaudiaData claudiaData, RouterInstance router, String region)
        throws InfrastructureException;

    /**
     * Destroy a subnet in OpenStack.
     * 
     * @param claudiaData
     * @param subnet
     * @return
     * @throws EntityNotFoundException
     */
    void destroySubNetwork(ClaudiaData claudiaData, SubNetworkInstance subnet, String region)
            throws InfrastructureException;

    /**
     * Loads all networks associated to a certain vdc.
     * 
     * @param claudiaData
     * @return List<Network>
     * @throws InfrastructureException
     */
    List<NetworkInstance> loadAllNetwork(ClaudiaData claudiaData,
                                         String region)
        throws InfrastructureException;

    /**
     * Loads all subnetwork associated to a certain vdc.
     *
     * @param claudiaData
     * @return List<SubNetwork>
     * @throws InfrastructureException
     */
    List<SubNetworkInstance> loadAllSubNetworks(ClaudiaData claudiaData,
                                         String region)
        throws InfrastructureException;


    /**
     * Load a Network from OpenStack.
     */
    NetworkInstance loadNetwork(ClaudiaData claudiaData, NetworkInstance network, String region)
        throws EntityNotFoundException;

    /**
     * Load a subNet from Openstack.
     * 
     * @param claudiaData
     * @param subNetId
     * @return
     * @throws EntityNotFoundException
     */
    SubNetworkInstance loadSubNetwork(ClaudiaData claudiaData, String subNetId, String region)
        throws EntityNotFoundException;
    
    /**
     * 
     * @param claudiaData
     * @param region
     * @param networkId
     * @return
     * @throws InfrastructureException
     */
    List<Port> listPortsFromNetwork(ClaudiaData claudiaData, String region, String networkId)
        throws InfrastructureException;

    /**
     * It deletes the interface in the public router.
     * 
     * @param claudiaData
     * @param networkInstance
     * @throws InfrastructureException
     */
    void deleteNetworkToPublicRouter(ClaudiaData claudiaData, NetworkInstance networkInstance, String region)
        throws InfrastructureException;

	/**
	 * It obtains the no shared networks.
	 * @param claudiaData
	 * @return
	 * @throws InfrastructureException 
	 */
    List<NetworkInstance> loadNotSharedNetworks(ClaudiaData claudiaData, String region) throws InfrastructureException;

    /**
     * 
     * @param claudiaData
     * @param region
     * @return
     * @throws InfrastructureException
     */
    NetworkInstance deployDefaultNetwork(ClaudiaData claudiaData, String region) throws InfrastructureException;

    /**
     * 
     * @param claudiaData
     * @param networkInstance
     * @param networkInstance2
     * @throws InfrastructureException 
     */
	void joinNetworks(ClaudiaData claudiaData, NetworkInstance networkInstance,
	    NetworkInstance networkInstance2) throws InfrastructureException;

}
