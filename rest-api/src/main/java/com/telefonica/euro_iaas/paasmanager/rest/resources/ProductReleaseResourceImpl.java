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

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.manager.ProductReleaseManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductReleaseDto;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * default Environment implementation.
 * 
 * @author Henar Mu�oz
 */
@Path("/catalog/org/{org}/vdc/{vdc}/environment/{environment}/tier/{tier}/productRelease")
@Component
@Scope("request")
public class ProductReleaseResourceImpl implements ProductReleaseResource {

    @Autowired
    private ProductReleaseManager productReleaseManager;
    @Autowired
    private TierManager tierManager;

    private SystemPropertiesProvider systemPropertiesProvider;

    /**
     * Delete a product release resource.
     * @param org   The organization id
     * @param vdc   The vdc id.
     * @param envName   The name of th environment.
     * @param tierName  The name of the tier.
     * @param productReleaseName    The name of the product to be deleted.
     * @throws APIException Any exception launched during the deleting process.
     */
    public void delete(String org, String vdc, String envName, String tierName, String productReleaseName)
        throws APIException {
        ClaudiaData claudiaData = new ClaudiaData(org, vdc, envName);

        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
            claudiaData.setUser(getCredentials());
        }

        try {

            ProductRelease productRelease = productReleaseManager.load(productReleaseName, claudiaData);

            Tier tier = tierManager.load(tierName, vdc, envName);
            tier.removeProductRelease(productRelease);
            tierManager.update(tier);

        } catch (Exception e) {
            throw new APIException(e);
        }

    }

    /**
     * Insert a new product release resource.
     * @param org   The organization id
     * @param vdc   The vdc id.
     * @param environmentName   The environment name.
     * @param tierName  The name of the tier.
     * @param productReleaseDto The information of the new product release resource.
     */
    public void insert(String org, String vdc, String environmentName, String tierName,
            ProductReleaseDto productReleaseDto) {

        ClaudiaData claudiaData = new ClaudiaData(org, vdc, environmentName);
        ProductRelease productRelease = null;

        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
            claudiaData.setUser(getCredentials());
        }

        try {
            productRelease = productReleaseManager.load(
                    productReleaseDto.getProductName() + "-" + productReleaseDto.getVersion(), claudiaData);
        } catch (EntityNotFoundException e1) {

            throw new WebApplicationException(e1, 500);
        }

        try {

            Tier tier = tierManager.load(tierName, vdc, environmentName);
            tier.addProductRelease(productRelease);
            //tierManager.addSecurityGroupToProductRelease(claudiaData, tier, productRelease);
            tierManager.update(tier);

        } catch (InvalidEntityException e) {
            throw new WebApplicationException(e, 500);
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(e, 500);
        } catch (Exception e) {
            throw new WebApplicationException(e, 500);
        }

    }

    /**
     * Find a specific product release resource.
     * @param org   The organization id
     * @param vdc   The vdc id.
     * @param environment   The environment id.
     * @param tier  The tier id.
     * @param name  The name of the product release resource.
     * @return  The detail of the product release resource.
     * @throws APIException Any exception launched during the process.
     */
    public ProductReleaseDto load(String org, String vdc, String environment, String tier, String name)
        throws APIException {
        try {
            ClaudiaData claudiaData = new ClaudiaData(org, vdc, environment);

            if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
                claudiaData.setUser(getCredentials());
            }
            ProductRelease productRelease = productReleaseManager.load(name, claudiaData);

            return convertToDto(productRelease);

        } catch (EntityNotFoundException e) {
            throw new APIException(e);
        }
    }

    private ProductReleaseDto convertToDto(ProductRelease productRelease) {

        ProductReleaseDto productReleaseDto = new ProductReleaseDto();

        if (productRelease.getName() != null) {
            productReleaseDto.setProductName(productRelease.getName());
        }

        if (productRelease.getVersion() != null) {
            productRelease.setVersion(productRelease.getVersion());
        }

        return productReleaseDto;
    }

    /**
     * @param systemPropertiesProvider
     *            the systemPropertiesProvider to set
     */
    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

    /**
     * Get user credentials.
     * @return  Credential.
     */
    public PaasManagerUser getCredentials() {
        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
            return (PaasManagerUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {
            return null;
        }
    }

    /**
     * Find all product release resource associated to a tier.
     * @param page  The page of data to show.
     * @param pageSize  The size of data to be returned.
     * @param orderBy   The order by parameter.
     * @param orderType The type of order of data o returned.
     * @param environment   Environment which contains the tier.
     * @param tier  Tier which contains the product to be listed.
     * @return  The complete list of product release resource.
     */
    public List<ProductReleaseDto> findAll(Integer page, Integer pageSize, String orderBy, String orderType,
            String environment, String tier) {
        // TODO Auto-generated method stub
        return null;
    }

}
