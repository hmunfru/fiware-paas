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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.bean.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentDto;
import com.telefonica.euro_iaas.paasmanager.rest.auth.OpenStackAuthenticationProvider;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;
import com.telefonica.euro_iaas.paasmanager.rest.validation.EnvironmentResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * default Environment implementation.
 * 
 * @author Henar Mu�oz
 */
@Path("/catalog/org/{org}/vdc/{vdc}/environment")
@Component
@Scope("request")
public class EnvironmentResourceImpl implements EnvironmentResource {

    public static final int ERROR_NOT_FOUND = 404;
    public static final int ERROR_REQUEST = 500;

    @Autowired
    private EnvironmentManager environmentManager;

    @Autowired
    private SystemPropertiesProvider systemPropertiesProvider;

    @Autowired
    private EnvironmentResourceValidator environmentResourceValidator;

    private static Logger log = LoggerFactory.getLogger(EnvironmentResourceImpl.class);

    /**
     * Delete an specific environment instance.
     * 
     * @param org
     *            The organization which contains the environment instance.
     * @param vdc
     *            The vdc which contains the environment instance.
     * @param envName
     *            The name of the environment instance.
     * @throws APIException
     *             Any exception that throws during the operation.
     */
    public void delete(String org, String vdc, String envName) throws APIException {
        ClaudiaData claudiaData = new ClaudiaData(org, vdc, envName);
        try {
            environmentResourceValidator.validateDelete(envName, vdc);

            OpenStackAuthenticationProvider.addCredentialsToClaudiaData(claudiaData);

            List<Environment> list = environmentManager.findByOrgAndVdcAndName(org, vdc, envName);
            environmentManager.destroy(claudiaData, list.get(0));
        } catch (EntityNotFoundException e) {
            throw new APIException(e);
        } catch (Exception e) {
            throw new APIException(new InvalidEntityException(e.getMessage()));
        }

    }

    /**
     * Find all environment resource.
     * 
     * @param org
     *            The organization which contains the environment instance.
     * @param vdc
     *            The vdc which contains the environment instance.
     * @param page
     *            for pagination is 0 based number(<i>nullable</i>)
     * @param pageSize
     *            for pagination, the number of items retrieved in a query (<i>nullable</i>)
     * @param orderBy
     *            the file to order the search (id by default <i>nullable</i>)
     * @param orderType
     *            defines if the order is ascending or descending (asc by default <i>nullable</i>)
     * @return The list of all environment.
     */
    public List<EnvironmentDto> findAll(String org, String vdc, Integer page, Integer pageSize, String orderBy,
            String orderType) {

        List<Environment> environments = environmentManager.findByOrgAndVdc(org, vdc);

        // Solve the tier-environment duplicity appeared at database due to hibernate problems
        // List<Environment> envs = filterEqualTiers(env);

        List<EnvironmentDto> envsDto = new ArrayList<EnvironmentDto>();

        for (Environment environment : environments) {
            envsDto.add(environment.toDto());
        }

        return envsDto;
    }

    /**
     * Get the credentials of a user.
     * 
     * @return The credentials.
     */
    public PaasManagerUser getCredentials() {
        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
            return (PaasManagerUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {
            return null;
        }

    }

    /**
     * Insert a new environment resource.
     * 
     * @param org
     *            The organization which contains the environment instance.
     * @param vdc
     *            The vdc which contains the environment instance.
     * @param environmentDto
     *            The new environment resource.
     * @throws APIException
     */
    public void insert(String org, String vdc, EnvironmentDto environmentDto) throws APIException {
        ClaudiaData claudiaData = new ClaudiaData(org, vdc, environmentDto.getName());

        log.info("Create an environment " + environmentDto.getName() + " " + environmentDto.getDescription() + " "
                + environmentDto.getVdc() + " " + environmentDto.getOrg() + " " + environmentDto.getTierDtos());

        try {
            OpenStackAuthenticationProvider.addCredentialsToClaudiaData(claudiaData);
            environmentResourceValidator.validateCreate(claudiaData, environmentDto, vdc);

            // try {
            environmentManager.create(claudiaData, environmentDto.fromDto(org, vdc));
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new APIException(e);
        }
    }

    /**
     * Find a specific environment resource.
     * 
     * @param org
     *            The organization which contains the environment instance.
     * @param vdc
     *            The vdc which contains the environment instance.
     * @param name
     *            The name of the environment resource.
     * @return The details information of the environment resource.
     * @throws APIException
     *             Any exception happened during the process.
     */
    public EnvironmentDto load(String org, String vdc, String name) throws APIException {

        List<Environment> env = environmentManager.findByOrgAndVdcAndName(org, vdc, name);

        // Solve the tier-environment duplicity appeared at database due to hibernate problems
        // List<Environment> envs = filterEqualTiers(env);

        if (env == null || env.size() == 0) {
            throw new WebApplicationException(new EntityNotFoundException(Environment.class, "Environment " + name
                    + " not found", ""), ERROR_NOT_FOUND);
        } else {
            EnvironmentDto envDto = env.get(0).toDto();
            // EnvironmentDto envDto = env.get(0).toDto();
            return envDto;
        }

    }

    public void setEnvironmentManager(EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    public void setEnvironmentResourceValidator(EnvironmentResourceValidator environmentResourceValidator) {
        this.environmentResourceValidator = environmentResourceValidator;
    }

    /**
     * @param systemPropertiesProvider
     *            the systemPropertiesProvider to set
     */
    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

}
