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


import java.util.ArrayList;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.Artifact;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ApplicationReleaseSearchCriteria;

/**
 * Unit test for TierDaoJpaImplTest.
 *
 * @author Jesus M. Movilla
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring-test-db-config.xml", "classpath:/spring-dao-config.xml" })
public class ApplicationReleaseDaoJpaImplTest {


    @Autowired
    private ApplicationReleaseDao applicationReleaseDao;

    @Autowired
    private ArtifactDao artifactDao;

    @Autowired
    private ProductReleaseDao productReleaseDao;


    public static final String APP_NAME = "app";
    public static final String VDC = "vdc";
    public static final String ENV = "env";
    private ProductRelease product;

    /**
     * Initialization of the Test case.
     * @throws AlreadyExistsEntityException
     */
    @Before
    public void setUp() throws AlreadyExistsEntityException {
        product = new ProductRelease("product", "version");
        product = productReleaseDao.create(product);
    }

    /**
     * Test the creation of an artifact.
     * @throws Exception
     */
    @Test
    public void testCreateArtifact() throws Exception {
        Artifact artifact = new Artifact("artifact", "path", product);
        artifact = artifactDao.create(artifact);

        assertNotNull(artifact);
        assertNotNull(artifact.getId());
        assertEquals(artifact.getName(), "artifact");
        assertEquals(artifact.getPath(), "path");
    }

    /**
     * Test the loading of a specific artifact.
     * @throws Exception
     */
    @Test
    public void testLoadArtifact() throws Exception {
        Artifact artifact = new Artifact("artifact2", "path", product);
        int num = artifactDao.findAll().size();
        artifactDao.create(artifact);
        artifact = artifactDao.load(artifact.getName());
        assertNotNull(artifact);
        assertNotNull(artifact.getId());
        assertEquals(artifact.getName(), "artifact2");
        assertEquals(artifact.getPath(), "path");
        assertEquals(artifactDao.findAll().size(), num + 1);
    }

    /**
     * Test the deletion of an specific artifact.
     * @throws Exception
     */
    @Test(expected = EntityNotFoundException.class)
    public void testDeleteArtifact() throws Exception {
        Artifact artifact = new Artifact("artifact3", "path", product);
        artifactDao.create(artifact);
        artifactDao.remove(artifact);
        artifactDao.load(artifact.getName());

    }

    /**
     * Test the creation of an application release.
     * @throws Exception
     */
    @Test
    public void testCreateAppRelease() throws Exception {
        ApplicationRelease applicationRelease = new ApplicationRelease("productV", "version");
        applicationRelease = applicationReleaseDao.create(applicationRelease);


        assertNotNull(applicationRelease);
        assertNotNull(applicationRelease.getId());
        assertEquals(applicationRelease.getName(), "productV");
        assertEquals(applicationRelease.getVersion(), "version");
    }

    /**
     * Test the creation of an application release with an artifact.
     * @throws Exception
     */
    @Test
    public void testCreateAppReleaseII() throws Exception {
        Artifact artifact = new Artifact("artifact5", "path", product);
        artifact = artifactDao.create(artifact);
        ApplicationRelease applicationRelease = new ApplicationRelease("productII", "version");
        List<Artifact> artifacts = new ArrayList<Artifact>();
        artifacts.add(artifact);
        applicationRelease.setArtifacts(artifacts);
        int num = applicationReleaseDao.findAll().size();
        applicationRelease = applicationReleaseDao.create(applicationRelease);


        assertNotNull(applicationRelease);
        assertNotNull(applicationRelease.getId());
        assertEquals(applicationRelease.getName(), "productII");
        assertEquals(applicationRelease.getVersion(), "version");
        assertEquals(applicationRelease.getArtifacts().size(), 1);
        assertEquals(applicationReleaseDao.findAll().size(), num + 1);
    }

    /**
     * Test the searching of an application release given a search criteria.
     * @throws Exception
     */
    @Test
    public void testFindByCriteria() throws Exception {
        Artifact artifact = new Artifact("artifact6", "path", product);
        artifact = artifactDao.create(artifact);
        ApplicationRelease applicationRelease = new ApplicationRelease("productIII", "version");
        List<Artifact> artifacts = new ArrayList<Artifact>();
        artifacts.add(artifact);
        applicationRelease.setArtifacts(artifacts);


        ApplicationReleaseSearchCriteria criteria = new ApplicationReleaseSearchCriteria();
        criteria.setArtifact(artifact);

        List<ApplicationRelease> lApp = applicationReleaseDao.findByCriteria(criteria);
        int num = lApp.size();
        assertNotNull(lApp);

        applicationRelease = applicationReleaseDao.create(applicationRelease);
        assertEquals(applicationReleaseDao.findByCriteria(criteria).size(), num + 1);

    }

    /**
     * Test the loading of an application release.
     * @throws Exception
     */
    @Test
    public void testLoadAppRelease() throws Exception {
        ApplicationRelease applicationRelease = new ApplicationRelease("product1", "version");
        applicationRelease = applicationReleaseDao.create(applicationRelease);
        applicationRelease = applicationReleaseDao.load("product1");


        assertEquals(applicationRelease.getName(), "product1");
        assertEquals(applicationRelease.getVersion(), "version");
        assertEquals(applicationRelease.getArtifacts().size(), 0);

    }

    /**
     * Test the deletion of an application release.
     * @throws Exception
     */
    @Test(expected = EntityNotFoundException.class)
    public void testDeleteAppRelease() throws Exception {
        ApplicationRelease applicationRelease = new ApplicationRelease("productIV", "version");
        applicationRelease = applicationReleaseDao.create(applicationRelease);
        applicationRelease = applicationReleaseDao.load("productIV");
        applicationReleaseDao.remove(applicationRelease);
        applicationReleaseDao.load("productIV-version");
    }


    public void setApplicationReleaseDao(ApplicationReleaseDao applicationReleaseDao) {
        this.applicationReleaseDao = applicationReleaseDao;
    }

    public void setArtifactDao(ArtifactDao artifactDao) {
        this.artifactDao = artifactDao;
    }

    public void setProductReleaseDao(ProductReleaseDao productReleaseDao) {
        this.productReleaseDao = productReleaseDao;
    }


}
