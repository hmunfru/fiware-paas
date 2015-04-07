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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.manager.NetworkManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.bean.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;
import com.telefonica.euro_iaas.paasmanager.rest.validation.TierResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * default Tier implementation.
 * 
 * @author Henar Mu�oz
 */
@Path("/catalog/org/{org}/vdc/{vdc}/environment/{environment}/tier")
@Component
@Scope("request")
public class TierResourceImpl implements TierResource {

    @Autowired
    private TierManager tierManager;

    @Autowired
    private NetworkManager networkManager;

    @Autowired
    private EnvironmentManager environmentManager;

    @Autowired
    private SystemPropertiesProvider systemPropertiesProvider;

    @Autowired
    private TierResourceValidator tierResourceValidator;

    @Autowired
    private ProductReleaseDao productReleaseDao;

    private static Logger log = LoggerFactory.getLogger(TierResourceImpl.class);

    /**
     * Delete the Tier in DB.
     */
    public void delete(String org, String vdc, String envName, String tierName) throws APIException {
        ClaudiaData claudiaData = new ClaudiaData(org, vdc, envName);
        log.info("Deleting tier " + tierName + " from env " + envName + " vdc " + vdc);

        try {
            tierResourceValidator.validateDelete(vdc, envName, tierName);

            if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
                claudiaData.setUser(getCredentials());
            }

            Tier tier = tierManager.load(tierName, vdc, envName);

            Environment environment = environmentManager.load(envName, vdc);
            environment.deleteTier(tier);
            environmentManager.update(environment);
            tierManager.delete(claudiaData, tier);
        } catch (Exception e) {
            log.error("Error deleting the tier " + e.getMessage());
            throw new APIException(e);
        }

        // throw new WebApplicationException(e, 500);

    }

    /**
     * Retrieve all Tiers available created in the system.
     *
     * @param page
     *            for pagination is 0 based number(<i>nullable</i>)
     * @param pageSize
     *            for pagination, the number of items retrieved in a query (<i>nullable</i>)
     * @param orderBy
     *            the file to order the search (id by default <i>nullable</i>)
     * @param orderType
     *            defines if the order is ascending or descending (asc by default <i>nullable</i>)
     * @return the Tiers.
     */
    public List<TierDto> findAll(Integer page, Integer pageSize, String orderBy, String orderType, String vdc,
            String environment) {
        TierSearchCriteria criteria = new TierSearchCriteria();
        Environment env = null;
        try {
            env = environmentManager.load(environment, vdc);

        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(e, 404);
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
            throw new WebApplicationException(e, 404);
        }

    }

    /**
     * Get the user credentials.
     */
    private PaasManagerUser getCredentials() {
        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
            return (PaasManagerUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {
            return null;
        }
    }

    /**
     * Add the selected Environment in to the SDC's catalog. If the Environment already exists, then add the new
     * Release.
     */
    public void insert(String org, String vdc, String environmentName, TierDto tierDto) throws APIException {

        log.info("Insert tier " + tierDto.getName() + " from env " + environmentName);
        ClaudiaData claudiaData = new ClaudiaData(org, vdc, environmentName);
        claudiaData.setUser(getCredentials());

        try {
            tierResourceValidator.validateCreate(claudiaData, tierDto, vdc, environmentName);
        } catch (Exception ex) {
            throw new APIException(ex);
        }

        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
            claudiaData.setUser(getCredentials());
        }
        log.info("From tier dto " + tierDto + "  product " + tierDto.getProductReleaseDtos() + " nets "
                + tierDto.getNetworksDto());
        Tier tier = tierDto.fromDto(vdc, environmentName);
        log.info("to tier " + tier + "  product " + tier.getProductReleases() + " nets " + tier.getNetworks());

        try {
            Environment environment = environmentManager.load(environmentName, vdc);
            Tier newTier = tierManager.create(claudiaData, environmentName, tier);
            environment.addTier(newTier);
            environmentManager.update(environment);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            throw new APIException(ex);
        }
    }

    /**
     * Retrieve the selected Tier.
     */
    public TierDto load(String vdc, String envName, String name) throws APIException {
        try {
            Tier tier = tierManager.load(name, vdc, envName);

            return tier.toDto();

        } catch (EntityNotFoundException e) {
            throw new APIException(e);
        }
    }

    public void setEnvironmentManager(EnvironmentManager environmentManager) {
        this.environmentManager = environmentManager;
    }

    public void setProductReleaseDao(ProductReleaseDao productReleaseDao) {
        this.productReleaseDao = productReleaseDao;
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

    public void setTierResourceValidator(TierResourceValidator tierResourceValidator) {
        this.tierResourceValidator = tierResourceValidator;
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    /**
     * Update the Tier in DB.
     */
    public void update(String org, String vdc, String environmentName, String tierName, TierDto tierDto)
        throws APIException {
        log.info("Update tier " + tierName + " from env " + environmentName);
        ClaudiaData claudiaData = new ClaudiaData(org, vdc, environmentName);

        try {
            tierResourceValidator.validateUpdate(vdc, environmentName, tierName, tierDto);
            log.info("Validated tier " + tierDto.getName() + " from env " + environmentName);

            if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
                claudiaData.setUser(getCredentials());
            }

            Tier newtier = tierDto.fromDto(vdc, environmentName);

            Environment environment = environmentManager.load(environmentName, vdc);
            List<Tier> tiers = new ArrayList();
            for (Tier tier : environment.getTiers()) {
                tiers.add(tier);
            }
            environment.setTiers(null);
            environmentManager.update(environment);
            for (Tier tier : tiers) {
                if (tier.getName().equals(newtier.getName())) {
                    log.info("load tier " + tierDto.getName());
                    tier = tierManager.load(tierDto.getName(), vdc, environmentName);

                    tierManager.updateTier(claudiaData, tier, newtier);
                }
                environment.addTier(tier);
                environmentManager.update(environment);
            }

            log.info("update tier " + tierDto.getName());

            // environmentManager.update(environment);

        } catch (Exception e) {
            throw new APIException(e);
        }
    }

}
