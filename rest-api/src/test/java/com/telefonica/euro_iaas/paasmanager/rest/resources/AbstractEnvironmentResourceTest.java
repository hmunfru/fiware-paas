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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.rest.validation.EnvironmentResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Check the Class AbstractEnvironmentResource.
 */
public class AbstractEnvironmentResourceTest extends TestCase {

    private AbstractEnvironmentResourceImpl environmentResource;
    private EnvironmentManager environmentManager;
    private SystemPropertiesProvider systemPropertiesProvider;
    private EnvironmentResourceValidator environmentResourceValidator;

    /**
     * Initialize the Unit Test.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        environmentResource = new AbstractEnvironmentResourceImpl();
        environmentManager = mock(EnvironmentManager.class);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        environmentResourceValidator = mock(EnvironmentResourceValidator.class);
        environmentResource.setEnvironmentManager(environmentManager);
        environmentResource.setEnvironmentResourceValidator(environmentResourceValidator);
        environmentResource.setSystemPropertiesProvider(systemPropertiesProvider);

        Environment environment = new Environment();
        environment.setName("Name");
        environment.setDescription("Description");

        List<ProductRelease> productRelease = new ArrayList<ProductRelease>();
        productRelease.add(new ProductRelease("test", "0.1"));
        Tier tier = new Tier("tiername", new Integer(1), new Integer(1), new Integer(1), productRelease);
        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        environment.setTiers(tiers);

        Mockito.doNothing().when(environmentResourceValidator).validateAbstractCreate(any(EnvironmentDto.class));
        when(environmentManager.create(any(ClaudiaData.class), any(Environment.class))).thenReturn(environment);
        when(environmentManager.load(any(String.class), any(String.class))).thenThrow(
                new EntityNotFoundException(Environment.class, "", environment));
        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE2");
    }

    /**
     * Test the insertion of a new environment.
     * 
     * @throws Exception
     */
    @Test
    public void testInsertEnvironment() throws Exception {
        Environment environment = new Environment();
        environment.setName("Name");
        environment.setDescription("Description");

        EnvironmentDto environmentDto = new EnvironmentDto();
        environmentDto.setName("Name2");
        environmentDto.setVdc("");
        environmentDto.setDescription("Description");

        // when(environmentManager.load(any(String.class),any(String.class))).thenThrow(
        // new EntityNotFoundException(Environment.class, "", environment));

        List<ProductReleaseDto> productReleaseDto = new ArrayList<ProductReleaseDto>();
        productReleaseDto.add(new ProductReleaseDto("test", "0.1"));
        TierDto tierDto = new TierDto("tiername", new Integer(1), new Integer(1), new Integer(1), productReleaseDto);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        Set<TierDto> tiers = new HashSet<TierDto>();
        tiers.add(tierDto);
        environmentDto.setTierDtos(tiers);

        environmentResource.insert("org", environmentDto);
    }

    /**
     * Test the insertion of a environment together with some tiers on it.
     * 
     * @throws Exception
     */
    @Test
    public void testInsertEnvironmentAndTiers() throws Exception {
        Environment environment = new Environment();
        environment.setName("Name");
        environment.setDescription("Description");

        EnvironmentDto environmentDto = new EnvironmentDto();
        environmentDto.setName("Name2");
        environmentDto.setDescription("Description");

        // when(environmentManager.load(any(String.class),any(String.class))).thenThrow(
        // new EntityNotFoundException(Environment.class, "", environment));

        environmentResource.insert("org", environmentDto);

    }

}
