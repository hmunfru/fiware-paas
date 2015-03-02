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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.paasmanager.claudia.impl.ClaudiaClientOpenStackImpl;
import com.telefonica.euro_iaas.paasmanager.manager.NetworkInstanceManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import com.telefonica.euro_iaas.paasmanager.util.FileUtilsImpl;
import com.telefonica.euro_iaas.paasmanager.util.OpenStackRegion;
import com.telefonica.euro_iaas.paasmanager.util.OpenStackUtil;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * @author jesus.movilla
 */
public class ClaudiaClientOpenStackImplTest {

    private Tier tier;
    private ClaudiaData claudiaData;
    private OpenStackUtil openStackUtil;
    private FileUtilsImpl fileUtils;
    private OpenStackRegion openStackRegion;
    private NetworkInstanceManager networkInstanceManager;
    private ClaudiaClientOpenStackImpl claudiaClientOpenStack;
    private SystemPropertiesProvider systemPropertiesProvider;

    @Before
    public void setUp() throws Exception {
        String expectedNetworks = "<?xml version='1.0' encoding='UTF-8'?>\n"
                + "<networks xmlns=\"http://openstack.org/quantum/api/v2.0\""
                + " xmlns:provider=\"http://docs.openstack.org/ext/provider/api/v1.0\""
                + " xmlns:quantum=\"http://openstack.org/quantum/api/v2.0\" "
                + "xmlns:router=\"http://docs.openstack.org/ext/quantum/router/api/v1.0\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "<network><status>ACTIVE</status>"
                + "<subnets><subnet>e7118ac1-4aea-49f3-8ebf-383170453fd9</subnet></subnets>"
                + "<name>test</name><provider:physical_network xsi:nil=\"true\" />"
                + "<admin_state_up quantum:type=\"bool\">True</admin_state_up>"
                + "<tenant_id>08bed031f6c54c9d9b35b42aa06b51c0</tenant_id>"
                + "<provider:network_type>gre</provider:network_type>"
                + "<router:external quantum:type=\"bool\">False</router:external>"
                + "<shared quantum:type=\"bool\">False</shared><id>7a2ec35e-08ce-42bb-a4f4-cb6d775db171</id>"
                + "<provider:segmentation_id quantum:type=\"long\">1</provider:segmentation_id></network></networks>";

        String expectedSubnet = "{\"subnet\": {\"name\": \"\", \"enable_dhcp\": true,"
                + " \"network_id\": \"42699a17-2367-41bc-8155-e58b41afa361\","
                + " \"tenant_id\": \"67c979f51c5b4e89b85c1f876bdffe31\", \"dns_nameservers\": [],"
                + " \"allocation_pools\": [{\"start\": \"10.0.3.20\", \"end\": \"10.0.3.150\"}],"
                + " \"host_routes\": [], \"ip_version\": 4, \"gateway_ip\": \"10.0.3.1\","
                + " \"cidr\": \"10.0.3.0/24\", \"id\": \"e8fd2234-447f-4f2b-b340-dbe701304b00\"}}";
        String expectedRouter = "{\"router\": {\"status\": \"ACTIVE\", \"external_gateway_info\":"
                + " {\"network_id\": \"b5be6aa7-b27e-493d-a139-75e78156950f\"}, \"name\": \"another_router\","
                + " \"admin_state_up\": true, \"tenant_id\": \"67c979f51c5b4e89b85c1f876bdffe31\","
                + " \"id\": \"08b0bd71-4fc4-4192-af2c-539ad4e02d2f\"}}";
        String expectedNetwork = "{\"network\": {\"status\": \"ACTIVE\", \"subnets\": [], \"name\": \"network-demo\","
                + " \"router:external\": false, \"tenant_id\": \"67c979f51c5b4e89b85c1f876bdffe31\","
                + " \"admin_state_up\": false,"
                + " \"shared\": false, \"id\": \"d8023576-7743-4f98-8089-a1ccc60b5ec2\"}}";

        String userData = "# The default is to install from packages.\n" + "{networks}\n" + "\n" + "{if_up}\n" + "\n"
                + "# Key from http://apt.opscode.com/packages@opscode.com.gpg.key\n" + "runcmd:\n"
                + "     - mkdir /etc/chef\n"
                + "     - curl -L http://repositories.testbed.fi-ware.org/webdav/chef/install.sh | bash\n"
                + "     - OHAI_TIME_DIR=\"$(find / -name ohai_time.rb)\"\n"
                + "     - sed -i 's/ohai_time Time.now.to_f/ohai_time Time.now/' ${OHAI_TIME_DIR}\n"
                + "     - mkdir -p /var/log/chef\n" + "     - chef-client -i 60 -s 6 -L /var/log/chef/client.log\n"
                + "\n" + "\n" + "\n" + "chef:\n" + "\n" + " # Valid values are 'gems' and 'packages' and 'omnibus'\n"
                + " install_type: \"packages\"\n" + "\n" + " # Boolean: run 'install_type' code even if chef-client\n"
                + " #          appears already installed.\n" + " force_install: false\n" + " node_name: {node_name}\n"
                + " # Chef settings\n" + " server_url: {server_url}\n" + " # Node Name\n"
                + " # Defaults to the instance-id if not present\n" + " validation_name: \"chef-validator\"\n"
                + " # Node Name\n" + " # Defaults to the instance-id if not present\n" + "\n"
                + " # Default validation name is chef-validator\n" + " validation_key: |\n" + "{validation_key}\n"
                + "\n" + "puppet:\n" + " # Every key present in the conf object will be added to puppet.conf:\n"
                + " # [name]\n" + " # subkey=value\n" + " #\n"
                + " # For example the configuration below will have the following section\n"
                + " # added to puppet.conf:\n" + " # [puppetd]\n" + " # server=puppetmaster.example.org\n"
                + " # certname=i-0123456.ip-X-Y-Z.cloud.internal\n" + " #\n"
                + " # The puppmaster ca certificate will be available in\n" + " # /var/lib/puppet/ssl/certs/ca.pem\n"
                + " conf:\n" + "   agent:\n" + "     server: \"{puppet_master}\"\n" + "     runinterval: \"60\"\n"
                + "     pluginsync: true\n" + "\n" + "\n" + "\n" + " # Capture all subprocess output into a logfile\n"
                + "# Useful for troubleshooting cloud-init issues\n"
                + "output: {all: '| tee -a /var/log/cloud-init-output.log'}\n";

        PaasManagerUser user = new PaasManagerUser("username", "myToken", new HashSet<GrantedAuthority>());
        user.setTenantName("FIWARE");
        claudiaData = new ClaudiaData("vdc", "org", "service");
        claudiaData.setUser(user);

        tier = new Tier();
        tier.setFlavour("2");
        tier.setImage("44dcdba3-a75d-46a3-b209-5e9035d2435e");

        tier.setName("prueba");
        tier.setKeypair("jesusmovilla");

        claudiaClientOpenStack = new ClaudiaClientOpenStackImpl();

        openStackUtil = mock(OpenStackUtil.class);
        networkInstanceManager = mock(NetworkInstanceManager.class);
        openStackRegion = mock(OpenStackRegion.class);
        fileUtils = new FileUtilsImpl();
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        when(systemPropertiesProvider.getProperty(anyString())).thenReturn("src/test/resources/userdata");
        claudiaClientOpenStack.setNetworkInstanceManager(networkInstanceManager);
        claudiaClientOpenStack.setOpenStackUtil(openStackUtil);
        claudiaClientOpenStack.setFileUtils(fileUtils);
        claudiaClientOpenStack.setOpenStackRegion(openStackRegion);
        claudiaClientOpenStack.setSystemPropertiesProvider(systemPropertiesProvider);

        when(openStackUtil.createServer(any(String.class), anyString(), anyString(), anyString())).thenReturn(
                "response");
        when(openStackUtil.createNetwork(any(String.class), anyString(), anyString(), anyString())).thenReturn(
                expectedNetwork);
        when(openStackUtil.createSubNet(any(SubNetworkInstance.class), anyString(), anyString(), anyString()))
                .thenReturn(expectedSubnet);
        when(openStackUtil.createRouter(any(RouterInstance.class), anyString(), anyString(), anyString())).thenReturn(
                expectedRouter);
        when(
                openStackUtil.addRouterInterface(any(String.class), any(String.class), anyString(), anyString(),
                        anyString())).thenReturn("OK");
        when(openStackUtil.getNetworks(anyString(), anyString(), anyString())).thenReturn(expectedNetworks);
        // when(fileUtils.readFile(anyString())).thenReturn(userData);
    }

    @Test
    public void testDeployVMEssex() throws Exception {

        TierInstance tierInstance = new TierInstance();
        tierInstance.setTier(tier);
        VM vm = new VM();
        vm.setHostname("hotname");
        tierInstance.setVM(vm);

        when(openStackRegion.getChefServerEndPoint(anyString(), anyString())).thenReturn("http");
        when(openStackRegion.getPuppetMasterEndPoint(anyString(), anyString())).thenReturn("http");

        claudiaClientOpenStack.deployVM(claudiaData, tierInstance, 1, vm);
        verify(openStackUtil).createServer(any(String.class), anyString(), anyString(), anyString());

    }

    @Test
    public void testUserData() throws Exception {

        TierInstance tierInstance = new TierInstance();
        tierInstance.setTier(tier);
        VM vm = new VM();
        vm.setHostname("hotname");
        tierInstance.setVM(vm);
        NetworkInstance network = new NetworkInstance("NETWORK", "VDC", "REGION");
        tierInstance.addNetworkInstance(network);
        NetworkInstance network2 = new NetworkInstance("2", "VDC", "REGION");
        tierInstance.addNetworkInstance(network2);

        when(openStackRegion.getChefServerEndPoint(anyString(), anyString())).thenReturn("http");
        when(openStackRegion.getPuppetMasterEndPoint(anyString(), anyString())).thenReturn("http");

        String result = claudiaClientOpenStack.getUserData(claudiaData, tierInstance);
        assertNotNull(result);

    }

    @Test
    public void testDeployVMEGrizzlyNetwork() throws Exception {

        TierInstance tierInstance = new TierInstance();
        Network network = new Network("NETWORK", "VDC", "REGION");
        tier.addNetwork(network);
        tierInstance.setTier(tier);
        tierInstance.addNetworkInstance(network.toNetworkInstance());

        VM vm = new VM();
        vm.setHostname("hotname");
        tierInstance.setVM(vm);
        when(openStackRegion.getChefServerEndPoint(anyString(), anyString())).thenReturn("http");
        when(openStackRegion.getPuppetMasterEndPoint(anyString(), anyString())).thenReturn("http");
        claudiaClientOpenStack.deployVM(claudiaData, tierInstance, 1, vm);
        verify(openStackUtil).createServer(any(String.class), any(String.class), any(String.class), any(String.class));

    }

    @Test
    public void testDeployVMEGrizzlyNotNetwork() throws Exception {

        TierInstance tierInstance = new TierInstance();
        tierInstance.setTier(tier);

        Network network = new Network("NETWORK", "VDC", "REGION");
        NetworkInstance netInst = network.toNetworkInstance();
        netInst.setShared(false);
        netInst.setTenantId(claudiaData.getVdc());
        netInst.setIdNetwork("ID");
        List<NetworkInstance> networkInstances = new ArrayList<NetworkInstance>();
        networkInstances.add(netInst);

        VM vm = new VM();
        vm.setHostname("hotname");
        tierInstance.setVM(vm);
        when(openStackRegion.getChefServerEndPoint(anyString(), anyString())).thenReturn("http");
        when(openStackRegion.getPuppetMasterEndPoint(anyString(), anyString())).thenReturn("http");

        when(networkInstanceManager.listNetworks(any(ClaudiaData.class), any(String.class))).thenReturn(
                networkInstances);

        when(networkInstanceManager.load(any(String.class), any(String.class), any(String.class))).thenReturn(netInst);

        claudiaClientOpenStack.deployVM(claudiaData, tierInstance, 1, vm);

        verify(openStackUtil).createServer(any(String.class), any(String.class), any(String.class), any(String.class));

    }

    @Test
    public void testDeployVMEGrizzlyNotNetwork2() throws Exception {

        TierInstance tierInstance = new TierInstance();
        tierInstance.setTier(tier);

        Network network = new Network("NETWORK", "VDC", "REGION");
        NetworkInstance netInst = network.toNetworkInstance();
        netInst.setShared(true);
        List<NetworkInstance> networkInstances = new ArrayList<NetworkInstance>();
        networkInstances.add(netInst);

        VM vm = new VM();
        vm.setHostname("hotname");
        tierInstance.setVM(vm);
        NetworkInstance netInst2 = network.toNetworkInstance();
        netInst2.setShared(false);
        netInst2.setDefaultNet(true);
        when(openStackRegion.getChefServerEndPoint(anyString(), anyString())).thenReturn("http");
        when(openStackRegion.getPuppetMasterEndPoint(anyString(), anyString())).thenReturn("http");

        when(networkInstanceManager.listNetworks(any(ClaudiaData.class), any(String.class))).thenReturn(
                networkInstances);

        when(networkInstanceManager.create(any(ClaudiaData.class), any(NetworkInstance.class), any(String.class)))
                .thenReturn(netInst2);

        claudiaClientOpenStack.deployVM(claudiaData, tierInstance, 1, vm);
        assertEquals(tierInstance.getNetworkInstances().size(), 1);
        verify(openStackUtil).createServer(any(String.class), any(String.class), any(String.class), any(String.class));

    }

    @Test
    public void testGenerateInterfacesFileOneNet() throws Exception {

        TierInstance tierInstance = new TierInstance();

        Network network = new Network("NETWORK", "VDC", "REGION");
        NetworkInstance netInst = network.toNetworkInstance();
        tierInstance.addNetworkInstance(netInst);

        String file = claudiaClientOpenStack.writeInterfaces(tierInstance);
        assertNotNull(file);
        assertEquals(file, "");

    }

    @Test
    public void testGenerateInterfacesFileTwoNet() throws Exception {

        TierInstance tierInstance = new TierInstance();

        Network network = new Network("NETWORK", "VDC", "REGION");
        NetworkInstance netInst = network.toNetworkInstance();
        tierInstance.addNetworkInstance(netInst);
        tierInstance.addNetworkInstance(new Network("NETWORK2", "VDC", "REGION").toNetworkInstance());

        String file = claudiaClientOpenStack.writeInterfaces(tierInstance);
        System.out.println(file);
        assertNotNull(file);

    }

}
