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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.exception.AlreadyExistEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidOVFException;
import com.telefonica.euro_iaas.paasmanager.exception.QuotaExceededException;
import com.telefonica.euro_iaas.paasmanager.manager.ApplicationInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierInstanceManager;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.Artifact;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.dto.ApplicationInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ApplicationReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ArtifactDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierInstanceDto;
import com.telefonica.euro_iaas.paasmanager.rest.resources.ApplicationInstanceResource;
import com.telefonica.euro_iaas.paasmanager.rest.resources.EnvironmentInstanceResource;
import com.telefonica.euro_iaas.paasmanager.rest.resources.EnvironmentResource;
import com.telefonica.euro_iaas.paasmanager.rest.resources.TierInstanceResource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContextTest.xml" })
@ActiveProfiles("dummy")
public class ApplicationInstanceITest {

    @Autowired
    private EnvironmentResource environmentResource;

    @Autowired
    private EnvironmentInstanceResource environmentInstanceResource;

    @Autowired
    private ProductReleaseDao productReleaseDao;

    @Autowired
    private EnvironmentManager environmentManager;

    @Autowired
    private EnvironmentInstanceManager environmentInstanceManager;
    
    @Autowired
    private ApplicationInstanceResource applicationInstanceResource;
    
    @Autowired
    private ApplicationInstanceManager applicationInstanceManager;
    
    
    

    String org = "FIWARE";
    String vdc = "6571e3422ad84f7d828ce2f30373b3d4";
    String PRODUCT_NAME = "productApp";
    String PRODUCT_VERSION = "VERSION"; 
    String ENVIRONMENT_NAME = "ENVIRONMENTNAME";
    String DESCRIPTION = "VERSION";
    String TIER_NAME = "TIERNAME";
    String BLUEPRINT_NAME = "BLUEPRINTNAME";
    public static String ARTIFACT_NAME ="ARTIFACTNAME";
    public static String ARTIFACT_PATH ="ARTIFACTPATH";
    public static String APP_NAME ="APPNAME";
    public static String APP_VERSION="APPVERSION";

    

    @Test
    public void testCreateApplicationInstance() throws Exception {

        ProductRelease product = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION,DESCRIPTION, null);
        product = productReleaseDao.create(product);


        Environment environmentBk = new Environment();
        environmentBk.setName(ENVIRONMENT_NAME);
        environmentBk.setDescription(DESCRIPTION);
        Tier tierbk = new Tier(TIER_NAME, new Integer(1), new Integer(1), new Integer(1), null);
        tierbk.addProductRelease(product);
        environmentBk.addTier(tierbk);
        environmentResource.insert(org, vdc, environmentBk.toDto());


        EnvironmentInstanceDto envInst = new EnvironmentInstanceDto();
        envInst.setBlueprintName(BLUEPRINT_NAME);
        envInst.setDescription(DESCRIPTION);
        envInst.setEnvironmentDto(environmentBk.toDto());
        List<TierInstanceDto> tierInstanceDtos = new ArrayList(2);
        TierInstanceDto tierInstanceDto = new TierInstanceDto();
        TierDto tierDto = new TierDto();
        tierDto.setInitialNumberInstances(1);
        tierInstanceDto.setTierDto(tierDto);
        tierInstanceDtos.add(tierInstanceDto);
        envInst.setTierInstances(tierInstanceDtos);

        Task task = environmentInstanceResource.create(org, vdc, envInst, "");

        Thread.sleep(5000);

        assertEquals(Task.TaskStates.RUNNING, task.getStatus());
        
        EnvironmentInstance env = environmentInstanceManager.load(vdc, BLUEPRINT_NAME);
        List<Status> status = new ArrayList();
        status.add(Status.INSTALLED);
        List<ApplicationInstance> apps = applicationInstanceResource.findAll(new Integer(1), new Integer(2), "", "",status, vdc, BLUEPRINT_NAME);
        
        assertNotNull (apps);
        ProductRelease productRelease = new ProductRelease (PRODUCT_NAME, PRODUCT_VERSION);
        ArtifactDto artifactDto = new ArtifactDto (ARTIFACT_NAME, ARTIFACT_PATH, productRelease.toDto());
        ArrayList<ArtifactDto> artifactsDto = new ArrayList<ArtifactDto> ();
        artifactsDto.add(artifactDto);

        ApplicationReleaseDto applicationReleaseDto = new ApplicationReleaseDto (APP_NAME, APP_VERSION, artifactsDto);
        ApplicationRelease appRelease = applicationReleaseDto.fromDto();
        
        task = applicationInstanceResource.install(org, vdc, BLUEPRINT_NAME, applicationReleaseDto, "");
        
        Thread.sleep(5000);

        assertEquals(Task.TaskStates.RUNNING, task.getStatus());
        
        
        ApplicationInstance app = applicationInstanceResource.load(vdc, env.getName(), appRelease.getName()+"-"+BLUEPRINT_NAME);
        
        applicationInstanceManager.load(vdc, appRelease.getName()+"-"+BLUEPRINT_NAME);
        
        assertEquals (app.getName(), appRelease.getName()+"-"+BLUEPRINT_NAME);
        
        applicationInstanceResource.uninstall(org, vdc, BLUEPRINT_NAME, appRelease.getName()+"-"+BLUEPRINT_NAME, "");

        

    }

   
}
