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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.rest.auth.OpenStackAuthenticationProvider;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;
import com.telefonica.euro_iaas.paasmanager.rest.validation.TierResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * default Tier implementation.
 * 
 * @author Henar Mu�oz
 */
@Path("/catalog/org/{org}/environment/{environment}/tier")
@Component
@Scope("request")
public class AbstractTierResourceImpl implements AbstractTierResource {

    private TierManager tierManager;

    private EnvironmentManager environmentManager;

    private SystemPropertiesProvider systemPropertiesProvider;

    private TierResourceValidator tierResourceValidator;

    private ProductReleaseDao productReleaseDao;

    private static Logger log = LoggerFactory.getLogger(AbstractTierResourceImpl.class);

    /**
     * Delete a specific Tier in a environment.
     * 
     * @param org
     *            The organization of the environment.
     * @param envName
     *            The name of the environment.
     * @param tierName
     *            The name of the tier to be deleted.
     * @throws APIException
     *             Exception launched when a problem happens.
     */
    public void delete(String org, String envName, String tierName) throws APIException {
        ClaudiaData claudiaData = new ClaudiaData(org, "", envName);
        log.info("Deleting tier " + tierName + " from env " + envName);

        try {
            tierResourceValidator.validateDelete("", envName, tierName);
            log.info("Tier validated correctly to be deleted");

            OpenStackAuthenticationProvider.addCredentialsToClaudiaData(claudiaData);

            Tier tier = tierManager.load(tierName, "", envName);

            Environment environment = environmentManager.load(envName, "");
            environment.deleteTier(tier);
            environmentManager.update(environment);
            tierManager.delete(claudiaData, tier);
        } catch (Exception e) {
            log.error("Error deleting the tier " + e.getMessage());
            throw new APIException(e);
        }

    }

    /**
     * Find all tiers associated to a specific environment.
     * 
     * @param page
     *            for pagination is 0 based number(<i>nullable</i>)
     * @param pageSize
     *            for pagination, the number of items retrieved in a query (<i>nullable</i>)
     * @param orderBy
     *            the file to order the search (id by default <i>nullable</i>)
     * @param orderType
     *            defines if the order is ascending or descending (asc by default <i>nullable</i>)
     * @param environment
     * @return
     * @throws APIException
     */
    public List<TierDto> findAll(Integer page, Integer pageSize, String orderBy, String orderType, String environment)
            throws APIException {
        TierSearchCriteria criteria = new TierSearchCriteria();
        Environment env = null;
        try {
            env = environmentManager.load(environment, "");

        } catch (EntityNotFoundException e) {
            throw new APIException(e, 404);
        }

        criteria.setEnvironment(env);

        if (page != null && pageSize != null) {
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
        }
        if (!StringUtils.isEmpty(orderBy)) {
            criteria.setOrderBy(orderBy);
        }
        if (!StringUtils.isEmpty(orderType)) {
            criteria.setOrderBy(orderType);
        }

        try {

            List<TierDto> tierDto = new ArrayList<TierDto>();
            List<Tier> tiers = tierManager.findByCriteria(criteria);

            for (Tier tier : tiers) {
                tierDto.add(tier.toDto());
            }
            return tierDto;

        } catch (EntityNotFoundException e) {
            throw new APIException(e);
        }

    }

    /**
     * Insert a new tier inside a specific environment.
     * 
     * @param org
     *            The organization where the abstract template belongs
     * @param environmentName
     *            The name of the environment where the abstract template belongs.
     * @param tierDto
     *            <ol>
     *            <li>The tierDto: contains the information about the tier</li>
     *            </ol>
     * @throws APIException
     */
    public void insert(String org, String environmentName, TierDto tierDto) throws APIException {

        log.info("Insert tier " + tierDto.getName() + " from env " + environmentName + " with product release "
                + tierDto.getProductReleaseDtos() + " with nets " + tierDto.getNetworksDto());
        ClaudiaData claudiaData = new ClaudiaData(org, "", environmentName);

        try {
            tierResourceValidator.validateCreateAbstract(tierDto, environmentName);

            OpenStackAuthenticationProvider.addCredentialsToClaudiaData(claudiaData);
            Tier tier = tierDto.fromDto("", environmentName);
            log.info("vdc " + claudiaData.getVdc());

            Environment environment = environmentManager.load(environmentName, "");
            Tier newTier = tierManager.create(claudiaData, environmentName, tier);
            environment.addTier(newTier);
            environmentManager.update(environment);
        } catch (Exception e) {
            throw new APIException(e);
        }
    }

    /**
     * Find a specific tier described by its name from the environment belongs.
     * 
     * @param org
     *            The organization which contains the environment.
     * @param envName
     *            The name id of the environment.
     * @param name
     *            The name of the tier to be found.
     * @return The detailed information about the tier.
     * @throws APIException
     *             Exception if the tier is not found.
     */
    public TierDto load(String org, String envName, String name) throws APIException {
        try {
            Tier tier = tierManager.load(name, "", envName);

            return tier.toDto();

        } catch (EntityNotFoundException e) {
            throw new APIException(e);
        }
    }

    /**
     * Update the description of a specific tier.
     * 
     * @param org
     *            The id of the organization which contains the environment.
     * @param environmentName
     *            The id of the environment which contains the tier.
     * @param tierName
     *            The tier id to be updated.
     * @param tierDto
     *            The updated information of the tier to be updated.
     * @throws APIException
     *             Esception if the tier cannot be found ot cannot be updated.
     */
    public void update(String org, String environmentName, String tierName, TierDto tierDto) throws APIException {
        log.info("Update tier " + tierName + " from env " + environmentName + "with product release "
                + tierDto.getProductReleaseDtos() + " with nets " + tierDto.getNetworksDto());
        ClaudiaData claudiaData = new ClaudiaData(org, "", environmentName);

        try {
            tierResourceValidator.validateUpdate("", environmentName, tierName, tierDto);
            log.info("Validated tier " + tierDto.getName() + " from env " + environmentName);

            OpenStackAuthenticationProvider.addCredentialsToClaudiaData(claudiaData);

            Tier newtier = tierDto.fromDto("", environmentName);

            Environment environment = environmentManager.load(environmentName, "");
            List<Tier> tiers = new ArrayList();
            for (Tier tier : environment.getTiers()) {
                tiers.add(tier);
            }
            environment.setTiers(null);
            environmentManager.update(environment);
            for (Tier tier : tiers) {
                if (tier.getName().equals(newtier.getName())) {

                    tier = tierManager.loadTierWithNetworks(tierDto.getName(), "", environmentName);
                    log.info("load tier " + tierDto.getName() + " with nets " + tier.getName() + " "
                            + tier.getNetworks());
                    tierManager.updateTier(claudiaData, tier, newtier);

                }
                environment.addTier(tier);
                environmentManager.update(environment);
            }
            log.info("updated tier " + tierDto.getName());

        } catch (Exception e) {
            throw new APIException(e);
        }
    }

    /**
     * @param systemPropertiesProvider
     *            the systemPropertiesProvider to set
     */
    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

    public void setTierManager(TierManager tierManager) {
        this.tierManager = tierManager;
    }

    public void setEnvironmentManager(EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    public void setTierResourceValidator(TierResourceValidator tierResourceValidator) {
        this.tierResourceValidator = tierResourceValidator;
    }

    public void setProductReleaseDao(ProductReleaseDao productReleaseDao) {
        this.productReleaseDao = productReleaseDao;
    }

}
