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

package com.telefonica.euro_iaas.paasmanager.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.NetworkDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;
import com.telefonica.euro_iaas.paasmanager.rest.resources.EnvironmentResource;
import com.telefonica.euro_iaas.paasmanager.rest.resources.TierResource;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from "classpath:/app-config.xml"
@ContextConfiguration(locations = { "classpath:/applicationContextTest.xml" })
@ActiveProfiles("dummy")
public class TierTest {

    @Autowired
    private TierResource tierResource;

    @Autowired
    private EnvironmentResource environmentResource;

    @Autowired
    private ProductReleaseDao productReleaseDao;

    @Autowired
    private EnvironmentManager environmentManager;

    @Autowired
    private TierManager tierManager;

    String org = "FIWARE";
    String vdc = "6571e3422ad84f7d828ce2f30373b3d4";

    @Before
    public void setUp() throws Exception {
        List<Attribute> atttomcat = new ArrayList<Attribute>();

        atttomcat.add(new Attribute("ssl_port", "8443", "The ssl listen port"));
        atttomcat.add(new Attribute("port", "8080", "The ssl listen port"));
        atttomcat.add(new Attribute("openports", "8080", "The ssl listen port"));
        atttomcat.add(new Attribute("sdcgroupid", "id_web_server", "The ssl listen port"));

    }

    @Test
    public void testCreateTierOK() throws Exception {

        // given
        Environment environment2 = new Environment();
        environment2.setName("env2");
        environment2.setDescription("description");

        environmentResource.insert(org, vdc, environment2.toDto());
        ProductRelease tomcat7Att = new ProductRelease("tomcat8", "78", "Tomcat server 8", null);

        tomcat7Att = productReleaseDao.create(tomcat7Att);

        Tier tierbk = new Tier("dd", new Integer(1), new Integer(1), new Integer(1), null);
        tierbk.setImage("image");
        tierbk.setIcono("icono");
        tierbk.setFlavour("flavour");
        tierbk.setFloatingip("floatingip");
        tierbk.setPayload("");
        tierbk.setKeypair("keypair");
        tierbk.addProductRelease(tomcat7Att);
        tierbk.setRegion("regionOne");

        // when
        tierResource.insert(org, vdc, environment2.getName(), tierbk.toDto());
        TierDto tierDto = tierResource.load(vdc, environment2.getName(), tierbk.getName());

        // then
        assertEquals(tierDto.getName(), tierbk.getName());
        assertEquals(1, tierDto.getProductReleaseDtos().size());
        assertEquals(tierDto.getProductReleaseDtos().get(0).getProductName(), "tomcat8");
        assertEquals("regionOne", tierDto.getRegion());
    }

    @Test(expected = APIException.class)
    public void testCreateTierAlreadyExist() throws Exception {
        Environment environmentBk = new Environment();
        environmentBk.setName("createtieralready");
        environmentBk.setDescription("description");

        environmentResource.insert(org, vdc, environmentBk.toDto());

        Tier tierbk = new Tier("tiercreatedalready", new Integer(1), new Integer(1), new Integer(1), null);
        tierbk.setImage("image");
        tierbk.setIcono("icono");
        tierbk.setFlavour("flavour");
        tierbk.setFloatingip("floatingip");
        tierbk.setPayload("");
        tierbk.setKeypair("keypair");

        tierResource.insert(org, vdc, "createtieralready", tierbk.toDto());
        tierResource.insert(org, vdc, "createtieralready", tierbk.toDto());
    }

    @Test(expected = Exception.class)
    public void testCreateTierNotFound() throws Exception {
        Environment environmentBk = new Environment();
        environmentBk.setName("create_tier");
        environmentBk.setDescription("description");

        environmentResource.insert(org, vdc, environmentBk.toDto());

        TierDto tier = tierResource.load(vdc, "testCreateTierOK", "tiercreated");

    }

    @Test
    public void testdUpdateTier() throws Exception {

        Environment environmentBk = new Environment();
        environmentBk.setName("updatedenvironmenttierv2");
        environmentBk.setDescription("Description Second environment");
        Tier tierbk = new Tier("tierupdatetier22", new Integer(1), new Integer(1), new Integer(1), null);
        tierbk.setImage("image");
        tierbk.setIcono("icono");
        tierbk.setFlavour("flavour");
        tierbk.setFloatingip("floatingip");
        tierbk.setPayload("");
        tierbk.setKeypair("keypair");
        environmentBk.addTier(tierbk);

        environmentResource.insert(org, vdc, environmentBk.toDto());

        Environment env = environmentManager.load("updatedenvironmenttierv2", vdc);

        TierDto tierDto = tierResource.load(vdc, "updatedenvironmenttierv2", "tierupdatetier22");

        tierDto.setFlavour("flavour3");
        tierDto.setIcono("icono2");
        tierResource.update(org, vdc, "updatedenvironmenttierv2", tierDto.getName(), tierDto);
        TierDto tier2Dto = tierResource.load(vdc, "updatedenvironmenttierv2", "tierupdatetier22");
        assertEquals(tier2Dto.getFlavour(), "flavour3");
        assertEquals(tier2Dto.getIcono(), "icono2");

        Environment env3 = environmentManager.load("updatedenvironmenttierv2", vdc);
        assertEquals(env3.getName(), "updatedenvironmenttierv2");
        assertEquals(env3.getTiers().size(), 1);

    }

    @Test
    public void testdUpdateTierSoftware() throws Exception {

        ProductRelease product2 = new ProductRelease("test", "0.1", "test 0.1", null);

        product2 = productReleaseDao.create(product2);
        assertNotNull(product2);
        assertNotNull(product2.getId());
        assertEquals(product2.getProduct(), "test");
        assertEquals(product2.getVersion(), "0.1");

        ProductRelease product3 = new ProductRelease("test2", "0.1", "test2 0.1", null);

        product3 = productReleaseDao.create(product3);
        assertNotNull(product3);
        assertNotNull(product3.getId());
        assertEquals(product3.getProduct(), "test2");
        assertEquals(product3.getVersion(), "0.1");

        Environment environmentBk = new Environment();
        environmentBk.setName("updatedenvironmentsoftwware");
        environmentBk.setDescription("Description Second environment");
        Tier tierbk = new Tier("tiersoftware", new Integer(1), new Integer(1), new Integer(1), null);
        tierbk.addProductRelease(product2);
        tierbk.setImage("image");
        tierbk.setIcono("icono");
        tierbk.setFlavour("flavour");
        tierbk.setFloatingip("floatingip");
        tierbk.setPayload("");
        tierbk.setKeypair("keypair");
        environmentBk.addTier(tierbk);

        environmentResource.insert(org, vdc, environmentBk.toDto());

        Environment env = environmentManager.load("updatedenvironmentsoftwware", vdc);
        assertEquals(env.getName(), "updatedenvironmentsoftwware");
        assertEquals(env.getTiers().size(), 1);

        TierDto tierDto = tierResource.load(vdc, "updatedenvironmentsoftwware", "tiersoftware");
        assertEquals(tierDto.getFlavour(), "flavour");
        assertEquals(tierDto.getName(), "tiersoftware");
        assertEquals(tierDto.getProductReleaseDtos().size(), 1);
        tierDto.setFlavour("flavour3");
        tierDto.setIcono("icono2");
        tierDto.addProductRelease(product3.toDto());
        tierResource.update(org, vdc, "updatedenvironmentsoftwware", tierDto.getName(), tierDto);
        TierDto tier2Dto = tierResource.load(vdc, "updatedenvironmentsoftwware", "tiersoftware");
        assertEquals(tier2Dto.getProductReleaseDtos().size(), 2);
        assertEquals(tier2Dto.getProductReleaseDtos().get(0).getProductName(), "test");
        assertEquals(tier2Dto.getProductReleaseDtos().get(1).getProductName(), "test2");

        Environment env3 = environmentManager.load("updatedenvironmentsoftwware", vdc);
        assertEquals(env3.getName(), "updatedenvironmentsoftwware");
        assertEquals(env3.getTiers().size(), 1);

    }
    
    @Test
    public void testdUpdateNetworks() throws Exception {

        Environment environmentBk = new Environment();
        environmentBk.setName("testUN");
        environmentBk.setDescription("Description Second environment");
        Tier tierbk = new Tier("te", new Integer(1), new Integer(1), new Integer(1), null);
        tierbk.setImage("image");
        tierbk.setIcono("icono");
        tierbk.setFlavour("flavour");
        tierbk.setFloatingip("floatingip");
        tierbk.setPayload("");
        tierbk.setKeypair("keypair");
        tierbk.addNetwork(new Network("dddd", "dd"));
        environmentBk.addTier(tierbk);

        environmentResource.insert(org, vdc, environmentBk.toDto());
        
        Tier tierbk2 = new Tier("te", new Integer(1), new Integer(1), new Integer(1), null);
        tierbk2.setImage("image");
        tierbk2.setIcono("icono");
        tierbk2.setFlavour("flavour");
        tierbk2.setFloatingip("floatingip");
        tierbk2.setPayload("");
        tierbk2.setKeypair("keypair");
        tierbk2.addNetwork(new Network("aaaa", "dd"));

        tierResource.update(org, vdc, environmentBk.getName(), tierbk.getName(), tierbk2.toDto());
        TierDto tierOut = tierResource.load(vdc, environmentBk.getName(), tierbk.getName());
        
        assertEquals (tierbk.getName(), tierOut.getName());
        assertEquals (tierbk.getNetworks().size(), 1);
        for (NetworkDto net: tierOut.getNetworksDto()) {
        	 assertEquals (net.getNetworkName(), "aaaa");

        }
        

    }
    
    @Test(expected = APIException.class)
    public void testdUpdateTierNoExist() throws Exception {

        ProductRelease product2 = new ProductRelease("test22", "0.1", "test 0.1", null);
        product2 = productReleaseDao.create(product2);

        Environment environmentBk = new Environment();
        environmentBk.setName("updattier22");
        environmentBk.setDescription("Description Second environment");
        Tier tierbk = new Tier("tiersoftware", new Integer(1), new Integer(1), new Integer(1), null);

        environmentResource.insert(org, vdc, environmentBk.toDto());
        tierResource.update(org, vdc, environmentBk.getName(), "henar", tierbk.toDto());
    }
    
    
    
    @Test(expected = APIException.class)
    public void testdUpdateTier2() throws Exception {

        ProductRelease product2 = new ProductRelease("test22", "0.1", "test 0.1", null);
        product2 = productReleaseDao.create(product2);

        Environment environmentBk = new Environment();
        environmentBk.setName("updattier22");
        environmentBk.setDescription("Description Second environment");
        Tier tierbk = new Tier("tsetUPdatetier2", new Integer(1), new Integer(1), new Integer(1), null);
        tierbk.addProductRelease(product2);
        tierbk.setImage("image");
        tierbk.setIcono("icono");
        tierbk.setFlavour("flavour");
        tierbk.setFloatingip("floatingip");
        tierbk.setPayload("");
        tierbk.setKeypair("keypair");
        environmentBk.addTier(tierbk);

        environmentResource.insert(org, vdc, environmentBk.toDto());
        tierResource.update(org, vdc, environmentBk.getName(), "henar", tierbk.toDto());
    }
    

    @Test
    public void testeDeleteTier() throws Exception {

        Environment environment = new Environment();
        environment.setName("testeDeleteTier2");
        environment.setDescription("Description");

        ProductRelease tomcat7Att = new ProductRelease("tomcat9", "7", "Tomcat server 7", null);

        tomcat7Att = productReleaseDao.create(tomcat7Att);

        Tier tier = new Tier("2tierotro", new Integer(1), new Integer(1), new Integer(1), null);
        tier.setImage("image");
        tier.setIcono("icono");
        tier.setFlavour("flavour");
        tier.setFloatingip("floatingip");
        tier.setPayload("");
        tier.setKeypair("keypair");
        tier.addProductRelease(tomcat7Att);
        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        environment.setTiers(tiers);

        environmentResource.insert(org, vdc, environment.toDto());

        Environment env = environmentManager.load("testeDeleteTier2", vdc);

        assertEquals(env.getName(), "testeDeleteTier2");
        assertEquals(env.getTiers().size(), 1);

        tierResource.delete(org, vdc, "testeDeleteTier2", "2tierotro");

        Environment env2 = environmentManager.load("testeDeleteTier2", vdc);
        assertEquals(env2.getName(), "testeDeleteTier2");
        assertEquals(env2.getTiers().size(), 0);

    }

    @Test
    public void testDeleteTierwithNetworkInAnotherTier() throws Exception {
        Environment environmentBk = new Environment();
        environmentBk.setName("env");
        environmentBk.setDescription("description");

        environmentResource.insert(org, vdc, environmentBk.toDto());

        Tier tierbk = new Tier("tier1", new Integer(1), new Integer(1), new Integer(1), null);
        tierbk.setImage("image");
        tierbk.setIcono("icono");
        tierbk.setFlavour("flavour");
        tierbk.setFloatingip("floatingip");
        tierbk.setPayload("");
        tierbk.setKeypair("keypair");

        Network net = new Network("network3", vdc);
        tierbk.addNetwork(net);

        tierResource.insert(org, vdc, environmentBk.getName(), tierbk.toDto());

        Tier tierbk2 = new Tier("tier2", new Integer(1), new Integer(1), new Integer(1), null);
        tierbk2.setImage("image");
        tierbk2.setIcono("icono");
        tierbk2.setFlavour("flavour");
        tierbk2.setFloatingip("floatingip");
        tierbk2.setPayload("");
        tierbk2.setKeypair("keypair");
        tierbk2.addNetwork(net);
        tierResource.insert(org, vdc, environmentBk.getName(), tierbk2.toDto());

        try {
            tierResource.delete(org, vdc, environmentBk.getName(), tierbk2.getName());
        } catch (Exception e) {
            fail();
        }

        try {
            tierResource.delete(org, vdc, environmentBk.getName(), tierbk.getName());
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testDeleteANonExistingtier() throws Exception {
        try {
            tierResource.delete(org, vdc, "12env", "noexistingier");
            fail();
        } catch (Exception e) {

        }

    }
}
