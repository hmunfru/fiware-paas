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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;


import com.telefonica.euro_iaas.paasmanager.exception.ApplicationInstanceNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidApplicationReleaseException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEntityException;

import com.telefonica.euro_iaas.paasmanager.manager.ApplicationInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;

import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;

import com.telefonica.euro_iaas.paasmanager.model.dto.ApplicationReleaseDto;

/**
 * Test the AppInstanceResourceValidatorImpl class.
 */
public class AppInstanceResourceValidatorImplTest {

    private ApplicationInstanceResourceValidatorImpl appInstanceResourceValidator;
    private ApplicationInstanceManager applicationInstanceManager;
    private EnvironmentInstanceManager environmentInstanceManager;

    /**
     * Initialize the Unit Test.
     *
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     */
    @Before
    public void setUp() throws EntityNotFoundException, InvalidEntityException {
        appInstanceResourceValidator = new ApplicationInstanceResourceValidatorImpl();
        applicationInstanceManager = mock(ApplicationInstanceManager.class);
        environmentInstanceManager = mock(EnvironmentInstanceManager.class);
        appInstanceResourceValidator.setApplicationInstanceManager(applicationInstanceManager);
        appInstanceResourceValidator.setEnvironmentInstanceManager(environmentInstanceManager);

    }

    /**
     * Test the installation of an application instance.
     *
     * @throws InvalidApplicationReleaseException
     * @throws ApplicationInstanceNotFoundException
     * @throws EntityNotFoundException
     */
    @Test
    public void testCheckValidateInstall()
        throws InvalidApplicationReleaseException, ApplicationInstanceNotFoundException, EntityNotFoundException {

        // given
        ApplicationReleaseDto applicationReleaseDto = new ApplicationReleaseDto("app", "version", null);

        when(applicationInstanceManager
                .load(any(String.class), any(String.class))).thenThrow(
                new EntityNotFoundException(ApplicationInstance.class, "", applicationReleaseDto));
        appInstanceResourceValidator.validateInstall("vdc", "environmentInstance", applicationReleaseDto);
        verify(applicationInstanceManager)
                .load(any(String.class), any(String.class));

    }

    /**
     * Test the exception in the installation of an application instance.
     *
     * @throws InvalidApplicationReleaseException
     * @throws ApplicationInstanceNotFoundException
     * @throws EntityNotFoundException
     */
    @Test(expected = InvalidApplicationReleaseException.class)
    public void testCheckValidateInstallError()
        throws InvalidApplicationReleaseException, ApplicationInstanceNotFoundException, EntityNotFoundException {

        // given
        ApplicationReleaseDto applicationReleaseDto = new ApplicationReleaseDto("app", "version", null);

        when(applicationInstanceManager
                .load(any(String.class), any(String.class))).thenReturn(new ApplicationInstance());
        appInstanceResourceValidator.validateInstall("vdc", "environmentInstance", applicationReleaseDto);
        verify(applicationInstanceManager)
                .load(any(String.class), any(String.class));

    }

    /**
     * Test the uninstall an application.
     *
     * @throws InvalidApplicationReleaseException
     * @throws ApplicationInstanceNotFoundException
     * @throws EntityNotFoundException
     * @throws com.telefonica.euro_iaas.commons.dao.InvalidEntityException
     */
    @Test
    public void testCheckValidateUnInstall()
            throws InvalidApplicationReleaseException, ApplicationInstanceNotFoundException, EntityNotFoundException,
            com.telefonica.euro_iaas.commons.dao.InvalidEntityException {

        // given

        when(applicationInstanceManager
                .load(any(String.class), any(String.class))).thenReturn(new ApplicationInstance());
        when(environmentInstanceManager
                .load(any(String.class), any(String.class))).thenReturn(new EnvironmentInstance());
        appInstanceResourceValidator.validateUnInstall("vdc", "environmentInstance", "applicationName");
        verify(applicationInstanceManager)
                .load(any(String.class), any(String.class));
        verify(environmentInstanceManager)
                .load(any(String.class), any(String.class));

    }


}
