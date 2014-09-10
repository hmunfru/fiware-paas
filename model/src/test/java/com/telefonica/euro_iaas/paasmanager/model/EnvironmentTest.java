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

import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentDto;

public class EnvironmentTest extends TestCase {

    private static final String VDC = "vdc";
    private static final String REGION = "region";

    @Override
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreateEnvironment() throws Exception {

        ProductRelease productRelease = new ProductRelease("product", "2.0");
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier();
        tier.setInitialNumberInstances(new Integer(1));
        tier.setMaximumNumberInstances(new Integer(5));
        tier.setMinimumNumberInstances(new Integer(1));
        tier.setName("tierName");
        tier.setProductReleases(productReleases);
        tier.setFlavour("3");
        tier.setFloatingip("true");
        tier.setImage("image");

        Tier tier2 = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "4", "image2",
                "icone2");

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier2);

        Environment envResult = new Environment();
        envResult.setName("environemntName");
        envResult.addTier(tier2);
        envResult.addTier(tier);
        // envResult.setTiers(tiers);

        EnvironmentDto envDto = envResult.toDto();
        Environment env2 = envDto.fromDto();
        assertEquals(env2, envResult);

        assertEquals(envResult.getTiers().size(), 2);

    }

    @Test
    public void testIsDifferentRegions() {
        Tier tier = new Tier("name1", new Integer(1), new Integer(5), new Integer(1), null);
        tier.setRegion("region1");

        Tier tier2 = new Tier("name2", new Integer(1), new Integer(5), new Integer(1), null);
        tier.setRegion("region2");

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier2);

        Environment env = new Environment();

        env.setName("environmentName");
        env.addTier(tier);
        env.addTier(tier2);

        boolean result = env.isDifferentRegions();
        assertEquals(result, true);
    }

    @Test
    public void testIsNotDifferentRegions() {
        Tier tier = new Tier("name1", new Integer(1), new Integer(5), new Integer(1), null);
        tier.setRegion("region1");

        Tier tier2 = new Tier("name2", new Integer(1), new Integer(5), new Integer(1), null);
        tier2.setRegion("region1");

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier2);

        Environment env = new Environment();

        env.setName("environmentName");
        env.addTier(tier);
        env.addTier(tier2);

        boolean result = env.isDifferentRegions();
        assertEquals(result, false);

    }

    @Test
    public void testIsNetworkFederated() {

        ProductRelease productRelease = new ProductRelease("product", "2.0");
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier("name1", new Integer(1), new Integer(5), new Integer(1), productReleases);
        tier.setRegion("region1");
        tier.addNetwork(new Network("uno", VDC, REGION));

        Tier tier2 = new Tier("name2", new Integer(1), new Integer(5), new Integer(1), productReleases);
        tier2.setRegion("region2");
        tier2.addNetwork(new Network("uno", VDC, REGION));

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier2);

        Environment envResult = new Environment("environemntName", tiers, "description", "ORG", VDC);
        envResult.setName("environemntName");
        envResult.setTiers(tiers);

        boolean result = envResult.isNetworkFederated();
        assertEquals(result, true);
        assertEquals(envResult.getDescription(), "description");
        assertEquals(envResult.getVdc(), VDC);
        assertEquals(envResult.getOrg(), "ORG");
    }

    @Test
    public void testNotIsNetworkFederated() {

        ProductRelease productRelease = new ProductRelease("product", "2.0");
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier("name1", new Integer(1), new Integer(5), new Integer(1), productReleases);
        tier.setRegion("region1");
        tier.addNetwork(new Network("uno2", VDC, REGION));

        Tier tier2 = new Tier("name2", new Integer(1), new Integer(5), new Integer(1), productReleases);
        tier.setRegion("region2");
        tier.addNetwork(new Network("uno", VDC, REGION));

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier2);

        Environment envResult = new Environment("environemntName", "description", tiers);


        boolean result = envResult.isNetworkFederated();
        assertEquals(result, false);
    }

    @Test
    public void testgetRegionNetworks() {

        ProductRelease productRelease = new ProductRelease("product", "2.0");
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier("name1", new Integer(1), new Integer(5), new Integer(1), productReleases);
        tier.setRegion("region1");
        tier.addNetwork(new Network("uno2", VDC, REGION));

        Tier tier3 = new Tier("name3", new Integer(1), new Integer(5), new Integer(1), productReleases);
        tier3.setRegion("region3");
        tier3.addNetwork(new Network("uno2", VDC, REGION));

        Tier tier2 = new Tier("name2", new Integer(1), new Integer(5), new Integer(1), productReleases);
        tier2.setRegion("region2");
        tier2.addNetwork(new Network("uno", VDC, REGION));

        Tier tier4 = new Tier("name5", new Integer(1), new Integer(5), new Integer(1), productReleases);
        tier4.setRegion("region2");
        tier4.addNetwork(new Network("uno2", VDC, REGION));

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier2);
        tiers.add(tier3);
        tiers.add(tier4);

        Environment envResult = new Environment("environemntName", tiers);
        Set<String> nets = envResult.getFederatedNetworks();
        assertEquals(nets.size(), 1);

    }

    @Test
    public void testEnvironmentII() {

        Tier tier = new Tier("name1", new Integer(1), new Integer(5), new Integer(1), null);
        tier.setRegion("region1");
        tier.addNetwork(new Network("uno2", VDC, REGION));

        Tier tier3 = new Tier("name3", new Integer(1), new Integer(5), new Integer(1), null);
        tier3.setRegion("region3");
        tier3.addNetwork(new Network("uno2", VDC, REGION));

        Tier tier2 = new Tier("name2", new Integer(1), new Integer(5), new Integer(1), null);
        tier2.setRegion("region2");
        tier2.addNetwork(new Network("uno", VDC, REGION));

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);


        Environment envResult = new Environment("environemntName", tiers);
        assertEquals(envResult.getTiers().size(), 1);

        envResult.addTier(tier2);
        assertEquals(envResult.getTiers().size(), 2);

        envResult.deleteTier(tier);
        assertEquals(envResult.getTiers().size(), 1);

        envResult.updateTier(tier2, tier3);

        for (Tier t : envResult.getTiers()) {
            assertEquals(t.getName(), "name3");
        }


    }

    public void testGetNetworkWithRegionAndName() {
        Tier tier = new Tier("name1", new Integer(1), new Integer(5), new Integer(1), null);
        tier.setRegion(REGION);
        tier.addNetwork(new Network("uno2", VDC, REGION));

        Tier tier3 = new Tier("name3", new Integer(1), new Integer(5), new Integer(1), null);
        tier3.setRegion("region3");
        tier3.addNetwork(new Network("uno3", VDC, REGION + 3));

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier3);

        Environment envResult = new Environment("environemntName", tiers);

        Network net = envResult.getNetworkWithRegionAndName(REGION, "uno2");
        Network net2 = envResult.getNetworkWithRegionAndName(REGION + 3, "uno3");
        assertEquals(net.getNetworkName(), "uno2");
        assertEquals(net2.getNetworkName(), "uno3");
        assertNull(envResult.getNetworkWithRegionAndName(REGION + 3, "noexists"));
    }


}
