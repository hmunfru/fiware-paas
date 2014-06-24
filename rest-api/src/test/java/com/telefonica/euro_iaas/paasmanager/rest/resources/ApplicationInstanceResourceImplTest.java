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

package com.telefonica.euro_iaas.paasmanager.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.manager.ApplicationInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.ApplicationReleaseManager;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.ProductReleaseManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.ApplicationInstanceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.EnvironmentInstanceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Task.TaskStates;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.ApplicationReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ArtifactDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;

import com.telefonica.euro_iaas.paasmanager.rest.validation.ApplicationInstanceResourceValidator;
import com.telefonica.euro_iaas.paasmanager.rest.validation.EnvironmentInstanceResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

public class ApplicationInstanceResourceImplTest {

	public ApplicationInstanceResourceImpl applicationInstanceResource;
    public EnvironmentInstanceManager environmentInstanceManager;
    public ApplicationReleaseManager applicationReleaseManager;
    public ApplicationInstanceManager applicationInstanceManager;
    public ApplicationInstanceAsyncManager applicationInstanceAsyncManager;
    public SystemPropertiesProvider systemPropertiesProvider;
    public EnvironmentInstanceAsyncManager environmentInstanceAsyncManager;
    public ApplicationInstanceResourceValidator validator;
    public String vdc = "vdc";
    public String org = "org";
    public String callback = "callback";
    public Environment environment;
    public Set<Tier> tiers;
    public TaskManager taskManager;
    public Task task;


    @Before
    public void setUp() throws Exception {
    	applicationInstanceResource = new ApplicationInstanceResourceImpl ();
    	environmentInstanceManager = mock( EnvironmentInstanceManager.class);
    	applicationInstanceManager = mock( ApplicationInstanceManager.class);
    	applicationReleaseManager = mock( ApplicationReleaseManager.class);
    	applicationInstanceAsyncManager = mock (ApplicationInstanceAsyncManager.class);
        environmentInstanceAsyncManager = mock(EnvironmentInstanceAsyncManager.class);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        taskManager = mock(TaskManager.class);
        validator = mock (ApplicationInstanceResourceValidator.class);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        
        applicationInstanceResource.setApplicationInstanceAsyncManager(applicationInstanceAsyncManager);
        applicationInstanceResource.setApplicationInstanceManager(applicationInstanceManager);
        applicationInstanceResource.setApplicationReleaseManager(applicationReleaseManager);
        applicationInstanceResource.setEnvironmentInstanceAsyncManager(environmentInstanceAsyncManager);
        applicationInstanceResource.setEnvironmentInstanceManager(environmentInstanceManager);
        applicationInstanceResource.setTaskManager(taskManager);
        applicationInstanceResource.setValidator(validator);
        applicationInstanceResource.setSystemPropertiesProvider(systemPropertiesProvider);


        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("");
       

    }


    @Test
    public void testInstallApplication() throws Exception {
    	
    	List<ArtifactDto> artifacts = new ArrayList ();
    	ArtifactDto artifact = new ArtifactDto ();
    	artifacts.add(artifact);
    	ApplicationReleaseDto application = new ApplicationReleaseDto ("APP", "version", artifacts);

    	Mockito.doNothing().when (validator).validateInstall(any(String.class), any(String.class), any(ApplicationReleaseDto.class));
    	Mockito.doNothing().when(applicationInstanceAsyncManager).install(any(ClaudiaData.class), any(String.class), 
    			any(ApplicationRelease.class), any(Task.class), any(String.class));
        Task tasks = new Task();
        tasks.setStatus(TaskStates.RUNNING);
    	when(taskManager.createTask(any(Task.class))).thenReturn(tasks);
    	task = applicationInstanceResource.install(org, vdc, "environment", application, callback);
    	
    	
    	assertNotNull (task);
        assertEquals (task.getStatus(), TaskStates.RUNNING);

    }
    
    @Test
    public void testFindAll () {
    	//applicationInstanceResource.findAll(page, pageSize, orderBy, orderType, status, vdc, environmentInstance, productInstanceName, applicationName);
    	
    }
    
    @Test
    public void testLoad () throws EntityNotFoundException {
    	ApplicationInstance app = new ApplicationInstance ();
    	when(applicationInstanceManager.load( any(String.class), any(String.class))).thenReturn(app);
    	ApplicationInstance apps= applicationInstanceResource.load(vdc, "environment", "APP");
    	assertNotNull (apps);
    	
    }
    
    @Test
    public void testUninstall() throws EntityNotFoundException {
    	EnvironmentInstance env = new EnvironmentInstance ();
    	ApplicationInstance app = new ApplicationInstance ();
    	when(environmentInstanceManager.load(any(String.class), any(String.class))).thenReturn(env);
    	when(applicationInstanceManager.load( any(String.class), any(String.class))).thenReturn(app);
    	Mockito.doNothing().when(applicationInstanceAsyncManager).uninstall(any(ClaudiaData.class), any(String.class), 
    			any(String.class), any(Task.class), any(String.class));
        Task tasks = new Task();
        tasks.setStatus(TaskStates.RUNNING);
    	when(taskManager.createTask(any(Task.class))).thenReturn(tasks);
    	task = applicationInstanceResource.uninstall(org, vdc, "env", "APP", callback); 
    	assertNotNull (task);
        assertEquals (task.getStatus(), TaskStates.RUNNING);
    	
    }
}
