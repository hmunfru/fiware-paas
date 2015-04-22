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

package com.telefonica.euro_iaas.paasmanager.rest.validation;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.AlreadyExistEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentInstanceException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Test class for validation of environment.
 *
 * @author henar
 */
public class EnviornmentResourceValidatorImplTest {
    private EnvironmentResourceValidatorImpl environmentResourceValidator;
    private EnvironmentManager environmentManager;
    private EnvironmentInstanceManager environmentInstanceManager;
    private ResourceValidator resourceValidator;
    private TierResourceValidator tierResourceValidator;
    private SystemPropertiesProvider systemPropertiesProvider;

    /**
     * Initialize the Unit Test.
     * 
     * @throws EntityNotFoundException
     * @throws com.telefonica.euro_iaas.paasmanager.exception.InvalidEntityException
     * @throws AlreadyExistEntityException
     * @throws InvalidEnvironmentInstanceException
     */
    @Before
    public void setUp() throws EntityNotFoundException,
            com.telefonica.euro_iaas.paasmanager.exception.InvalidEntityException, AlreadyExistEntityException,
            InvalidEnvironmentInstanceException {

        environmentResourceValidator = new EnvironmentResourceValidatorImpl();
        resourceValidator = mock(ResourceValidator.class);
        environmentManager = mock(EnvironmentManager.class);
        tierResourceValidator = mock(TierResourceValidator.class);
        environmentResourceValidator.setResourceValidator(resourceValidator);

        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        environmentInstanceManager = mock(EnvironmentInstanceManager.class);
        environmentResourceValidator.setEnvironmentManager(environmentManager);
        environmentResourceValidator.setEnvironmentInstanceManager(environmentInstanceManager);
        environmentResourceValidator.setTierResourceValidator(tierResourceValidator);
        Mockito.doNothing().when(resourceValidator).validateName(anyString());
        Mockito.doNothing().when(resourceValidator).validateDescription(anyString());
        Mockito.doNothing().when(tierResourceValidator).validateCreateAbstract(any(TierDto.class), anyString());

    }

    /**
     * Validate that an environment data is ok.
     * 
     * @throws Exception
     */
    @Test
    public void shouldValidateEnvironmentDataOK() throws Exception {
        // given

        EnvironmentDto envDto = new EnvironmentDto();
        envDto.setName("name");
        envDto.setDescription("description");
        when(environmentManager.load(anyString(), anyString())).thenThrow(
                new EntityNotFoundException(Environment.class, "dd", envDto.fromDto()));

        ClaudiaData claudiaData = mock(ClaudiaData.class);

        environmentResourceValidator.validateCreate(claudiaData, envDto, "vdc");

    }

    /**
     * Test the validation of an abstract environment.
     * 
     * @throws EntityNotFoundException
     */
    @Test
    public void shouldValidateAbstractEnv() throws EntityNotFoundException {

        EnvironmentDto envDto = new EnvironmentDto();
        envDto.setName("name");
        envDto.setDescription("description");
        when(environmentManager.load(anyString(), anyString())).thenThrow(
                new EntityNotFoundException(Environment.class, "dd", envDto.fromDto()));

        try {
            environmentResourceValidator.validateAbstractCreate(envDto);
        } catch (Exception e) {
            fail();
        }

    }

    /**
     * Test the validation of a delete environment.
     * 
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     */
    @Test
    public void shouldValidateDeleteEnv() throws EntityNotFoundException, InvalidEntityException {

        EnvironmentDto envDto = new EnvironmentDto();
        envDto.setName("name");
        envDto.setDescription("description");
        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        Set<TierDto> ltiers = new HashSet<TierDto>();
        ltiers.add(tierDTO);
        envDto.setTierDtos(ltiers);
        when(environmentManager.load(anyString(), anyString())).thenReturn(envDto.fromDto());
        when(environmentInstanceManager.findByCriteria(any(EnvironmentInstanceSearchCriteria.class))).thenReturn(
                new ArrayList<EnvironmentInstance>());

        environmentResourceValidator.validateDelete(envDto.getName(), "vdc");

    }

    /**
     * Test the creation of a abstract environment with tiers.
     * 
     * @throws EntityNotFoundException
     * @throws AlreadyExistEntityException
     * @throws InvalidEntityException
     * @throws InvalidEnvironmentInstanceException
     */
    @Test
    public void shouldValidateAbstractEnvWitTiers() throws EntityNotFoundException, AlreadyExistEntityException,
            InvalidEntityException, InvalidEnvironmentInstanceException {

        EnvironmentDto envDto = new EnvironmentDto();
        envDto.setName("name");
        envDto.setDescription("description");
        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        Set<TierDto> ltiers = new HashSet<TierDto>();
        ltiers.add(tierDTO);
        envDto.setTierDtos(ltiers);
        when(environmentManager.load(anyString(), anyString())).thenThrow(
                new EntityNotFoundException(Environment.class, "dd", envDto.fromDto()));

        environmentResourceValidator.validateAbstractCreate(envDto);

    }

    /**
     * Test the creation of an abstract enviroment when it already exists.
     * 
     * @throws EntityNotFoundException
     * @throws AlreadyExistEntityException
     * @throws InvalidEntityException
     * @throws InvalidEnvironmentInstanceException
     */
    @Test(expected = AlreadyExistEntityException.class)
    public void shouldValidateAbstractEnvAlreadyExists() throws EntityNotFoundException, AlreadyExistEntityException,
            InvalidEntityException, InvalidEnvironmentInstanceException {

        EnvironmentDto envDto = new EnvironmentDto();
        envDto.setName("name");
        envDto.setDescription("description");
        when(environmentManager.load(anyString(), anyString())).thenReturn(envDto.fromDto());
        environmentResourceValidator.validateAbstractCreate(envDto);

    }

}
