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

import com.telefonica.euro_iaas.paasmanager.manager.ProductReleaseManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.EnvironmentInstanceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
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
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentInstanceDto;
import com.telefonica.euro_iaas.paasmanager.rest.validation.EnvironmentInstanceResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Test the class EnvironmentInstanceResourceImpl.
 */
public class EnvironmentInstanceResourceImplTest {

    private EnvironmentInstanceResourceImpl environmentInstanceResource;
    private SystemPropertiesProvider systemPropertiesProvider;
    private EnvironmentInstanceAsyncManager environmentInstanceAsyncManager;
    private EnvironmentInstanceResourceValidator environmentInstanceResourceValidator;
    private ProductReleaseManager productReleaseManager;
    private String vdc = "vdc";
    private String org = "org";
    private String callback = "callback";
    private Environment environment;
    private Set<Tier> tiers;
    private TaskManager taskManager;
    private Task task;

    /**
     * Initialize the Unit Test.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        environmentInstanceResource = new EnvironmentInstanceResourceImpl();
        environmentInstanceResourceValidator = mock(EnvironmentInstanceResourceValidator.class);
        productReleaseManager = mock(ProductReleaseManager.class);
        environmentInstanceAsyncManager = mock(EnvironmentInstanceAsyncManager.class);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        taskManager = mock(TaskManager.class);
        environmentInstanceResource.setValidator(environmentInstanceResourceValidator);

        environmentInstanceResource.setSystemPropertiesProvider(systemPropertiesProvider);
        environmentInstanceResource.setEnvironmentInstanceAsyncManager(environmentInstanceAsyncManager);
        environmentInstanceResource.setTaskManager(taskManager);

        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("");
        Mockito.doNothing().when(environmentInstanceAsyncManager)
                .create(any(ClaudiaData.class), any(EnvironmentInstance.class), any(Task.class), any(String.class));

        // environmentOvfInstanceResource.setOvfGeneration(ovfGeneration);
        // environmentOvfInstanceResource.setProductReleaseManager(productReleaseManager);
        // environmentOvfInstanceResource.setSystemPropertiesProvider(systemPropertiesProvider);
        ProductRelease productRelease = new ProductRelease("product", "2.0");
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");

        tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier);

        environment = new Environment();
        environment.setName("environemntName");
        environment.setDescription("description");

        environment.setTiers(tiers);

        task = new Task(TaskStates.RUNNING);
        task.setDescription("description");
        task.setVdc(vdc);
        task.setEnvironment(environment.getName());

        when(taskManager.createTask(any(Task.class))).thenReturn(task);

        ProductInstance productInstance = new ProductInstance();
        productInstance.setProductRelease(productRelease);
        // productInstance.setVm(vms.get(0));
        productInstance.setStatus(Status.INSTALLED);
        productInstance.setName("name");
        productInstance.setVdc("vdc");

        List<ProductInstance> productInstances = new ArrayList<ProductInstance>();
        productInstances.add(productInstance);

        TierInstance tierInstance = new TierInstance();
        tierInstance.setName("nametierInstance");
        tierInstance.setTier(tier);
        tierInstance.setVdc("vdc");
        tierInstance.setStatus(Status.INSTALLED);
        tierInstance.setProductInstances(productInstances);

        List<TierInstance> tierInstances = new ArrayList<TierInstance>();
        tierInstances.add(tierInstance);

    }

    /**
     * Test the creation of an environment based on a OVF file.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateOvfEnvironmentInstance() throws Exception {

        // Given
        EnvironmentInstanceDto environmentInstanceDto = new EnvironmentInstanceDto();
        environmentInstanceDto.setDescription("description");
        environmentInstanceDto.setBlueprintName("BlueprintName");
        environmentInstanceDto.setEnvironmentDto(environment.toDto());

        // When

        Task task = environmentInstanceResource.create(org, vdc, environmentInstanceDto, callback);

        // Then
        verify(environmentInstanceAsyncManager, times(1)).create(any(ClaudiaData.class),
                any(EnvironmentInstance.class), any(Task.class), any(String.class));

        assertNotNull(task);
    }
}
