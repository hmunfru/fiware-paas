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

package com.telefonica.euro_iaas.paasmanager.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.model.dto.NetworkDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierPDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;

/**
 * Test for Tier entity.
 *
 * @author henar
 */
public class TierTest extends TestCase {

    /**
     * Test the creation of the enviornment.
     *
     * @throws Exception
     */
    @Test
    public void testTier() throws Exception {

        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(1);

        Tier tier = new Tier("tier", maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes", null);
        tier.setRegion("regionOne");

        SecurityGroup securityGroup = new SecurityGroup("nanme", "description");
        /*securityGroup.addRule(new Rule("ipProtocol", "fromPort", "toPort", "sourceGroup", "cidr"));
        tier.setSecurityGroup(securityGroup);*/

        TierDto tierDto = tier.toDto();
        Tier tier3 = tierDto.fromDto("vdc", "env");

       // assertEquals(tier.getSecurityGroup().getRules().size(), 1);
        assertEquals(tier.getInitialNumberInstances(), initial);
        assertEquals(tier.getMaximumNumberInstances(), maximum);
        assertEquals(tier.getMinimumNumberInstances(), minimum);
        assertEquals(tier3.equals(tier), true);

    }

    /**
     * Test the creation of a tier with network.
     * @throws Exception
     */
    @Test
    public void testTierWithNetwork() throws Exception {

        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(1);

        Tier tier = new Tier("tier", maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes", null);

        Network net = new Network("net", "vdc", "region");
        tier.addNetwork(net);

        for (Network netOut : tier.getNetworks()) {
            assertEquals(netOut.getNetworkName(), "net");
        }

    }

    /**
     * Test the creation of a tier from a Dto with no affinity information.
     * @throws Exception
     */
    @Test
    public void testTierFromDtoNoAffinity() throws Exception {

        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(3);

        TierDto tierDto = new TierDto("tier", maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes");
        Tier tier = tierDto.fromDto("vdc", "envName");

        assertEquals(tier.getName(), "tier");
        assertEquals(tier.getMaximumNumberInstances().intValue(), 3);
        assertEquals(tier.getMinimumNumberInstances().intValue(), 1);
        assertEquals(tier.getInitialNumberInstances().intValue(), 2);
        assertEquals(tier.getFlavour(), "2");
        assertEquals(tier.getImage(), "image");
        assertEquals(tier.getIcono(), "icono");
        assertEquals(tier.getKeypair(), "keypair");
        assertEquals(tier.getFloatingip(), "yes");
        assertEquals(tier.getAffinity(), "None");
    }

    /**
     * Test the creation of a tier from a Dto with affinity information.
     * @throws Exception
     */
    @Test
    public void testTierFromDtoAffinity() throws Exception {

        Integer minimumNumberInstances = new Integer(1);
        Integer initialNumberInstances = new Integer(2);
        Integer maximumNumberInstances = new Integer(3);

        TierDto tierDto = new TierDto();
        tierDto.setAffinity("affinity");
        tierDto.setName("tier");
        tierDto.setMaximumNumberInstances(maximumNumberInstances);
        tierDto.setMinimumNumberInstances(minimumNumberInstances);
        tierDto.setInitialNumberInstances(initialNumberInstances);
        tierDto.setFlavour("2");
        tierDto.setIcono("icono");
        tierDto.setImage("image");
        tierDto.setKeypair("keypair");
        tierDto.setFloatingip("true");
        ProductReleaseDto product = new ProductReleaseDto("product", "version");
        Set<Attribute> atts = new HashSet<Attribute>();
        atts.add(new Attribute("key", "value"));
        product.setPrivateAttributes(atts);
        tierDto.addProductRelease(product);
        Tier tier = tierDto.fromDto("vdc", "envName");


        assertEquals(tier.getName(), "tier");
        assertEquals(tier.getMaximumNumberInstances().intValue(), 3);
        assertEquals(tier.getMinimumNumberInstances().intValue(), 1);
        assertEquals(tier.getInitialNumberInstances().intValue(), 2);
        assertEquals(tier.getFlavour(), "2");
        assertEquals(tier.getImage(), "image");
        assertEquals(tier.getIcono(), "icono");
        assertEquals(tier.getKeypair(), "keypair");
        assertEquals(tier.getFloatingip(), "true");
        assertEquals(tier.getAffinity(), "affinity");
        assertEquals(tier.getProductReleases().get(0).getAttributes().size(), 1);


        TierDto tierDto2 = new TierDto("tier", maximumNumberInstances, minimumNumberInstances,
                initialNumberInstances, null, "2", "image", "icono", "keypair", "yes", "affinity");

        tierDto2.addProductRelease(new ProductReleaseDto());
        tierDto2.addNetworkDto(new NetworkDto());

        assertEquals(tierDto2.getAffinity(), "affinity");
        assertEquals(tierDto2.getProductReleaseDtos().size(), 1);
        assertEquals(tierDto2.getNetworksDto().size(), 1);


    }

    /**
     * Test the creation of a new Tier Instance.
     * @throws Exception
     */
    @Test
    public void testTierInstance() throws Exception {

        // Given
        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(1);

        NetworkInstance networkInstance = new NetworkInstance("net", "vdc", "region");
        networkInstance.setIdNetwork("ID");

        Tier tier = new Tier("tier", maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes", null);
        VM vm = new VM();
        vm.setFqn("services.vees.fqn");
        TierInstance tierInst = new TierInstance();
        tierInst.setName("tier");
        tierInst.addNetworkInstance(networkInstance);
        tierInst.setTier(tier);
        tier.setRegion("RegionOne");
        tier.setAffinity("affinity");
        tierInst.setVM(vm);

        // When
        JSONObject jsonObject = JSONObject.fromObject(tierInst.toJson(null));

        // Then
        assertEquals("keypair", JSONObject.fromObject(jsonObject.get("server")).get("key_name"));
        assertEquals("2", JSONObject.fromObject(jsonObject.get("server")).get("flavorRef"));
        assertEquals("image", JSONObject.fromObject(jsonObject.get("server")).get("imageRef"));
        assertEquals("tier", JSONObject.fromObject(jsonObject.get("server")).get("name"));
        assertEquals("RegionOne", JSONObject
                .fromObject(JSONObject.fromObject(jsonObject.get("server")).get("metadata")).get("region"));
        assertEquals("ID",
                JSONObject.fromObject(JSONObject.fromObject(jsonObject.get("server")).getJSONArray("networks").get(0))
                        .get("uuid"));

    }

    /**
     * Test the creation of a Tier instance dto with other information.
     */
    @Test
    public void testTierInstanceDtoII() {
        List<ProductInstanceDto> productInstanceDtos = new ArrayList();
        TierInstanceDto tierInstanceDto = new TierInstanceDto("tiername", 1, productInstanceDtos);
        tierInstanceDto.addProductInstanceDto(new ProductInstanceDto());
        tierInstanceDto.setAttributes(new HashSet<Attribute>());
        assertEquals(tierInstanceDto.getReplicaNumber(), 1);
        assertEquals(tierInstanceDto.getTierInstanceName(), "tiername");
        assertEquals(tierInstanceDto.getProductInstanceDtos().size(), 1);
    }

    /**
     * Test the creation of a tierPDto.
     */
    @Test
    public void testTierPDto() {
        TierPDto tierDto = new TierPDto("tier");
        Integer minimumNumberInstances = new Integer(1);
        Integer initialNumberInstances = new Integer(2);
        Integer maximumNumberInstances = new Integer(3);

        tierDto.setMaximumNumberInstances(maximumNumberInstances);
        tierDto.setMinimumNumberInstances(minimumNumberInstances);
        tierDto.setInitialNumberInstances(initialNumberInstances);
        tierDto.setFlavour("2");
        tierDto.setIcono("icono");
        tierDto.setImage("image");
        tierDto.setKeypair("keypair");
        tierDto.setFloatingip("true");

        assertEquals(tierDto.getName(), "tier");
        assertEquals(tierDto.getMaximumNumberInstances().intValue(), 3);
        assertEquals(tierDto.getMinimumNumberInstances().intValue(), 1);
        assertEquals(tierDto.getInitialNumberInstances().intValue(), 2);
        assertEquals(tierDto.getFlavour(), "2");
        assertEquals(tierDto.getImage(), "image");
        assertEquals(tierDto.getIcono(), "icono");
        assertEquals(tierDto.getKeypair(), "keypair");
        assertEquals(tierDto.getFloatingip(), "true");
    }


}
