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

import java.util.Set;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.AlreadyExistEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentInstanceException;
import com.telefonica.euro_iaas.paasmanager.exception.QuotaExceededException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;

/**
 * @author jesus.movilla
 */
public interface TierResourceValidator {

    /**
     * Validate the request to create a Tier resource.
     *
     * @param claudiaData
     *            The information related to organization, vdc and service
     *            together with the user.
     * @param environmentDto
     *            The information of the environment.
     * @param vdc
     *            The vdc details.
     * @param environmentName
     *            The environment name.
     * @throws InvalidEntityException
     * @throws AlreadyExistEntityException
     * @throws InfrastructureException
     * @throws QuotaExceededException
     * @throws InvalidEnvironmentInstanceException
     */
    void validateCreate(ClaudiaData claudiaData, TierDto environmentDto, String vdc, String environmentName)
            throws InvalidEntityException, AlreadyExistEntityException, InfrastructureException,
            QuotaExceededException, InvalidEnvironmentInstanceException;

    /**
     * Validate the request to create an abstract.
     * 
     * @param tierDto
     *            The tier data.
     * @param environmentName
     *            The name of the environment.
     * @throws InvalidEntityException
     * @throws AlreadyExistEntityException
     * @throws InvalidEnvironmentInstanceException
     */
    void validateCreateAbstract(TierDto tierDto, String environmentName) throws InvalidEntityException,
            AlreadyExistEntityException, InvalidEnvironmentInstanceException;

    /**
     * Validate the update of the Tier resource.
     * 
     * @param vdc
     *            The vdc details.
     * @param environmentName
     *            The environment name.
     * @param tierName
     *            The Tier name.
     * @param environmentDto
     *            The information of the environment.
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     */
    void validateUpdate(String vdc, String environmentName, String tierName, TierDto environmentDto)
            throws InvalidEntityException, EntityNotFoundException;

    /**
     * Validate the delete of the Tier resource.
     * 
     * @param vdc
     *            The vdc details.
     * @param environmentName
     *            The environment name.
     * @param tierName
     *            The Tier name.
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     */
    void validateDelete(String vdc, String environmentName, String tierName) throws InvalidEntityException,
            EntityNotFoundException;

    /**
     * Validate the Tiers software dependencies.
     * 
     * @param environmentName
     *            The environment name.
     * @param vdc
     *            The vdc details.
     * @param tierDtoList
     *            Set of tiers.
     * @throws InvalidEntityException
     */
    void validateTiersDependencies(String environmentName, String vdc, Set<TierDto> tierDtoList)
            throws InvalidEntityException;
}
