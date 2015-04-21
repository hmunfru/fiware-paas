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
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.rest.validation.TierResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Test the class AbstractTierResource.
 */
public class AbstractTierResourceTest extends TestCase {

    private AbstractTierResourceImpl tierResource;
    private TierManager tierManager;
    private SystemPropertiesProvider systemPropertiesProvider;
    private EnvironmentManager environmentManager;
    private TierResourceValidator validator;

    /**
     * Initialize the Unit Test.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        tierResource = new AbstractTierResourceImpl();
        tierManager = mock(TierManager.class);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        environmentManager = mock(EnvironmentManager.class);
        validator = mock(TierResourceValidator.class);
        tierResource.setTierManager(tierManager);
        tierResource.setSystemPropertiesProvider(systemPropertiesProvider);
        tierResource.setEnvironmentManager(environmentManager);
        tierResource.setTierResourceValidator(validator);

        Mockito.doNothing()
                .doThrow(new RuntimeException())
                .when(validator)
                .validateCreate(any(ClaudiaData.class), any(TierDto.class), any(String.class), any(String.class));

        List<ProductRelease> productRelease = new ArrayList<ProductRelease>();
        productRelease.add(new ProductRelease("test", "0.1"));
        Tier tier = new Tier("tiername", new Integer(1), new Integer(1), new Integer(1), productRelease);
        tier.setImage("image");
        tier.setIcono("icono");
        tier.setFlavour("flavour");
        tier.setFloatingip("floatingip");
        tier.setKeypair("keypair");

        Environment environment = new Environment("name", "description", null);

        when(tierManager.create(any(ClaudiaData.class), any(String.class), any(Tier.class))).thenReturn(tier);
        when(environmentManager.load(any(String.class), any(String.class))).thenReturn(environment);
        when(environmentManager.update(any(Environment.class))).thenReturn(environment);

        when(tierManager.load(any(String.class), any(String.class), any(String.class))).thenThrow(
                new EntityNotFoundException(Tier.class, "", tier));
        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE2");

    }

    /**
     * Test the insertion of a tier.
     * @throws Exception
     */
    @Test
    public void testInsertTier() throws Exception {

        Environment environment = new Environment("name", "description", null);

        try {
            when(environmentManager.load(any(String.class), any(String.class))).thenReturn(environment);
        } catch (EntityNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        List<ProductReleaseDto> productReleaseDto = new ArrayList<ProductReleaseDto>();
        productReleaseDto.add(new ProductReleaseDto("test", "0.1"));
        TierDto tierDto = new TierDto("tiername", new Integer(1), new Integer(1), new Integer(1), productReleaseDto);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        tierDto.setFloatingip("floatingip");
        tierDto.setKeypair("keypair");

        List<TierDto> tiers = new ArrayList<TierDto>();
        tiers.add(tierDto);

        tierResource.insert("org", "name", tierDto);

    }

    /**
     * Test the insertion of a tier with no products.
     * @throws Exception
     */
    @Test
    public void testInsertTierNoProducts() throws Exception {

        Environment environment = new Environment("name", "description", null);

        try {
            when(environmentManager.load(any(String.class), any(String.class))).thenReturn(environment);
        } catch (EntityNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        TierDto tierDto = new TierDto("tiername", new Integer(1), new Integer(1), new Integer(1), null);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        tierDto.setFloatingip("floatingip");
        tierDto.setKeypair("keypair");

        List<TierDto> tiers = new ArrayList<TierDto>();
        tiers.add(tierDto);

        tierResource.insert("org", "name", tierDto);

    }

}
