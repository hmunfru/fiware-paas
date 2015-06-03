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
import static org.mockito.Matchers.anyString;
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

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;

import com.telefonica.euro_iaas.paasmanager.claudia.QuotaClient;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentInstanceDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidOVFException;
import com.telefonica.euro_iaas.paasmanager.exception.QuotaExceededException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentInstanceDto;
import com.telefonica.euro_iaas.paasmanager.rest.validation.EnvironmentInstanceResourceValidatorImpl;
import com.telefonica.euro_iaas.paasmanager.rest.validation.ResourceValidator;
import com.telefonica.euro_iaas.paasmanager.rest.validation.TierResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Test the EnvironmetnInstanceValidator class.
 */
public class EnvironmetnInstanceValidatorTest extends TestCase {
    private EnvironmentInstanceResourceValidatorImpl validator;
    private TierResourceValidator tierResourceValidator;
    private EnvironmentInstanceDao environmentInstanceDao;
    private Environment environment;
    private SystemPropertiesProvider systemPropertiesProvider;
    private ResourceValidator resourceValidator;

    /**
     * Initialize the Unit Test.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        validator = new EnvironmentInstanceResourceValidatorImpl();
        tierResourceValidator = mock(TierResourceValidator.class);
        environmentInstanceDao = mock(EnvironmentInstanceDao.class);
        resourceValidator = mock(ResourceValidator.class);
        validator.setEnvironmentInstanceDao(environmentInstanceDao);
        validator.setTierResourceValidator(tierResourceValidator);
        validator.setResourceValidator(resourceValidator);

        Mockito.doNothing().when(resourceValidator).validateName(anyString());
        Mockito.doNothing().when(resourceValidator).validateDescription(anyString());

        ProductRelease productRelease = new ProductRelease("product", "2.0");
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier("name", new Integer(1), new Integer(1), new Integer(1), productReleases, "flavour",
                "image", "icono", "keypair", "floatingip", "payload");

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier);

        environment = new Environment();
        environment.setName("environemntName");
        environment.setDescription("description");

        environment.setTiers(tiers);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        Mockito.doThrow(new EntityNotFoundException(EnvironmentInstance.class, "dd", tiers))
                .when(environmentInstanceDao).load(any(String.class));

        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FIWARE");

    }

    /**
     * Test the creation of an environment instance.
     * @throws Exception
     */
    @Test
    public void testCreateEnviornmentInstance() throws Exception {
        EnvironmentInstanceDto environmentInstanceDto = new EnvironmentInstanceDto();
        environmentInstanceDto.setDescription("description");
        environmentInstanceDto.setEnvironmentDto(environment.toDto());
        environmentInstanceDto.setBlueprintName("blueprintName");
        ClaudiaData claudiaData = mock(ClaudiaData.class);
        QuotaClient quotaClient = mock(QuotaClient.class);
        validator.setQuotaClient(quotaClient);

        // when
        validator.validateCreate(environmentInstanceDto, systemPropertiesProvider, claudiaData);

    }

    /**
     * Test the creation of an environment instance without eny environment.
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     * @throws InfrastructureException
     * @throws InvalidOVFException
     * @throws QuotaExceededException
     */
    @Test
    public void testCreateEnviornmentInstanceNoEnvironment() throws
            EntityNotFoundException, AlreadyExistsEntityException, InfrastructureException,
            InvalidOVFException, QuotaExceededException {
        EnvironmentInstanceDto environmentInstanceDto = new EnvironmentInstanceDto();
        environmentInstanceDto.setDescription("description");
        environmentInstanceDto.setBlueprintName("BlueprintName");
        boolean exception = false;
        ClaudiaData claudiaData = mock(ClaudiaData.class);

        try {
            validator.validateCreate(environmentInstanceDto, systemPropertiesProvider, claudiaData);
        } catch (InvalidEntityException e) {
            exception = true;
        }
        assertTrue(exception);

    }

}
