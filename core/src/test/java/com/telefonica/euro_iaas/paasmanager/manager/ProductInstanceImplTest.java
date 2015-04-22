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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.telefonica.euro_iaas.paasmanager.dao.ProductInstanceDao;
import com.telefonica.euro_iaas.paasmanager.installator.ProductInstallator;
import com.telefonica.euro_iaas.paasmanager.manager.impl.ProductInstanceManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.bean.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;

/**
 * @author jesus.movilla
 */
public class ProductInstanceImplTest extends TestCase {

    private ProductInstanceDao productInstanceDao;

    private ProductReleaseManager productReleaseManager;
    private ProductInstallator productInstallator;

    private ProductInstanceManagerImpl manager = null;

    private ProductRelease productRelease = null;
    private TierInstance tierInstance = null;
    private ClaudiaData data;

    @Override
    @Before
    public void setUp() throws Exception {

        productInstanceDao = mock(ProductInstanceDao.class);

        productReleaseManager = mock(ProductReleaseManager.class);
        productInstallator = mock(ProductInstallator.class);

        manager = new ProductInstanceManagerImpl();

        manager.setProductInstallator(productInstallator);
        manager.setProductInstanceDao(productInstanceDao);
        manager.setProductReleaseManager(productReleaseManager);

        productRelease = new ProductRelease("product", "2.0");

        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        VM host = new VM(null, "hostname", "domain");

        Tier tier = new Tier();
        tier.setInitialNumberInstances(new Integer(1));
        tier.setMaximumNumberInstances(new Integer(5));
        tier.setMinimumNumberInstances(new Integer(1));
        tier.setName("tierName");
        tier.setProductReleases(productReleases);
        
        data = mock(ClaudiaData.class);
        PaasManagerUser user = mock(PaasManagerUser.class);
        
    

        when (data.getUser()).thenReturn(user);
        when (data.getOrg()).thenReturn("FIWARE");
        when (data.getService()).thenReturn("deploytm");
        when (data.getVdc()).thenReturn("60b4125450fc4a109f50357894ba2e28");
        when (user.getToken()).thenReturn("any");

        tierInstance = new TierInstance(tier, "tierInsatnce", "nametierInstance", host);
        ProductInstance productInstance = new ProductInstance(productRelease, Status.INSTALLING, "vdc");

        when(productReleaseManager.load(any(String.class),any(ClaudiaData.class))).thenReturn(productRelease);
        when(productInstanceDao.load(any(String.class))).thenReturn(productInstance);
        when(
                productInstallator.install(any(ClaudiaData.class), any(EnvironmentInstance.class), any(TierInstance.class),
                        any(ProductRelease.class))).thenReturn(productInstance);
    }

    @Test
    public void testCreateProductInstance() throws Exception {

        ProductInstance productInstance = new ProductInstance(productRelease, Status.INSTALLING, "vdc");
        ProductInstance productInstanceCreated = manager.create(data, productInstance);
        assertEquals(productInstance.getName(), productInstanceCreated.getName());

    }

    @Test
    public void testInstallProductInstanceNoAttributes() throws Exception {
        EnvironmentInstance enviromnentInstance = mock(EnvironmentInstance.class);
        
        ProductInstance productInstance = new ProductInstance(productRelease, Status.INSTALLING, "vdc");
   
        ProductInstance productInstanceCreated = manager.install(tierInstance, data, enviromnentInstance, productRelease);
        assertEquals(productInstance.getName(), productInstanceCreated.getName());

    }

}
