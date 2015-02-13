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

package com.telefonica.euro_iaas.paasmanager.claudia.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.Port;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.util.OpenStackUtil;

public class OpenStackNetworkImplTest {
    OpenstackNetworkClientImpl openStackNetworkImpl = new OpenstackNetworkClientImpl();
    ClaudiaData claudiaData;
    private OpenStackUtil openStackUtil;
    private String REGION = "region";
    private String NETWORK_STRING= "{network: {\"status\": \"ACTIVE\", \"subnets\": " +
            "[\"2b7a07f6-0b73-46a1-9327-6911c0480f49\"], \"name\": "
            + " \"network\", \"provider:physical_network\": null, \"admin_state_up\": true, " +
            "\"tenant_id\": \"67c979f51c5b4e89b85c1f876bdffe31\", "
            + " \"provider:network_type\": \"gre\", \"router:external\": false, \"shared\": false, " +
            "\"id\": \"044aecbe-3975-4318-aad2-a1232dcde47d\", "
            + " \"provider:segmentation_id\": 8}}";

    private String NETWORKS_STRING_1NET= "{networks: {\"status\": \"ACTIVE\", \"subnets\": " +
            "[\"2b7a07f6-0b73-46a1-9327-6911c0480f49\"], \"name\": "
            + " \"network\", \"provider:physical_network\": null, \"admin_state_up\": true, " +
            "\"tenant_id\": \"67c979f51c5b4e89b85c1f876bdffe31\", "
            + " \"provider:network_type\": \"gre\", \"router:external\": false, \"shared\": false, " +
            "\"id\": \"044aecbe-3975-4318-aad2-a1232dcde47d\", "
            + " \"provider:segmentation_id\": 8}}";

    private String NETWORKS_STRING = "{\"networks\": [{\"status\": \"ACTIVE\", \"subnets\": " +
            "[\"2b7a07f6-0b73-46a1-9327-6911c0480f49\"], \"name\": "
            + " \"dia146\", \"provider:physical_network\": null, \"admin_state_up\":" +
            " true, \"tenant_id\": \"67c979f51c5b4e89b85c1f876bdffe31\", "
            + " \"provider:network_type\": \"gre\", \"router:external\": false, \"shared\":" +
            " false, \"id\": \"044aecbe-3975-4318-aad2-a1232dcde47d\", "
            + " \"provider:segmentation_id\": 8}, {\"status\": \"ACTIVE\", \"subnets\": " +
            "[\"e2d10e6b-33c3-400c-88d6-f905d4cd02f2\"], \"name\": \"ext-net\","
            + " \"provider:physical_network\": null, \"admin_state_up\": true, \"tenant_id\":" +
            " \"08bed031f6c54c9d9b35b42aa06b51c0\", \"provider:network_type\": "
            + " \"gre\", \"router:external\": true, \"shared\": false, \"id\": " +
            "\"080b5f2a-668f-45e0-be23-361c3a7d11d0\", \"provider:segmentation_id\": 1}"
            + "]}";

    private String SUB_NETWORKS_STRING = "{\"subnets\": " +
        "[{\"name\": \"demo-subnet\", \"enable_dhcp\": true, \"network_id\": " +
        "\"e9dcf592\", \"tenant_id\": \"0c6e5c00749b45999c88609049ee5c4b\", " +
        "\"dns_nameservers\": [], \"gateway_ip\": \"192.168.1.1\", " +
        "\"cidr\": \"192.168.1.0/24\", \"id\": \"6795f2f2-a103-4602-ab07-c5ea0ac737e7\"}," +
        "{\"name\": \"net2\", \"enable_dhcp\": true, \"network_id\": \"90689\", " +
        "\"tenant_id\": \"71c462bc3d204135b655cb59f7ea2620\", \"cidr\": \"10.0.9.0/24\", " +
        "\"id\": \"81a05896-5a2b-4b53-a433-a1ea3c728b21\"}]}";

    @Before
    public void setUp() {
        GrantedAuthority grantedAuthority = mock(GrantedAuthority.class);
        Collection<GrantedAuthority> authorities = new HashSet();
        authorities.add(grantedAuthority);
        PaasManagerUser paasManagerUser = new PaasManagerUser("08bed031f6c54c9d9b35b42aa06b51c0", "aa", authorities);
        claudiaData = new ClaudiaData("org", "08bed031f6c54c9d9b35b42aa06b51c0", "service");
        claudiaData.setUser(paasManagerUser);
        openStackUtil = mock(OpenStackUtil.class);
        openStackNetworkImpl.setOpenStackUtil(openStackUtil);
    }

    @Test
    public void shouldListNetworks() throws OpenStackException, InfrastructureException {

        // when
        when(openStackUtil.listNetworks(any(PaasManagerUser.class), anyString())).thenReturn(NETWORKS_STRING);

        List<NetworkInstance> networks = openStackNetworkImpl.loadAllNetwork(claudiaData, "region");

        // then
        assertNotNull(networks);
        verify(openStackUtil).listNetworks(any(PaasManagerUser.class), anyString());
        assertEquals(2, networks.size());
        assertEquals("dia146", networks.get(0).getNetworkName());
    }

    @Test
    public void shouldListPortsFromNetwork() throws OpenStackException, InfrastructureException {

        // when
        String response = "{\"ports\": [ {\"status\": \"ACTIVE\",\"name\": \"\",  \"admin_state_up\": true, \"network_id\": "
                + "\"6a609412-3f04-485c-b269-b1a9b9ecb6bf\", \"tenant_id\": \"08bed031f6c54c9d9b35b42aa06b51c0\",\"binding:vif_type\": \"ovs\","
                + "\"device_owner\": \"network:dhcp\", \"binding:capabilities\": {\"port_filter\": true}, \"mac_address\": \"fa:16:3e:54:1a:40\","
                + "\"fixed_ips\": [ {\"subnet_id\": \"601ec829-a166-4b39-9c74-166cc76f251b\", \"ip_address\": \"172.31.0.3\"}],"
                + "\"id\": \"07fd27d2-9ce1-48f3-be83-c7d2b7041a1a\", \"security_groups\": [], \"device_id\": \"dhcpfa3e6aae-2140-5176-877a-2f67684a3165-6a609412-3f04-485c-b269-b1a9b9ecb6bf\""
                + "}, {\"status\": \"ACTIVE\",\"name\": \"\",  \"admin_state_up\": true, \"network_id\": "
                + "\"6a609412-3f04-485c-b269-b1a9b9ecb6bf\", \"tenant_id\": \"08bed031f6c54c9d9b35b42aa06b51c0\",\"binding:vif_type\": \"ovs\","
                + "\"device_owner\": \"network:router_interface\", \"binding:capabilities\": {\"port_filter\": true}, \"mac_address\": \"fa:16:3e:54:1a:40\","
                + "\"fixed_ips\": [ {\"subnet_id\": \"601ec829-a166-4b39-9c74-166cc76f251b\", \"ip_address\": \"172.31.0.1\"}],"
                + "\"id\": \"07fd27d2-9ce1-48f3-be83-c7d2b7041a1a\", \"security_groups\": [], \"device_id\": \"dhcpfa3e6aae-2140-5176-877a-2f67684a3165-6a609412-3f04-485c-b269-b1a9b9ecb6bf\""
                + "}, {\"status\": \"ACTIVE\",\"name\": \"\",  \"admin_state_up\": true, \"network_id\": "
                + "\"6a609412-3f04-485c-b269-b1a9b9ecb6bf\", \"tenant_id\": \"08bed031f6c54c9d9b35b42aa06b51c0\",\"binding:vif_type\": \"ovs\","
                + "\"device_owner\": \"compute:None\", \"binding:capabilities\": {\"port_filter\": true}, \"mac_address\": \"fa:16:3e:54:1a:40\","
                + "\"fixed_ips\": [ {\"subnet_id\": \"601ec829-a166-4b39-9c74-166cc76f251b\", \"ip_address\": \"172.31.0.1\"}],"
                + "\"id\": \"07fd27d2-9ce1-48f3-be83-c7d2b7041a1a\", \"security_groups\": [], \"device_id\": \"dhcpfa3e6aae-2140-5176-877a-2f67684a3165-6a609412-3f04-485c-b269-b1a9b9ecb6bf\""
                + "}]}";

        when(openStackUtil.listPorts(any(PaasManagerUser.class), anyString())).thenReturn(response);

        List<Port> ports = openStackNetworkImpl.listPortsFromNetwork(claudiaData, "region",
                "6a609412-3f04-485c-b269-b1a9b9ecb6bf");

        // then
        assertNotNull(ports);
        verify(openStackUtil).listPorts(any(PaasManagerUser.class), anyString());
        assertEquals(1, ports.size());

    }

    @Test
    public void shouldLoadNotSharedNetworks() throws OpenStackException, InfrastructureException {

        // when
        when(openStackUtil.listNetworks(any(PaasManagerUser.class), anyString())).thenReturn(NETWORKS_STRING);

        List<NetworkInstance> networks = openStackNetworkImpl.loadNotSharedNetworks(claudiaData, REGION);

        // then
        assertNotNull(networks);

        verify(openStackUtil).listNetworks(any(PaasManagerUser.class), anyString());
        assertEquals(2, networks.size());
    }

    @Test
    public void shouldObtainDataNetwork() throws OpenStackException, InfrastructureException {

        // when
        when(openStackUtil.listNetworks(any(PaasManagerUser.class), anyString())).thenReturn(NETWORKS_STRING);

        List<NetworkInstance> networks = openStackNetworkImpl.loadAllNetwork(claudiaData, REGION);

        // then
        assertNotNull(networks);
        verify(openStackUtil).listNetworks(any(PaasManagerUser.class), anyString());
        assertEquals(2, networks.size());
        assertEquals(networks.get(0).getAdminStateUp(), true);
        assertEquals(networks.get(0).getNetworkName(), "dia146");
        assertEquals(networks.get(0).getIdNetwork(), "044aecbe-3975-4318-aad2-a1232dcde47d");
        assertEquals(networks.get(0).getShared(), false);
        assertEquals(networks.get(0).getTenantId(), "67c979f51c5b4e89b85c1f876bdffe31");

    }

    /**
     * This tests check the data obtaining to list all
     * subnets.
     * @throws OpenStackException
     * @throws InfrastructureException
     */
    @Test
    public void shouldObtainSubDataNetwork()
        throws OpenStackException, InfrastructureException {

        // when
        when(openStackUtil.listSubNetworks(any(PaasManagerUser.class),
            anyString())).thenReturn(SUB_NETWORKS_STRING);

        List<SubNetworkInstance> subNetworks =
            openStackNetworkImpl.loadAllSubNetworks(claudiaData, REGION);

        // then
        assertNotNull(subNetworks);
        verify(openStackUtil).listSubNetworks(any(PaasManagerUser.class), anyString());
        assertEquals(2, subNetworks.size());
        assertEquals(subNetworks.get(0).getCidr(), "192.168.1.0/24");

    }

    @Test
    public void shouldAddNetworkToPublicRouter() throws OpenStackException, InfrastructureException {

        // when
        String response = "response";
        NetworkInstance net = new NetworkInstance();
        when(
                openStackUtil.addInterfaceToPublicRouter(any(PaasManagerUser.class), any(NetworkInstance.class),
                        anyString())).thenReturn(response);

        openStackNetworkImpl.addNetworkToPublicRouter(claudiaData, net, REGION);

        verify(openStackUtil).addInterfaceToPublicRouter(any(PaasManagerUser.class), any(NetworkInstance.class),
                anyString());

    }

    @Test
    public void shouldDeployDefaultNetwork() throws OpenStackException, InfrastructureException {

        // when
        NetworkInstance net = new NetworkInstance("network", "vdc", "region");
        when(openStackUtil.createNetwork(any(String.class), anyString(), anyString(), anyString()))
        .thenReturn(NETWORK_STRING);

        NetworkInstance net2 = openStackNetworkImpl.deployDefaultNetwork(claudiaData, REGION);

        verify(openStackUtil).createNetwork(any(String.class), anyString(), anyString(), anyString());
        assertNotNull(net2);
        assertEquals(net2.getNetworkName(), "network");

    }

    @Test
    public void shouldDeployNetwork() throws OpenStackException, InfrastructureException {

        // when
        NetworkInstance net = new NetworkInstance("network", "vdc", "region");
        when(openStackUtil.createNetwork(any(String.class), anyString(), anyString(), anyString()))
                .thenReturn(NETWORK_STRING);

        openStackNetworkImpl.deployNetwork(claudiaData, net, REGION);

        verify(openStackUtil).createNetwork(any(String.class), anyString(), anyString(), anyString());
        assertNotNull(net.getIdNetwork());

    }

    @Test
    public void shouldDestroyNetwork() throws OpenStackException, InfrastructureException {

        // when
        String response = "ok";
        NetworkInstance net = new NetworkInstance("network", "vdc", "region");
        when(openStackUtil.deleteNetwork(any(String.class), anyString(), anyString(), anyString()))
                .thenReturn(response);

        openStackNetworkImpl.destroyNetwork(claudiaData, net, REGION);

        verify(openStackUtil).deleteNetwork(any(String.class), anyString(), anyString(), anyString());

    }

    @Test
    public void shouldDestroyRouter() throws OpenStackException, InfrastructureException {

        // when
        String response = "ok";
        RouterInstance net = new RouterInstance("router");
        when(openStackUtil.deleteRouter(any(String.class), anyString(), anyString(), anyString())).thenReturn(response);
        openStackNetworkImpl.destroyRouter(claudiaData, net, REGION);

        verify(openStackUtil).deleteRouter(any(String.class), anyString(), anyString(), anyString());
    }

    @Test
    public void shouldDeleteNetworkToPublicRouter() throws OpenStackException, InfrastructureException {

        // when
        String response = "ok";
        NetworkInstance net = new NetworkInstance("router", "vdc", "region");
        when(
                openStackUtil.deleteInterfaceToPublicRouter(any(PaasManagerUser.class), any(NetworkInstance.class),
                        anyString())).thenReturn(response);
        openStackNetworkImpl.deleteNetworkToPublicRouter(claudiaData, net, REGION);

        verify(openStackUtil).deleteInterfaceToPublicRouter(any(PaasManagerUser.class), any(NetworkInstance.class),
                anyString());
    }

    @Test
    public void shouldLoadNetworkNoId() throws OpenStackException, InfrastructureException, EntityNotFoundException {

        // when
        String response2 = "{\"status\": \"ACTIVE\", \"subnets\": " +
                "[\"2b7a07f6-0b73-46a1-9327-6911c0480f49\"], \"name\": "
                + " \"MYNET\", \"provider:physical_network\": null, \"admin_state_up\": " +
                "true, \"tenant_id\": \"67c979f51c5b4e89b85c1f876bdffe31\", "
                + " \"provider:network_type\": \"gre\", \"router:external\": false, " +
                "\"shared\": true, \"id\": \"044aecbe-3975-4318-aad2-a1232dcde47d\", "
                + " \"provider:segmentation_id\": 8}}";
        String response3 = "\n" +
                "\n" +
                "    {\n" +
                "       \"subnet\":\n" +
                "       {\n" +
                "           \"name\": \"dd\",\n" +
                "           \"enable_dhcp\": true,\n" +
                "           \"network_id\": \"33cb6d12-3792-4ff7-8abe-8f948ce60a4d\",\n" +
                "           \"tenant_id\": \"00000000000000000000000000000046\",\n" +
                "           \"dns_nameservers\":\n" +
                "           [\n" +
                "           ],\n" +
                "           \"allocation_pools\":\n" +
                "           [\n" +
                "               {\n" +
                "                   \"start\": \"12.1.0.2\",\n" +
                "                   \"end\": \"12.1.0.254\"\n" +
                "               }\n" +
                "           ],\n" +
                "           \"host_routes\":\n" +
                "           [\n" +
                "           ],\n" +
                "           \"ip_version\": 4,\n" +
                "           \"gateway_ip\": \"12.1.0.1\",\n" +
                "           \"cidr\": \"12.1.0.0/24\",\n" +
                "           \"id\": \"6959446a-204b-4e68-a5d2-f7a3ef5a442a\"\n" +
                "       }\n" +
                "    }\n" +
                "\n";
        NetworkInstance net = new NetworkInstance("dia146", "vdc", "region");
        when(openStackUtil.listNetworks(any(PaasManagerUser.class), anyString())).thenReturn(NETWORKS_STRING);
        when(openStackUtil.getNetworkDetails(anyString(),anyString(),anyString(),anyString())).thenReturn(response2);
        when(openStackUtil.getSubNetworkDetails(anyString(),anyString(),anyString(),anyString())).thenReturn(response3);

        openStackNetworkImpl.loadNetwork(claudiaData, net, REGION);
        assertNotNull(net.getIdNetwork());


    }

    /**
     * The test for loading the net when the id does not exist
     * @throws OpenStackException
     * @throws InfrastructureException
     */
    @Test
    public void shouldLoadNetworkNoIdNetNotExists() throws OpenStackException,
        InfrastructureException, JSONException {

        // when
        NetworkInstance net = new NetworkInstance("router", "vdc", "region");
        when(openStackUtil.listNetworks(any(PaasManagerUser.class), anyString())).thenReturn(NETWORK_STRING);

        try {
            openStackNetworkImpl.loadNetwork(claudiaData, net, REGION);
            fail("A Not entity exception should have been lanched");
        } catch (EntityNotFoundException e) {
            verify(openStackUtil).listNetworks(any(PaasManagerUser.class), anyString());
        }


    }

}
