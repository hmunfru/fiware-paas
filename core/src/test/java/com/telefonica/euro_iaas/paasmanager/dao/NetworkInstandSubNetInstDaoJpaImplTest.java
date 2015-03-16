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

package com.telefonica.euro_iaas.paasmanager.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;

/**
 * @author jesus.movilla
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-test-db-config.xml", "classpath:/spring-dao-config.xml" })
public class NetworkInstandSubNetInstDaoJpaImplTest {

    @Autowired
    private NetworkInstanceDao networkInstanceDao;
    @Autowired
    private SubNetworkInstanceDao subNetworkInstanceDao;
    @Autowired
    private TierInstanceDao tierInstanceDao;
    public static String NETWORK_NAME = "network_name";
    public static String SUB_NETWORK_NAME = "subnetwork_name";

    public static String CIDR ="10.100.1.0/24";
    public static String CIDR2 ="10.100.2.0/24";
    public static String CIDR_ID ="1";
    public static String ID ="8";
    public static String REGION = "region";
    public static String VDC = "vdc";

    @Test
    public void testNetworkNoSubNet() throws Exception {

        NetworkInstance network = new NetworkInstance(NETWORK_NAME + 1, "vdc", REGION);

        network = networkInstanceDao.create(network);
        assertNotNull(network);
        assertEquals(network.getNetworkName(), NETWORK_NAME + 1);
        assertEquals(network.getSubNets().size(), 0);

        List<NetworkInstance> networks = networkInstanceDao.findAll();
        assertNotNull(networks);

        NetworkInstance networkOut = networkInstanceDao.load(NETWORK_NAME + 1, VDC, REGION);
        assertNotNull(networkOut);
        assertEquals(networkOut.getNetworkName(), NETWORK_NAME + 1);
        assertEquals(networkOut.getSubNets().size(), 0);

    }

    /**
     * It test finding tier instances.
     * @throws Exception
     */
    @Test
    public void testFindTierInstanceUsedByNetwork() throws Exception {

        TierInstance tierInst = new TierInstance ();
        tierInst.setName("tiername");
        tierInstanceDao.create(tierInst);
        NetworkInstance net = new NetworkInstance("netname" , "vdc", "region");
        networkInstanceDao.create(net);
        tierInst.addNetworkInstance(net);
        tierInstanceDao.update(tierInst);

        TierInstance tierInst2 = new TierInstance ();
        tierInst2.setName("tiername2");
        tierInstanceDao.create(tierInst2);
        NetworkInstance net2 = new NetworkInstance("netname2" , "vdc", "region");
        networkInstanceDao.create(net2);
        tierInst2.addNetworkInstance(net2);
        tierInstanceDao.update(tierInst2);

        List<TierInstance> networkOut = networkInstanceDao.
            findTierInstanceUsedByNetwork("netname", "vdc", "region");
        assertNotNull(networkOut);
        assertEquals(networkOut.size(), 1);


    }

    @Test
    public void testNetworkWithSubNets() throws Exception {

        List<NetworkInstance> networks = networkInstanceDao.findAll();
        assertNotNull(networks);

        int number = networks.size();
        SubNetworkInstance subNet = new SubNetworkInstance(SUB_NETWORK_NAME,VDC, REGION);
        subNet = subNetworkInstanceDao.create(subNet);
        assertNotNull(subNet);
        assertEquals(subNet.getName(), SUB_NETWORK_NAME);

        Set<SubNetworkInstance> subNets = new HashSet<SubNetworkInstance>();
        subNets.add(subNet);
        NetworkInstance network = new NetworkInstance(NETWORK_NAME+2, "vdc", REGION);
        network.setSubNets(subNets);

        network = networkInstanceDao.create(network);
        assertNotNull(network);
        assertEquals(network.getNetworkName(), NETWORK_NAME+2);
        assertEquals(network.getSubNets().size(), 1);

        networks = networkInstanceDao.findAll();
        assertNotNull(networks);
        assertEquals(networks.size(), number + 1);

        NetworkInstance networkOut = networkInstanceDao.load(NETWORK_NAME+2,VDC, REGION);
        assertNotNull(networkOut);
        assertEquals(networkOut.getNetworkName(), NETWORK_NAME+2);
        assertEquals(networkOut.getSubNets().size(), 1);

        for (SubNetworkInstance subNet2 : networkOut.getSubNets()) {
            assertEquals(subNet2.getName(), SUB_NETWORK_NAME);
        }

    }

    @Test(expected = EntityNotFoundException.class)
    public void testDestroyNetworkInstNoSubNetInst() throws Exception {

        NetworkInstance network = new NetworkInstance(NETWORK_NAME+3, "vdc", REGION);
        network = networkInstanceDao.create(network);
        networkInstanceDao.remove(network);
        networkInstanceDao.load(NETWORK_NAME+3,VDC, REGION);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteNetworkIInstanceWithSubNets() throws Exception {

        SubNetworkInstance subNet = new SubNetworkInstance(SUB_NETWORK_NAME+2,VDC, REGION);
        subNet = subNetworkInstanceDao.create(subNet);
        Set<SubNetworkInstance> subNets = new HashSet<SubNetworkInstance>();
        subNets.add(subNet);
        NetworkInstance network = new NetworkInstance(NETWORK_NAME+4, "vdc", REGION);
        network.setSubNets(subNets);
        network = networkInstanceDao.create(network);
        assertNotNull(network);
        Set<SubNetworkInstance> subNetOut = network.cloneSubNets();
        network.getSubNets().clear();

        networkInstanceDao.remove(network);
        for (SubNetworkInstance subNet2 : subNetOut) {
            subNetworkInstanceDao.remove(subNet2);
        }
        networkInstanceDao.load(NETWORK_NAME + 4,VDC, REGION);

    }

    @Test(expected=EntityNotFoundException.class)
    public void testDeleteSubNet() throws InvalidEntityException, AlreadyExistsEntityException, EntityNotFoundException {

        SubNetworkInstance subNet = new SubNetworkInstance(SUB_NETWORK_NAME+3, VDC, REGION);
        subNet = subNetworkInstanceDao.create(subNet);
        subNetworkInstanceDao.remove(subNet); 
        subNetworkInstanceDao.load(SUB_NETWORK_NAME+3, VDC, REGION);
    }
    
    @Test
    public void testExistsSubNet() throws InvalidEntityException, AlreadyExistsEntityException {

        SubNetworkInstance subNet = new SubNetworkInstance(SUB_NETWORK_NAME+3, VDC, REGION);
        subNet = subNetworkInstanceDao.create(subNet);
        boolean result = subNetworkInstanceDao.exists(SUB_NETWORK_NAME+3, VDC, REGION);
        assertEquals (result, true);
     
       
    }
    
    @Test
    public void testUpdateSubNetInstance() throws Exception {

        NetworkInstance network = new NetworkInstance(NETWORK_NAME+5, "vdc", REGION);
        SubNetworkInstance subnet = new SubNetworkInstance(SUB_NETWORK_NAME+4,VDC, REGION, CIDR_ID);
        subnet = subNetworkInstanceDao.create(subnet);
        network.addSubNet(subnet);
        network = networkInstanceDao.create(network);
        subnet.setCidr(CIDR2);
        subnet = subNetworkInstanceDao.update(subnet);
        
        network.updateSubNet(subnet);
        networkInstanceDao.update(network);
        
        assertEquals(network.getSubNets().size(), 1);
     
        for (SubNetworkInstance subNet: network.getSubNets()) {
            assertEquals(subNet.getName(), SUB_NETWORK_NAME+4);
            assertEquals(subNet.getCidr(), CIDR2);
        }
        
        
        network = networkInstanceDao.load(NETWORK_NAME+5,VDC, REGION);
        assertEquals(network.getSubNets().size(), 1);
        for (SubNetworkInstance subNet: network.getSubNets()) {
            assertEquals(subNet.getName(), SUB_NETWORK_NAME+4);
            assertEquals(subNet.getCidr(), CIDR2);
        }
    }
}
