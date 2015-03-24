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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.AttributeDao;
import com.telefonica.euro_iaas.paasmanager.dao.MetadataDao;
import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.dao.sdc.ProductReleaseSdcDao;
import com.telefonica.euro_iaas.paasmanager.manager.impl.ProductReleaseManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Metadata;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;

import junit.framework.TestCase;

public class ProductReleaseManagerImplTest extends TestCase {

    private ProductReleaseDao productReleaseDao;
    private ProductReleaseSdcDao productReleaseSdcDao;
    private MetadataDao metadataDao; 
    private AttributeDao attributeDao;
    
    private ProductReleaseManagerImpl manager = null;
    
    private ProductRelease productReleasePM = null;
    private ProductRelease productReleaseSDC = null;

    private PaasManagerUser user;
    private ClaudiaData claudiaData;
    
    Metadata metadata_diff = new Metadata("key_diff", "value_diff");
	    
	@Override
    @Before
    public void setUp() throws Exception {
		/*claudiaData = new ClaudiaData("org", "vdc", "service");
        user = new PaasManagerUser("user", "password", new ArrayList<GrantedAuthority>());
        claudiaData.setUser(user);*/
        
		productReleaseDao = mock(ProductReleaseDao.class);
		productReleaseSdcDao = mock(ProductReleaseSdcDao.class);
		metadataDao = mock (MetadataDao.class);
		attributeDao = mock (AttributeDao.class);
		claudiaData = mock (ClaudiaData.class);
		
        manager = new ProductReleaseManagerImpl();

        manager.setAttributeDao(attributeDao);   
        manager.setMetadataDao(metadataDao);      
        manager.setProductReleaseDao(productReleaseDao);
        manager.setProductReleaseSdcDao(productReleaseSdcDao); 
        
        productReleasePM = new ProductRelease("product", "version");
        productReleaseSDC = new ProductRelease("product", "version");
        
        Metadata metadata1 = new Metadata("key1", "value1");
        productReleasePM.addMetadata(metadata1);
        productReleaseSDC.addMetadata(metadata1);

        Metadata metadata2 = new Metadata("key2", "value2");
        productReleasePM.addMetadata(metadata2);
        productReleaseSDC.addMetadata(metadata2);
        
        productReleaseSDC.addMetadata(metadata_diff);
        
        when(productReleaseSdcDao.load(eq("product"), eq("version"), any(ClaudiaData.class))).thenReturn(productReleaseSDC);
        when(productReleaseDao.load(any(String.class))).thenReturn(productReleasePM);
       //when(productReleaseDao.update(any(ProductRelease.class))).thenReturn(productReleasePM);
        when(metadataDao.create(any(Metadata.class))).thenReturn(metadata_diff);
    }
    
    @Test
    public void testloadFromSDCAndCreateUpdateMetadata() throws EntityNotFoundException {
    	
    	ProductRelease productRelease = manager.loadFromSDCAndCreate("product-version", claudiaData);
    	
    	assertEquals(productRelease.getName(), "product-version");
    	assertEquals(productRelease.getMetadatas().contains(metadata_diff), true);     
    }
}
