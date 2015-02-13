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

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;




import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.claudia.NetworkClient;
import com.telefonica.euro_iaas.paasmanager.dao.NetworkInstanceDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.manager.impl.NetworkInstanceManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.Port;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;
import com.telefonica.euro_iaas.paasmanager.model.SubNetwork;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Network, SubNetwork and Router Manager.
 * 
 * @author henar
 */
public class NetworkInstanceManagerImplTest {

    private static String NETWORK_NAME = "name";
    private static String SUB_NETWORK_NAME = "subname";
    private static String CIDR = "10.100.1.0/24";

    private NetworkInstanceManagerImpl networkInstanceManager;
    private NetworkInstanceDao networkInstanceDao;
    private NetworkClient networkClient = null;
    private SubNetworkInstanceManager subNetworkInstanceManager = null;
    private RouterManager routerManager = null;
    private SystemPropertiesProvider systemPropertiesProvider = null;

    @Before
    public void setUp() throws Exception {

        networkInstanceManager = new NetworkInstanceManagerImpl();
        networkInstanceDao = mock(NetworkInstanceDao.class);
        networkInstanceManager.setNetworkInstanceDao(networkInstanceDao);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        networkInstanceManager.setSystemPropertiesProvider(systemPropertiesProvider);
        networkClient = mock(NetworkClient.class);

        subNetworkInstanceManager = mock(SubNetworkInstanceManager.class);
        routerManager = mock(RouterManager.class);
        networkInstanceManager.setNetworkClient(networkClient);
        networkInstanceManager.setSubNetworkInstanceManager(subNetworkInstanceManager);
        networkInstanceManager.setRouterManager(routerManager);

    }

    /**
     * It tests the creation of a network.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateNetwork() throws Exception {
        // Given
        Network net = new Network(NETWORK_NAME, "vdc", "region");
        SubNetwork subNet = new SubNetwork(SUB_NETWORK_NAME,"vdc", "region");
        net.addSubNet(subNet);
        NetworkInstance netInst = net.toNetworkInstance();
        netInst.setIdNetwork("ID");
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        when(networkInstanceDao.load(any(String.class),any(String.class),any(String.class))).thenThrow(
                new EntityNotFoundException(Network.class, "test", net));
        when(systemPropertiesProvider.getProperty("key")).thenReturn("VALUE");
        Mockito.doNothing().when(networkClient)
                .deployNetwork(any(ClaudiaData.class), any(NetworkInstance.class), anyString());
        Mockito.doNothing().when(networkClient)
                .addNetworkToPublicRouter(any(ClaudiaData.class), any(NetworkInstance.class), anyString());
        when(subNetworkInstanceManager.create(any(ClaudiaData.class), any(SubNetworkInstance.class), anyString()))
                .thenReturn(subNet.toInstance("vdc", "region"));
        Mockito.doNothing().when(routerManager)
                .create(any(ClaudiaData.class), any(RouterInstance.class), any(NetworkInstance.class), anyString());
        when(networkInstanceDao.create(any(NetworkInstance.class))).thenReturn(netInst);

        // Verify
        NetworkInstance netInstOut = networkInstanceManager.create(claudiaData, netInst, "region");
        assertEquals(netInstOut.getNetworkName(), NETWORK_NAME);
        assertEquals(netInstOut.getSubNets().size(), 1);
        for (SubNetworkInstance subNet2 : netInstOut.getSubNets()) {
            assertEquals(subNet2.getName(), SUB_NETWORK_NAME);
        }

    }
    
    @Test
    public void testCreateNetworkAlreadyExistsInDB() throws Exception {
        // Given
        Network net = new Network(NETWORK_NAME, "vdc", "region");
        SubNetwork subNet = new SubNetwork(SUB_NETWORK_NAME,"vdc", "region");
        net.addSubNet(subNet);
        NetworkInstance netInst = net.toNetworkInstance();
        netInst.setIdNetwork("ID");
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        when(networkInstanceDao.load(any(String.class),any(String.class),any(String.class))).thenReturn(netInst);
        when(systemPropertiesProvider.getProperty("key")).thenReturn("VALUE");
        when(networkClient.loadNetwork(any(ClaudiaData.class), any(NetworkInstance.class), anyString())).thenThrow(
                new EntityNotFoundException(Network.class, "test", net));
       
        Mockito.doNothing().when(networkClient)
                .addNetworkToPublicRouter(any(ClaudiaData.class), any(NetworkInstance.class), anyString());
        when(subNetworkInstanceManager.create(any(ClaudiaData.class), any(SubNetworkInstance.class), anyString()))
                .thenReturn(subNet.toInstance("vdc", "region"));
        Mockito.doNothing().when(routerManager)
                .create(any(ClaudiaData.class), any(RouterInstance.class), any(NetworkInstance.class), anyString());
        when(networkInstanceDao.create(any(NetworkInstance.class))).thenReturn(netInst);

        // Verify
        NetworkInstance netInstOut = networkInstanceManager.create(claudiaData, netInst, "region");
        assertEquals(netInstOut.getNetworkName(), NETWORK_NAME);
       

    }

    /**
     * It tests the creation of a network.
     * @throws EntityNotFoundException 
     * @throws InfrastructureException 
     * @throws AlreadyExistsEntityException 
     * @throws InvalidEntityException 
     * 
     * @throws Exception
     */

    @Test(expected=InfrastructureException.class)
    public void testCreateNetworkSubNetFailure() throws EntityNotFoundException, InfrastructureException, InvalidEntityException, AlreadyExistsEntityException {
        // Given
        Network net = new Network(NETWORK_NAME, "vdc", "region");
        SubNetwork subNet = new SubNetwork(SUB_NETWORK_NAME, "vdc", "region");
        net.addSubNet(subNet);
        NetworkInstance netInst = net.toNetworkInstance();
        netInst.setIdNetwork("ID");
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        when(networkInstanceDao.load(any(String.class),any(String.class),any(String.class))).thenThrow(
                new EntityNotFoundException(Network.class, "test", net));
        when(systemPropertiesProvider.getProperty("key")).thenReturn("VALUE");
        Mockito.doNothing().when(networkClient)
                .deployNetwork(any(ClaudiaData.class), any(NetworkInstance.class), anyString());
        Mockito.doNothing().when(networkClient)
                .addNetworkToPublicRouter(any(ClaudiaData.class), any(NetworkInstance.class), anyString());
        when(subNetworkInstanceManager.create(any(ClaudiaData.class), any(SubNetworkInstance.class), anyString()))
                .thenThrow(InfrastructureException.class);
        when(
                subNetworkInstanceManager.isSubNetworkDeployed(any(ClaudiaData.class), any(SubNetworkInstance.class),
                        anyString())).thenReturn(false);

        Mockito.doNothing().when(routerManager)
                .create(any(ClaudiaData.class), any(RouterInstance.class), any(NetworkInstance.class), anyString());
        when(networkInstanceDao.create(any(NetworkInstance.class))).thenReturn(netInst);

        // Verify
        networkInstanceManager.create(claudiaData, netInst, "region");
    }

    /**
     * It tests the creation of a network.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateNetworkAlreadyExist() throws Exception {
        // Given
        Network net = new Network(NETWORK_NAME, "vdc", "region");
        SubNetwork subNet = new SubNetwork(SUB_NETWORK_NAME, "vdc", "region");
        net.addSubNet(subNet);
        NetworkInstance netInst = net.toNetworkInstance();
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        when(networkInstanceDao.load(any(String.class),any(String.class),any(String.class))).thenReturn(netInst);
        when(systemPropertiesProvider.getProperty("key")).thenReturn("VALUE");
        Mockito.doNothing().when(networkClient)
                .deployNetwork(any(ClaudiaData.class), any(NetworkInstance.class), anyString());
        when(subNetworkInstanceManager.create(any(ClaudiaData.class), any(SubNetworkInstance.class), anyString()))
                .thenReturn(subNet.toInstance("vdc", "region"));
        Mockito.doNothing().when(routerManager)
                .create(any(ClaudiaData.class), any(RouterInstance.class), any(NetworkInstance.class), anyString());
        when(networkInstanceDao.create(any(NetworkInstance.class))).thenReturn(netInst);

        NetworkInstance netInstOut = networkInstanceManager.create(claudiaData, netInst, "region");

        // Verify
        assertEquals(netInstOut.getNetworkName(), NETWORK_NAME);
        assertEquals(netInstOut.getSubNets().size(), 1);

    }

    /**
     * It tests the destruction of a network.
     * 
     * @throws Exception
     */
    @Test
    public void testDestroyNetwork() throws Exception {
        // Given
        NetworkInstance net = new NetworkInstance(NETWORK_NAME, "VDC", "region");
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
       
        when(systemPropertiesProvider.getProperty("key")).thenReturn("VALUE");
        Mockito.doNothing().when(networkClient)
                .deployNetwork(any(ClaudiaData.class), any(NetworkInstance.class), anyString());
        when(networkInstanceDao.load(any(String.class),any(String.class),any(String.class))).thenReturn(net);
        Mockito.doNothing().when(subNetworkInstanceManager)
                .delete(any(ClaudiaData.class), any(SubNetworkInstance.class), anyString());
        Mockito.doNothing().when(networkInstanceDao).remove(any(NetworkInstance.class));

        // Verify
        networkInstanceManager.delete(claudiaData, net, "region");
        verify(networkInstanceDao).remove(any(NetworkInstance.class));

    }
    
    @Test
    public void testCanbeDeleted() throws Exception {
        // Given
        NetworkInstance net = new NetworkInstance(NETWORK_NAME, "VDC", "region");
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        List<Port> ports = new ArrayList<Port> ();
        ports.add(new Port ());
        when(networkClient.listPortsFromNetwork(any(ClaudiaData.class), anyString(), anyString())).thenReturn(ports);

        // Verify
        boolean result = networkInstanceManager.canBeDeleted(claudiaData, net, "region");
        assertEquals (result, false);
    }

    @Test
    public void testDestroyNetworkErrorInInterface () throws Exception {
        // Given
        NetworkInstance net = new NetworkInstance(NETWORK_NAME, "VDC", "region");
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        List<Port> ports = new ArrayList<Port> ();
        ports.add(new Port ());
        when(networkClient.listPortsFromNetwork(any(ClaudiaData.class), anyString(),
            anyString())).thenReturn(ports);
        Mockito.doThrow(new InfrastructureException("")).when(networkClient).
            deleteNetworkToPublicRouter(any(ClaudiaData.class),
            any(NetworkInstance.class), anyString());
        // Verify
        boolean result = networkInstanceManager.canBeDeleted(claudiaData, net, "region");
        assertEquals (result, false);
    }
    
    @Test
    public void testNetworkInstExistsinDBByNotOPenstack() throws Exception {
        // Given
        NetworkInstance net = new NetworkInstance(NETWORK_NAME, "VDC", "region");
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        List<Port> ports = new ArrayList<Port> ();
        ports.add(new Port ());
        when(networkInstanceDao.load(anyString(),anyString(),anyString())).thenReturn(net);
        when (networkClient.loadNetwork(any(ClaudiaData.class), any(NetworkInstance.class),  anyString())).thenThrow(EntityNotFoundException.class);

        // Verify
        boolean result = networkInstanceManager.exists(claudiaData, net, "region");
        assertEquals (result, false);
    }
    
    @Test
    public void testNetworkInstNoExistsinDBByNotOPenstack() throws Exception {
        // Given
        NetworkInstance net = new NetworkInstance(NETWORK_NAME, "VDC", "region");
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        List<Port> ports = new ArrayList<Port> ();
        ports.add(new Port ());
        when(networkInstanceDao.load(anyString(),anyString(),anyString())).thenThrow(EntityNotFoundException.class);
        when (networkClient.loadNetwork(any(ClaudiaData.class), any(NetworkInstance.class),  anyString())).thenThrow(EntityNotFoundException.class);

        // Verify
        boolean result = networkInstanceManager.exists(claudiaData, net, "region");
        assertEquals (result, false);
    }
    
    @Test
    public void testNetworkExists() throws Exception {
        // Given
        NetworkInstance net = new NetworkInstance(NETWORK_NAME, "VDC", "region");
        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        List<Port> ports = new ArrayList<Port> ();
        ports.add(new Port ());
        when(networkInstanceDao.load(anyString(),anyString(),anyString())).thenReturn(net);
        when (networkClient.loadNetwork(any(ClaudiaData.class), any(NetworkInstance.class),  anyString())).thenThrow(EntityNotFoundException.class);

        // Verify
        boolean result = networkInstanceManager.exists(claudiaData, net, "region");
        assertEquals (result, false);
    }

    @Test
    public void testNetworkExistsInOpenstackbutNotBD() throws Exception {
        // Given
        NetworkInstance net = new NetworkInstance(NETWORK_NAME, "VDC", "region");
        SubNetworkInstance subNet = new SubNetworkInstance ();
        net.addSubNet(subNet);

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        List<Port> ports = new ArrayList<Port> ();
        ports.add(new Port ());
        when(networkInstanceDao.load(anyString(),anyString(),anyString())).thenThrow(EntityNotFoundException.class);
        when (networkClient.loadNetwork(any(ClaudiaData.class), any(NetworkInstance.class),  anyString())).thenReturn(net);
        when (subNetworkInstanceManager.createInBD(any(SubNetworkInstance.class))).thenReturn(subNet);
         // Verify
        boolean result = networkInstanceManager.exists(claudiaData, net, "region");
        assertEquals (result, true);
    }

    @Test
    public void testGetDefaultCIDRMaxNetworkAchieved() throws Exception {
        networkInstanceManager.MAX_NETWORK=4;
        networkInstanceManager.NET_ELEMENT = 2;
        // Given
        NetworkInstance net = new NetworkInstance(NETWORK_NAME, "VDC", "region");
        SubNetworkInstance subNet = new SubNetworkInstance ();
        subNet.setCidr("another");
        net.addSubNet(subNet);
        List<NetworkInstance> nets = new ArrayList<NetworkInstance> ();
        nets.add(net);
        nets.add(net);
        nets.add(net);
        nets.add(net);

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "");

        // When

        when (networkClient.loadAllNetwork(any(ClaudiaData.class),anyString())).thenReturn(nets);
        // Verify
        String cidr = networkInstanceManager.getDefaultCidr(claudiaData, "region");
        assertEquals (cidr, "10.3.1.0/24");
    }

    @Test
    public void testGetDefaultCIDROnet() throws Exception {
        // Given
        networkInstanceManager.MAX_NETWORK=254;
        networkInstanceManager.NET_ELEMENT = 2;
        NetworkInstance net = new NetworkInstance(NETWORK_NAME, "VDC", "region");
        SubNetworkInstance subNet = new SubNetworkInstance ();
        subNet.setCidr("another");
        net.addSubNet(subNet);
        List<NetworkInstance> nets = new ArrayList<NetworkInstance> ();
        nets.add(net);

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "");

        // When

        when (networkClient.loadAllNetwork(any(ClaudiaData.class),anyString())).thenReturn(nets);
        // Verify
        String cidr = networkInstanceManager.getDefaultCidr(claudiaData, "region");
        assertEquals (cidr, "10.2.2.0/24");
    }

    @Test
    public void testGetDefaultCIDRAlreadyExists() throws Exception {
        // Given
        networkInstanceManager.MAX_NETWORK=254;
        networkInstanceManager.NET_ELEMENT = 2;
        NetworkInstance net = new NetworkInstance(NETWORK_NAME,
            "VDC", "region");
        SubNetworkInstance subNet = new SubNetworkInstance ();
        subNet.setCidr("10.2.2.0/24");

        List<NetworkInstance> nets = new ArrayList<NetworkInstance> ();
        nets.add(net);
        List<SubNetworkInstance> subNets =
            new ArrayList<SubNetworkInstance>();
        subNets.add(subNet);

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "");

        // When

        when (networkClient.loadAllNetwork(any(ClaudiaData.class),
            anyString())).thenReturn(nets);
        when (networkClient.loadAllSubNetworks(any(ClaudiaData.class),
            anyString())).thenReturn(subNets);
        // Verify
        String cidr = networkInstanceManager.
            getDefaultCidr(claudiaData, "region");
        assertEquals (cidr, "10.2.3.0/24");
    }

    @Test
    public void testGetDefaultCIDRAlreadyExistsTwoNets() throws Exception {
        // Given
        networkInstanceManager.MAX_NETWORK=254;
        networkInstanceManager.NET_ELEMENT = 2;
        NetworkInstance net = new NetworkInstance(NETWORK_NAME,
            "VDC", "region");

        SubNetworkInstance subNet = new SubNetworkInstance ();
        subNet.setCidr("10.2.3.0/24");
        SubNetworkInstance subNet2 = new SubNetworkInstance ();
        subNet2.setCidr("10.2.4.0/24");

        List<NetworkInstance> nets = new ArrayList<NetworkInstance> ();
        nets.add(net);
        nets.add(net);
        List<SubNetworkInstance> subNets =
            new ArrayList<SubNetworkInstance>();
        subNets.add(subNet);
        subNets.add(subNet2);

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "");

        // When

        when (networkClient.loadAllNetwork(any(ClaudiaData.class),
            anyString())).thenReturn(nets);
        when (networkClient.loadAllSubNetworks(any(ClaudiaData.class),
            anyString())).thenReturn(subNets);
        // Verify
        String cidr = networkInstanceManager.
            getDefaultCidr(claudiaData, "region");
        assertEquals (cidr, "10.2.5.0/24");
    }


}
