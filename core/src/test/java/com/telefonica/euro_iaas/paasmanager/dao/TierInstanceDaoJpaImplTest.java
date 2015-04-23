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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierInstanceSearchCriteria;

/**
 * Unit test for TierDaoJpaImplTest
 * 
 * @author Jesus M. Movilla
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-test-db-config.xml", "classpath:/spring-dao-config.xml" })
public class TierInstanceDaoJpaImplTest {


    @Autowired
    private TierInstanceDao tierInstanceDao;
    @Autowired
    private TierDao tierDao;
    @Autowired
    private NetworkInstanceDao networkInstanceDao;
    @Autowired
    private ProductInstanceDao productInstanceDao;

    public final static String TIER_NAME = "TierName";
    public final static String TIER_INSTANCE_NAME = "TierInstanceName";
    public final static String PRODUCT_NAME = "Product";
    public final static String NETWORK_NAME = "NETWORK";
    public final static String VDC = "vdc";
    public final static String ENV = "env";
    public final static String PRODUCT_VERSION = "version";
    public final static Integer MAXIMUM_INSTANCES = 8;
    public final static Integer MINIMUM_INSTANCES = 1;
    public final static Integer INITIAL_INSTANCES = 1;
    public static String REGION = "region";

    /**
     * Test the create method
     */
    @Test
    public void testCreateAndLoad() throws Exception {
        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(1);

        NetworkInstance networkInstance = new NetworkInstance ("net", "vdc",REGION);
        networkInstance.setIdNetwork("ID");
        networkInstance = networkInstanceDao.create(networkInstance);

        Tier tier = new Tier(TIER_NAME, maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes", null);
        tier = tierDao.create(tier);
        TierInstance tierInst = new TierInstance();
        tierInst.setName(TIER_INSTANCE_NAME);
        tierInst.setNumberReplica(2);
        tierInst.addNetworkInstance(networkInstance);
        tierInst.setTier(tier);
        tierInst = tierInstanceDao.create(tierInst);
        assertNotNull(tierInst);
        assertNotNull(tierInst.getId());
        
        tierInst = tierInstanceDao.load(TIER_INSTANCE_NAME);
        assertEquals(tierInst.getName(), TIER_INSTANCE_NAME);
        assertEquals(tierInst.getNumberReplica(), 2); 

    }
    
    @Test
    public void testFindByTierInstanceId() throws Exception {
        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(1);

        ProductInstance productInstance = new ProductInstance ();
        productInstance.setName("name");
        productInstance = productInstanceDao.create(productInstance);
        

        Tier tier = new Tier(TIER_NAME, maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes", null);
        tier = tierDao.create(tier);
        TierInstance tierInst = new TierInstance();
        tierInst.setName(TIER_INSTANCE_NAME);
        tierInst.setNumberReplica(2);
        tierInst.addProductInstance(productInstance);
        tierInst.setTier(tier);
        tierInst = tierInstanceDao.create(tierInst);
        assertNotNull(tierInst);
        assertNotNull(tierInst.getId());
        
        tierInst = tierInstanceDao.findByTierInstanceId(tierInst.getId());
        assertEquals(tierInst.getName(), TIER_INSTANCE_NAME);
        assertEquals(tierInst.getNumberReplica(), 2); 

    }
    
    @Test
    public void testFindByTierInstanceName() throws Exception {
        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(1);

        ProductInstance productInstance = new ProductInstance ();
        productInstance.setName("name");
        productInstance = productInstanceDao.create(productInstance);

        Tier tier = new Tier(TIER_NAME, maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes", null);
        tier = tierDao.create(tier);
        TierInstance tierInst = new TierInstance();
        tierInst.setName(TIER_INSTANCE_NAME+2);
        tierInst.setNumberReplica(2);
        tierInst.addProductInstance(productInstance);
        tierInst.setTier(tier);
        
        tierInst = tierInstanceDao.create(tierInst);
        assertNotNull(tierInst);
        assertNotNull(tierInst.getId());
        
        tierInst = tierInstanceDao.findByTierInstanceName(TIER_INSTANCE_NAME+2);
        assertEquals(tierInst.getName(), TIER_INSTANCE_NAME+2);
        assertEquals(tierInst.getNumberReplica(), 2); 

    }
    
    @Test
    public void testFindByCriteria() throws Exception {
        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(1);

        ProductInstance productInstance = new ProductInstance ();
        productInstance.setName("name2");
        productInstance = productInstanceDao.create(productInstance);

        Tier tier = new Tier(TIER_NAME+"crite", maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes", null);
        tier = tierDao.create(tier);
        
        TierInstance tierInst = new TierInstance();
        tierInst.setName(TIER_INSTANCE_NAME+2);
        tierInst.setNumberReplica(2);
        tierInst.addProductInstance(productInstance);
        tierInst.setTier(tier);
        tierInst.setVdc(VDC);
        tierInst = tierInstanceDao.create(tierInst);
        assertNotNull(tierInst);
        assertNotNull(tierInst.getId());
        
        TierInstanceSearchCriteria criteria = new TierInstanceSearchCriteria ();
        criteria.setVdc(VDC);
        criteria.setProductInstance(productInstance);
        List<TierInstance> lTierInstance = tierInstanceDao.findByCriteria(criteria);
        assertNotNull(lTierInstance);
        assertEquals(lTierInstance.size(), 1);


    }

    

    @Test
    public void testUpdate() throws Exception {
        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(1);

        NetworkInstance networkInstance = new NetworkInstance ("net","vdc",REGION);
        networkInstance.setIdNetwork("ID");
        networkInstance = networkInstanceDao.create(networkInstance);

        Tier tier = new Tier(TIER_NAME, maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes", null);
        tier = tierDao.create(tier);
        TierInstance tierInst = new TierInstance ();
        tierInst.setName(TIER_INSTANCE_NAME);
        tierInst.addNetworkInstance(networkInstance);
        tierInst.setTier(tier);
        tierInst = tierInstanceDao.create (tierInst);
        assertNotNull (tierInst);
        tierInst.setNumberReplica(55);
        tierInst = tierInstanceDao.update(tierInst);
        assertNotNull (tierInst);
        assertEquals (tierInst.getNumberReplica(), 55);

    }
    
    /**
     * Test delete method
     * @throws AlreadyExistsEntityException 
     * @throws InvalidEntityException 
     */
    @Test
    public void testDelete() throws InvalidEntityException, AlreadyExistsEntityException  {
        Integer minimum = new Integer(1);
        Integer initial = new Integer(2);
        Integer maximum = new Integer(1);

        NetworkInstance networkInstance = new NetworkInstance ("net","vdc",REGION);
        networkInstance.setIdNetwork("ID");
        networkInstance = networkInstanceDao.create(networkInstance);

        Tier tier = new Tier(TIER_NAME, maximum, minimum, initial, null, "2", "image", "icono", "keypair", "yes", null);
        tier = tierDao.create(tier);
        TierInstance tierInst = new TierInstance();
        tierInst.setName(TIER_INSTANCE_NAME);
        tierInst.addNetworkInstance(networkInstance);
        tierInst.setTier(tier);
        tierInst = tierInstanceDao.create(tierInst);
        
        tierInstanceDao.remove(tierInst);
        
     /*   try {
            tierInstanceDao.load(TIER_INSTANCE_NAME);
            fail("Should have thrown an EntityNotFoundException because the tier instance does not exit!");
        } catch (Exception e) {
            assertNotNull(e);
        }*/

    }
   

}
