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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.ApplicationInstanceDao;
import com.telefonica.euro_iaas.paasmanager.dao.ApplicationReleaseDao;
import com.telefonica.euro_iaas.paasmanager.dao.ArtifactDao;
import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.installator.ProductInstallator;
import com.telefonica.euro_iaas.paasmanager.manager.impl.ApplicationInstanceManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.Artifact;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;


import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jesus.movilla
 */
public class ApplicationInstanceImplTest extends TestCase {

    private ApplicationInstanceDao applicationInstanceDao;

    private ApplicationReleaseDao applicationReleaseDao;
    private ProductReleaseDao productReleaseDao;
    private ProductInstallator productInstallator;
    private ArtifactDao artifactDao;
    private ApplicationInstanceManagerImpl manager = null;

    private ApplicationRelease appRelease = null;
    private EnvironmentInstance environmentInstance = null;
    private TierInstance tierInstance = null;
    private ClaudiaData data;

    @Override
    @Before
    public void setUp() throws Exception {

        applicationInstanceDao = mock(ApplicationInstanceDao.class);
        applicationReleaseDao = mock(ApplicationReleaseDao.class);
        artifactDao = mock(ArtifactDao.class);
        productInstallator = mock(ProductInstallator.class);
        productReleaseDao = mock(ProductReleaseDao.class);

        manager = new ApplicationInstanceManagerImpl();

        manager.setProductInstallator(productInstallator);
        manager.setApplicationInstanceDao(applicationInstanceDao);
        manager.setApplicationReleaseDao(applicationReleaseDao);
        manager.setArtifactDao(artifactDao);
        manager.setProductReleaseDao(productReleaseDao);

        ProductRelease product = new ProductRelease("product", "version");
        Artifact artifact = new Artifact("arti", "version", product);
        List<Artifact> arts = new ArrayList();
        arts.add(artifact);
        appRelease = new ApplicationRelease("product", "2.0");
        appRelease.setArtifacts(arts);

        Tier tier = new Tier();
        tier.setInitialNumberInstances(new Integer(1));
        tier.setMaximumNumberInstances(new Integer(5));
        tier.setMinimumNumberInstances(new Integer(1));
        tier.setName("tierName");
        tier.addProductRelease(product);
        tier.setRegion("region1");
        tier.addNetwork(new Network("uno", "VDC", "region1"));

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);


        Environment environment = new Environment();
        environment.setName("environemntName");

        environment.setTiers(tiers);
        ProductInstance productInstance = new ProductInstance();
        productInstance.setProductRelease(product);
        // productInstance.setVm(vms.get(0));
        productInstance.setStatus(Status.INSTALLED);
        productInstance.setName("name");
        productInstance.setVdc("vdc");

        List<ProductInstance> productInstances = new ArrayList<ProductInstance>();
        productInstances.add(productInstance);

        tierInstance = new TierInstance();
        tierInstance.setName("nametierInstance");
        tierInstance.setTier(tier);
        tierInstance.setVdc("vdc");
        tierInstance.setStatus(Status.INSTALLED);
        tierInstance.setProductInstances(productInstances);

        List<TierInstance> tierInstances = new ArrayList<TierInstance>();
        tierInstances.add(tierInstance);

        environmentInstance = new EnvironmentInstance();
        environmentInstance.setName("name");
        environmentInstance.setTierInstances(tierInstances);
        environmentInstance.setVdc("vdc");
        environmentInstance.setBlueprintName("blue");
        environmentInstance.setStatus(Status.INSTALLED);
        environmentInstance.setEnvironment(environment);

        when(productReleaseDao.load(any(String.class))).thenReturn(product);
        when(applicationReleaseDao.create(any(ApplicationRelease.class))).thenReturn(appRelease);
        when(artifactDao.load(any(String.class))).thenReturn(artifact);

        data = mock(ClaudiaData.class);
        PaasManagerUser user = mock(PaasManagerUser.class);


        when(data.getUser()).thenReturn(user);
        when(data.getOrg()).thenReturn("FIWARE");
        when(data.getService()).thenReturn("deploytm");
        when(data.getVdc()).thenReturn("vdc");
        when(user.getToken()).thenReturn("any");
    }

    /**
     * Test the creation of an applicaiton instance.
     * @throws Exception
     */
    @Test
    public void testCreateApplicationInstance() throws Exception {
        ApplicationInstance appInst = new ApplicationInstance(appRelease, environmentInstance);

        when(applicationInstanceDao.create(any(ApplicationInstance.class)))
                .thenReturn(appInst);

        when(applicationInstanceDao.load(any(String.class), any(String.class)))
                .thenThrow(EntityNotFoundException.class);

        Mockito
                .doNothing()
                .when(productInstallator)
                .installArtifact(any(ClaudiaData.class), any(ProductInstance.class), any(Artifact.class));

        ApplicationInstance appInstanceCreated = manager.install(data, environmentInstance, appRelease);
        assertNotNull(appInstanceCreated);
        assertEquals(
                appInstanceCreated.getName(),
                appRelease.getName() + "-" + environmentInstance.getBlueprintName());

    }

    /**
     * Test the deletion of an application instance.
     * @throws Exception
     */
    @Test
    public void testDeleteApplicationInstance() throws Exception {
        ApplicationInstance appInst = new ApplicationInstance(appRelease, environmentInstance);

        when(applicationInstanceDao.create(any(ApplicationInstance.class)))
                .thenReturn(appInst);

        when(applicationInstanceDao.load(any(String.class), any(String.class)))
                .thenThrow(EntityNotFoundException.class);

        Mockito
                .doNothing()
                .when(productInstallator)
                .installArtifact(any(ClaudiaData.class), any(ProductInstance.class), any(Artifact.class));

        manager.uninstall(data, environmentInstance, appInst);


    }

    /**
     * Test the loading of an application instance.
     * @throws Exception
     */
    @Test
    public void testLoadApplicationInstance() throws Exception {
        ApplicationInstance appInst = new ApplicationInstance(appRelease, environmentInstance);
        when(applicationInstanceDao.create(any(ApplicationInstance.class))).thenReturn(appInst);
        when(applicationInstanceDao.load(any(String.class), any(String.class))).thenReturn(appInst);

        Mockito
                .doNothing()
                .when(productInstallator)
                .installArtifact(any(ClaudiaData.class), any(ProductInstance.class), any(Artifact.class));

        appInst = manager.load(data.getVdc(), appInst.getName());
        assertNotNull(appInst);
        assertEquals(appInst.getName(), appRelease.getName() + "-" + environmentInstance.getBlueprintName());

    }

    /**
     * Test the creation of an application instance with different product release.
     * @throws Exception
     */
    @Test
    public void testCreateApplicationInstancedifferentProductRelease() throws Exception {
        ProductRelease product = new ProductRelease("product2", "version");
        Artifact artifact = new Artifact("arti", "version", product);
        List<Artifact> arts = new ArrayList();
        arts.add(artifact);
        appRelease = new ApplicationRelease("product2", "2.0");
        appRelease.setArtifacts(arts);

        try {
            manager.install(data, environmentInstance, appRelease);
            fail();
        } catch (InvalidEntityException e) {
            // do nothing...
            assertEquals(true, true);
        }
    }


}
