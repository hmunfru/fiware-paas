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
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.claudia.FirewallingClient;
import com.telefonica.euro_iaas.paasmanager.dao.SecurityGroupDao;
import com.telefonica.euro_iaas.paasmanager.dao.TierInstanceDao;
import com.telefonica.euro_iaas.paasmanager.manager.impl.SecurityGroupManagerImpl;
import com.telefonica.euro_iaas.paasmanager.manager.impl.TierInstanceManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.Metadata;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * @author jesus.movilla
 */
public class TierInstanceManagerImplTest extends TestCase {

    private TierInstanceDao tierInstanceDao;
    private TierManager tierManager;
    private ProductInstanceManager productInstanceManager;
    private InfrastructureManager infrastructureManager;
    private ProductReleaseManager productReleaseManager;
    private EnvironmentManager enviromentManager;
    private EnvironmentInstanceManager environmentInstanceManager;
    private SecurityGroupManager securityGroupManager;
    private SystemPropertiesProvider systemPropertiesProvider;
    
    private Tier tierProductShard = null;
    private Tier tierProductConfig = null;
    private Tier tierProductMongos = null;

    private TierInstance tierInstanceConfig = null;
    private TierInstance tierInstanceShard = null;
    private TierInstance tierInstanceMongos = null;
    private EnvironmentInstance environmentInstance = null;
    private TierInstanceManagerImpl manager = null;
    private Environment envResult = null;

    private PaasManagerUser user;
    private ClaudiaData claudiaData;

    @Override
    @Before
    public void setUp() throws Exception {

        claudiaData = new ClaudiaData("org", "vdc", "service");
        user = new PaasManagerUser("user", "password", new ArrayList<GrantedAuthority>());
        claudiaData.setUser(user);

        tierInstanceDao = mock(TierInstanceDao.class);
        tierManager = mock(TierManager.class);
        productInstanceManager = mock(ProductInstanceManager.class);
        infrastructureManager = mock(InfrastructureManager.class);
        productReleaseManager = mock(ProductReleaseManager.class);        
        enviromentManager = mock(EnvironmentManager.class);
        environmentInstanceManager = mock(EnvironmentInstanceManager.class);
        securityGroupManager = mock(SecurityGroupManager.class);
        systemPropertiesProvider = mock (SystemPropertiesProvider.class);
        
        manager = new TierInstanceManagerImpl();

        manager.setTierInstanceDao(tierInstanceDao);        
        manager.setTierManager(tierManager);        
        manager.setProductInstanceManager(productInstanceManager);
        manager.setInfrastructureManager(infrastructureManager);
        manager.setProductReleaseManager(productReleaseManager);        
        manager.setEnvironmentManager(enviromentManager);
        manager.setEnvironmentInstanceManager(environmentInstanceManager);
        manager.setSecurityGroupManager(securityGroupManager);
        manager.setSystemPropertiesProvider(systemPropertiesProvider);
        

        VM host = new VM(null, "hostname", "domain");

        ProductRelease productReleaseConfig = new ProductRelease("config", "2.0");
        List<ProductRelease> productReleasesConfig = new ArrayList<ProductRelease>();
        productReleasesConfig.add(productReleaseConfig);
        tierProductConfig = new Tier();
        tierProductConfig.setInitialNumberInstances(new Integer(1));
        tierProductConfig.setMaximumNumberInstances(new Integer(5));
        tierProductConfig.setMinimumNumberInstances(new Integer(1));
        tierProductConfig.setName("tierconfig");
        tierProductConfig.setProductReleases(productReleasesConfig);
        
        SecurityGroup secGroup = new SecurityGroup("name", "description");
                
        Attribute att = new Attribute("balancer", "mongos", "description");
        List<Attribute> lAtt = new ArrayList();
        lAtt.add(att);
        ProductRelease productReleaseShard = new ProductRelease("shard", "2.0");
        productReleaseShard.addAttribute(att);
        // ProductRelease productBalancer = new ProductRelease("productbalancer", "2.0");
        List<ProductRelease> productReleasesShards = new ArrayList<ProductRelease>();
        productReleasesShards.add(productReleaseShard);

        tierProductShard = new Tier();
        tierProductShard.setInitialNumberInstances(new Integer(1));
        tierProductShard.setMaximumNumberInstances(new Integer(5));
        tierProductShard.setMinimumNumberInstances(new Integer(1));
        tierProductShard.setName("tiershard");
        tierProductShard.setProductReleases(productReleasesShards);
        //tierProductShard.setSecurityGroup(secGroup);

        List<ProductRelease> productReleaseBalancer = new ArrayList<ProductRelease>();
        ProductRelease productReleaseMongos = new ProductRelease("mongos", "2.0");
        productReleaseBalancer.add(productReleaseMongos);

        tierProductMongos = new Tier();
        tierProductMongos.setInitialNumberInstances(new Integer(1));
        tierProductMongos.setMaximumNumberInstances(new Integer(5));
        tierProductMongos.setMinimumNumberInstances(new Integer(1));
        tierProductMongos.setName("tierNameMongos");
        tierProductMongos.setProductReleases(productReleaseBalancer);

        tierInstanceConfig = new TierInstance(tierProductConfig, "tierInsatnceConfig", "nametierInstance-tier-1", host);
        ProductInstance productInstance = new ProductInstance(productReleaseConfig, Status.INSTALLING, "vdc");
        tierInstanceShard = new TierInstance(tierProductShard, "tierInsatnceShard", "nametierInstance-tier-1", host);
        tierInstanceMongos = new TierInstance(tierProductMongos, "tierInsatnceMongos", "nametierInstance-tier-1", host);
        tierInstanceMongos.setId(new Long(1));
        tierInstanceMongos.addProductInstance(productInstance);
        when(tierManager.loadTierWithProductReleaseAndMetadata(anyString(), anyString(), anyString())).thenReturn(
                tierProductConfig);
        when(tierInstanceDao.create(any(TierInstance.class))).thenReturn(tierInstanceMongos);
        when(tierInstanceDao.update(any(TierInstance.class))).thenReturn(tierInstanceMongos);

        when(tierInstanceDao.load(any(String.class))).thenThrow(
                new EntityNotFoundException(TierInstance.class, "dD", null));

        when(productInstanceManager.create(any(ClaudiaData.class), any(ProductInstance.class))).thenReturn(
                productInstance);
        when(securityGroupManager.load(anyString())).thenReturn(secGroup);
        when(securityGroupManager.create(anyString(), anyString(), anyString(), any(SecurityGroup.class))).thenReturn(
        		secGroup);

        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");
        
        when(productReleaseManager.loadWithMetadata(any(String.class))).thenReturn(productReleaseShard);
        
        
        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tierProductConfig);
        tiers.add(tierProductShard);
        tiers.add(tierProductMongos);

        envResult = new Environment();
        envResult = new Environment();
        envResult.setName("environemntName");

        envResult.setTiers(tiers);

        environmentInstance = new EnvironmentInstance();
        environmentInstance.setEnvironment(envResult);
        environmentInstance.setBlueprintName("blueprintName");
        environmentInstance.setDescription("description");

        List<ProductInstance> lProductInstance = new ArrayList();
        lProductInstance.add(productInstance);

        List<TierInstance> lTierInstance = new ArrayList();
        lTierInstance.add(tierInstanceConfig);
        lTierInstance.add(tierInstanceShard);
        lTierInstance.add(tierInstanceMongos);

        environmentInstance.setTierInstances(lTierInstance);
        when(enviromentManager.load(any(String.class), any(String.class))).thenReturn(envResult);

    }

    @Test
    public void testAddProductInstances() throws Exception {

        VM host = new VM(null, "hostname", "domain");
        TierInstance tierInstance = new TierInstance(tierProductConfig, "tierInsatnce", "nametierInstance-tier-1", host);
        //tierProductShard.setRegion("Region");
        //tierInstance.setTier(tierProductShard);
        
        TierInstance tierInstanceCreated = manager.create(claudiaData, "env", tierInstance);

        assertEquals(tierInstanceCreated.getName(), tierInstanceCreated.getName());

        ProductRelease productRelease = new ProductRelease("product", "2.0");

        ProductInstance productInstance = new ProductInstance(productRelease, Status.INSTALLING, "vdc");
        tierInstanceCreated.addProductInstance(productInstance);
        TierInstance tierInstanceUpdate = manager.update(claudiaData, "env", tierInstance);

        verify(tierInstanceDao, times(2)).load(any(String.class));
        verify(tierInstanceDao, times(0)).update(any(TierInstance.class));
        verify(tierInstanceDao, times(2)).create(any(TierInstance.class));
        // assertEquals(tierInstanceUpdate.getProductInstances().size(), 3);
    }

    @Test
    public void testCreateTierInstance() throws Exception {

        VM host = new VM(null, "hostname", "domain");
        TierInstance tierInstance = new TierInstance(tierProductConfig, "tierInsatnce", "nametierInstance-tier-1", host);

        TierInstance tierInstanceCreated = manager.create(claudiaData, "env", tierInstance);
        assertEquals(tierInstanceCreated.getName(), tierInstanceCreated.getName());
        // assertNotNull(environmentCreated.getId());
    }

    @Test
    public void testGetProductNameBalanced() throws Exception {

        String productName = manager.getProductNameBalanced(tierProductShard);
        assertEquals(productName, "mongos");
    }

    @Test
    public void testGetProductNameBalancedError() throws Exception {

        String productName = manager.getProductNameBalanced(tierProductMongos);
        assertNull(productName);
    }

    @Test
    public void testGetTierInstanceToConfigure() throws Exception {

        TierInstance tierfound = manager.getTierInstanceToConfigure(environmentInstance, tierProductShard);
        assertNotNull(tierfound);
        assertEquals(tierfound.getTier().getName(), tierInstanceMongos.getTier().getName());
        assertEquals(tierfound, tierInstanceMongos);
    }

    @Test
    public void testGetTierInstanceWithTier() throws Exception {
        TierInstance tierInstance = manager.getTierInstanceWithTier(environmentInstance, tierProductMongos);
        assertEquals(tierInstance.getTier(), tierProductMongos);
        assertEquals(tierInstance, tierInstanceMongos);
    }

    @Test
    public void testGetTierProductWithName() throws Exception {
        Tier tier = manager.getTierProductWithName(envResult, "mongos");
        assertEquals(tier, tierProductMongos);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReconfigure() throws Exception {

        when(tierManager.load(any(String.class), any(String.class), any(String.class))).thenReturn(tierProductConfig);
        Mockito.doNothing().when(productInstanceManager)
                .configure(any(ClaudiaData.class), any(ProductInstance.class), anyList());

        manager.reconfigure(claudiaData, environmentInstance, tierInstanceShard);

    }

    @Test
    public void testRemoveTierInstance() throws Exception {

        VM host = new VM(null, "hostname", "domain");
        TierInstance tierInstance = new TierInstance(tierProductConfig, "tierInsatnce", "nametierInstance-tier-1", host);
        TierInstance tierInstanceCreated = manager.create(claudiaData, "env", tierInstance);
        // when(tierInstanceDao.load(any(String.class))).thenReturn(tierInstance);
        manager.remove(tierInstanceCreated);
    }
}
