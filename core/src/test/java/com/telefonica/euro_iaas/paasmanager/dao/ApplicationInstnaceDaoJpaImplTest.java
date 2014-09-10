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
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ApplicationInstanceSearchCriteria;

/**
 * Unit test for TierDaoJpaImplTest.
 *
 * @author Jesus M. Movilla
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring-test-db-config.xml", "classpath:/spring-dao-config.xml" })
public class ApplicationInstnaceDaoJpaImplTest {

    @Autowired
    private ApplicationInstanceDao applicationInstanceDao;

    @Autowired
    private ApplicationReleaseDao applicationReleaseDao;

    @Autowired
    private EnvironmentInstanceDao environmentInstanceDao;

    public static final String APP_NAME = "app";
    public static final String VDC = "vdc";
    public static final String ENV = "env";

    private EnvironmentInstance environmentInstance;


    /**
     * Initialization of the Test Case.
     * @throws AlreadyExistsEntityException
     */
    @Before
    public void setUp() throws AlreadyExistsEntityException {

        environmentInstance = new EnvironmentInstance("blue", "description");
        environmentInstance.setName(ENV);
        environmentInstance.setVdc(VDC);
        environmentInstance = environmentInstanceDao.create(environmentInstance);
    }

    /**
     * Test the create method.
     */
    @Test
    public void testCreate1() throws Exception {
        ApplicationRelease applicationRelease = new ApplicationRelease("product", "version");
        applicationRelease = applicationReleaseDao.create(applicationRelease);
        ApplicationInstance appIn = new ApplicationInstance(applicationRelease, environmentInstance);

        appIn = applicationInstanceDao.create(appIn);

        assertNotNull(appIn);
        assertNotNull(appIn.getId());

        assertEquals(appIn.getName(), applicationRelease.getName() + "-" + environmentInstance.getBlueprintName());
    }

    /**
     * Test the create method.
     */
    @Test
    public void testLoadNull() throws Exception {
        ApplicationRelease applicationRelease = new ApplicationRelease("product2", "version");
        applicationRelease = applicationReleaseDao.create(applicationRelease);
        ApplicationInstance appIn = new ApplicationInstance(applicationRelease, environmentInstance);

        appIn = applicationInstanceDao.create(appIn);
        appIn = applicationInstanceDao.load(
                    applicationRelease.getName() + "-" + environmentInstance.getBlueprintName());

        assertNull(appIn);
    }

    /**
     * Test the load of an application instance.
     * @throws Exception
     */
    @Test
    public void testLoad() throws Exception {
        ApplicationRelease applicationRelease = new ApplicationRelease("product3", "version");
        applicationRelease = applicationReleaseDao.create(applicationRelease);
        ApplicationInstance appIn = new ApplicationInstance(applicationRelease, environmentInstance);
        int num = applicationInstanceDao.findAll().size();
        appIn = applicationInstanceDao.create(appIn);
        appIn = applicationInstanceDao.load(
                    applicationRelease.getName() + "-" + environmentInstance.getBlueprintName(), VDC);

        assertNotNull(appIn);
        assertNotNull(appIn.getId());
        assertEquals(applicationInstanceDao.findAll().size(), num + 1);
        assertEquals(appIn.getName(), applicationRelease.getName() + "-" + environmentInstance.getBlueprintName());
    }

    /**
     * Test an application instance using a search criteria.
     * @throws Exception
     */
    @Test
    public void testFindByCriteria() throws Exception {
        ApplicationInstanceSearchCriteria criteria = new ApplicationInstanceSearchCriteria();
        criteria.setVdc(VDC);
        criteria.setEnvironmentInstance(environmentInstance.getName());
        List<ApplicationInstance> lAppInt = applicationInstanceDao.findByCriteria(criteria);
        int num = lAppInt.size();

        ApplicationRelease applicationRelease = new ApplicationRelease("product4", "version");
        applicationRelease = applicationReleaseDao.create(applicationRelease);
        ApplicationInstance appIn = new ApplicationInstance(applicationRelease, environmentInstance);
        appIn = applicationInstanceDao.create(appIn);

        lAppInt = applicationInstanceDao.findByCriteria(criteria);
        assertNotNull(lAppInt);
        assertEquals(lAppInt.size(), num + 1);
    }

    /**
     * Test an application instance using a search criteria.
     * @throws Exception
     */
    @Test
    public void testFindByCriteria2() throws Exception {
        ApplicationRelease applicationRelease = new ApplicationRelease("product5", "version");
        applicationRelease = applicationReleaseDao.create(applicationRelease);
        ApplicationInstanceSearchCriteria criteria = new ApplicationInstanceSearchCriteria();
        criteria.setVdc(VDC);
        criteria.setEnvironmentInstance(environmentInstance.getName());
        criteria.setApplicatonRelease(applicationRelease);


        ApplicationInstance appIn = new ApplicationInstance(applicationRelease, environmentInstance);
        appIn = applicationInstanceDao.create(appIn);
        List<ApplicationInstance> lAppInt = applicationInstanceDao.findByCriteria(criteria);

        assertNotNull(lAppInt);
        assertEquals(lAppInt.size(), 1);
    }


    public void setApplicationInstanceDao(ApplicationInstanceDao appInstanceDao) {
        this.applicationInstanceDao = appInstanceDao;
    }

    public void setApplicationReleaseDao(ApplicationReleaseDao applicationReleaseDao) {
        this.applicationReleaseDao = applicationReleaseDao;
    }

    public void setEnvironmentInstanceDao(EnvironmentInstanceDao environmentInstanceDao) {
        this.environmentInstanceDao = environmentInstanceDao;
    }


}
