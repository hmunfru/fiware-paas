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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.AlreadyExistEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentInstanceException;
import com.telefonica.euro_iaas.paasmanager.exception.QuotaExceededException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.Metadata;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Test the TierResourceValidatorImpl.
 */
public class TierResourceValidatorImplTest {

    private TierResourceValidatorImpl tierResourceValidator;
    private SystemPropertiesProvider systemPropertiesProvider;
    private ResourceValidator resourceValidator;
    private TierManager tierManager;
    private ProductValidator productValidator;

    /**
     * Initialize the Unit Test.
     * 
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     */
    @Before
    public void setUp() throws EntityNotFoundException, InvalidEntityException {
        tierResourceValidator = new TierResourceValidatorImpl();
        tierManager = mock(TierManager.class);
        EnvironmentManager environmentManager = mock(EnvironmentManager.class);
        EnvironmentInstanceManager environmentInstanceManager = mock(EnvironmentInstanceManager.class);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        tierResourceValidator.setTierManager(tierManager);
        tierResourceValidator.setEnvironmentInstanceManager(environmentInstanceManager);
        tierResourceValidator.setEnvironmentManager(environmentManager);
        productValidator = mock(ProductValidator.class);
        tierResourceValidator.setProductValidator(productValidator);

        resourceValidator = mock(ResourceValidator.class);
        tierResourceValidator.setResourceValidator(resourceValidator);

        Mockito.doNothing().when(resourceValidator).validateName(anyString());
        Mockito.doNothing().when(resourceValidator).validateDescription(anyString());
        Environment env = new Environment();
        when(environmentManager.load(anyString(), anyString())).thenReturn(env);

        List<EnvironmentInstance> envs = new ArrayList<EnvironmentInstance>();
        when(environmentInstanceManager.findByCriteria(any(EnvironmentInstanceSearchCriteria.class))).thenReturn(envs);
    }

    /**
     * Test that it returns true when all dependencies for a product exist.
     */
    @Test
    public void shouldReturnTrueWhenAllDependenciesForProductExists() {
        // given

        Map<String, String> productsNameMap = new HashMap<String, String>();
        productsNameMap.put("tomcat-6", "");
        productsNameMap.put("mysql", "");
        productsNameMap.put("mongos", "");
        productsNameMap.put("mongoshard", "");
        productsNameMap.put("mongoconfig", "");
        List<String> contextBrokerDependencies = new ArrayList<String>(2);
        contextBrokerDependencies.add("mongos");
        contextBrokerDependencies.add("mongoshard");
        contextBrokerDependencies.add("mongoconfig");

        // when
        boolean result = tierResourceValidator.checkTierProductsInDependencyList(contextBrokerDependencies,
                productsNameMap);
        // then
        assertTrue(result);
    }

    /**
     * Test that it returns false when it does not exist a dependency for a
     * product.
     */
    @Test
    public void shouldReturnFalseWhenNotExistADependencyForAproduct() {
        // given
        TierResourceValidatorImpl tierResourceValidator = new TierResourceValidatorImpl();

        Map<String, String> productsNameMap = new HashMap<String, String>();
        productsNameMap.put("tomcat-6", "");
        productsNameMap.put("mysql", "");
        List<String> contextBrokerDependencies = new ArrayList<String>(2);
        contextBrokerDependencies.add("mongos");

        // when
        boolean result = tierResourceValidator.checkTierProductsInDependencyList(contextBrokerDependencies,
                productsNameMap);
        // then
        assertFalse(result);
    }

    /**
     * Test that it returns false when the list of dependencies is empty due to
     * a product need another product.
     */
    @Test
    public void shouldReturnFalseWhenDependenciesListIsEmptyBecauseAProductNeedAnotherProduct() {
        // given
        TierResourceValidatorImpl tierResourceValidator = new TierResourceValidatorImpl();

        Map<String, String> productsNameMap = new HashMap<String, String>();
        List<String> contextBrokerDependencies = new ArrayList<String>(2);
        contextBrokerDependencies.add("mongos");

        // when
        boolean result = tierResourceValidator.checkTierProductsInDependencyList(contextBrokerDependencies,
                productsNameMap);
        // then
        assertFalse(result);
    }

    /**
     * Test that it returns true when the list of dependencies is empty and
     * products have no dependencies.
     */
    @Test
    public void shouldReturnTrueWhenDependenciesListIsEmptyAndProductHasntDependencies() {
        // given
        TierResourceValidatorImpl tierResourceValidator = new TierResourceValidatorImpl();

        Map<String, String> productsNameMap = new HashMap<String, String>();
        List<String> contextBrokerDependencies = new ArrayList<String>(2);

        // when
        boolean result = tierResourceValidator.checkTierProductsInDependencyList(contextBrokerDependencies,
                productsNameMap);
        // then
        assertTrue(result);
    }

    /**
     * Test the creation f a list with dependencies from metadata products.
     */
    @Test
    public void shouldCreateAListWithDependenciesFromMetadataProducts() {
        // given
        TierResourceValidatorImpl tierResourceValidator = new TierResourceValidatorImpl();
        List<Tier> tiers = new ArrayList<Tier>();
        Tier tier1 = new Tier();
        tiers.add(tier1);
        List<ProductRelease> products = new ArrayList<ProductRelease>(2);
        tier1.setProductReleases(products);
        ProductRelease product1 = new ProductRelease();
        product1.setName("mongo");
        product1.setVersion("1");
        ProductRelease product2 = new ProductRelease();
        product2.setName("mongoshared");
        product2.setVersion("1");
        products.add(product1);
        products.add(product2);

        Tier tier2 = new Tier();
        tiers.add(tier2);
        List<ProductRelease> products2 = new ArrayList<ProductRelease>(2);
        tier2.setProductReleases(products2);
        ProductRelease product3 = new ProductRelease();
        product3.setName("contexbroker");
        product3.setVersion("1.0");
        Set<Metadata> metadatas = new HashSet<Metadata>(2);
        product3.setMetadatas(metadatas);
        products2.add(product3);
        Metadata metadataDep = new Metadata();
        metadatas.add(metadataDep);
        metadataDep.setKey("dep");
        metadataDep.setValue("mongo-1");

        // when
        List<String> dependenciesList = tierResourceValidator.createDependenciesForTiers(tiers);

        // then
        assertNotNull(dependenciesList);
        assertEquals(1, dependenciesList.size());
    }

    /**
     * Test the creation of a list of products.
     */
    @Test
    public void shouldCreateAListOfProducts() {
        // given
        TierResourceValidatorImpl tierResourceValidator = new TierResourceValidatorImpl();
        List<Tier> tiers = new ArrayList<Tier>();
        Tier tier1 = new Tier();
        tiers.add(tier1);
        List<ProductRelease> products = new ArrayList<ProductRelease>(2);
        tier1.setProductReleases(products);
        ProductRelease product1 = new ProductRelease();
        product1.setName("mongo");
        product1.setVersion("1");
        ProductRelease product2 = new ProductRelease();
        product2.setName("mongoshared");
        product2.setVersion("1");
        products.add(product1);
        products.add(product2);

        Tier tier2 = new Tier();
        tiers.add(tier2);
        List<ProductRelease> products2 = new ArrayList<ProductRelease>(2);
        tier2.setProductReleases(products2);
        ProductRelease product3 = new ProductRelease();
        product3.setName("contexbroker");
        product3.setVersion("1.0");
        Set<Metadata> metadatas = new HashSet<Metadata>(2);
        product3.setMetadatas(metadatas);
        products2.add(product3);
        Metadata metadataDep = new Metadata();
        metadatas.add(metadataDep);
        metadataDep.setKey("dep");
        metadataDep.setValue("mongo-1");

        // when
        Map<String, String> productNameList = tierResourceValidator.createProductList(tiers);

        // then
        assertNotNull(productNameList);
        assertEquals(3, productNameList.size());

    }

    /**
     * Test the dependencies of a tier.
     * 
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     */
    @Test
    public void shouldValidateATierDependencies() throws EntityNotFoundException, InvalidEntityException {
        // given
        TierResourceValidatorImpl tierResourceValidator = new TierResourceValidatorImpl();
        Set<TierDto> tierDTOlist = new HashSet<TierDto>(2);
        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTOlist.add(tierDTO);
        String vdc = "vdc";
        String environmentName = "env";
        TierManager tierManager = mock(TierManager.class);
        tierResourceValidator.setTierManager(tierManager);
        Tier tier = new Tier();

        // when
        when(tierManager.loadTierWithProductReleaseAndMetadata(tierDTO.getName(), environmentName, vdc)).thenReturn(
                tier);
        tierResourceValidator.validateTiersDependencies(environmentName, vdc, tierDTOlist);
        // then
    }

    /**
     * Test the launch of a exception in the process to validate a tier with
     * dependencies that there is not exist.
     * 
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     */
    @Test(expected = InvalidEntityException.class)
    public void shouldThrowExceptionInValidateATierWithDependencyThatNotExist() throws EntityNotFoundException,
            InvalidEntityException {

        // given
        TierResourceValidatorImpl tierResourceValidator = new TierResourceValidatorImpl();
        Set<TierDto> tierDTOlist = new HashSet<TierDto>(2);
        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTOlist.add(tierDTO);
        String vdc = "vdc";
        String environmentName = "env";
        TierManager tierManager = mock(TierManager.class);
        tierResourceValidator.setTierManager(tierManager);
        Tier tier = new Tier();
        List<ProductRelease> productReleaseList = new ArrayList<ProductRelease>(2);
        ProductRelease productRelease = new ProductRelease();
        productRelease.setName("tomcat");
        Set<Metadata> metadatas = new HashSet<Metadata>(2);
        Metadata metadata = new Metadata();
        metadata.setKey("dep");
        metadata.setValue("mysql");
        metadatas.add(metadata);
        productRelease.setMetadatas(metadatas);
        productReleaseList.add(productRelease);
        tier.setProductReleases(productReleaseList);

        // when
        when(tierManager.loadTierWithProductReleaseAndMetadata(tierDTO.getName(), environmentName, vdc)).thenReturn(
                tier);
        tierResourceValidator.validateTiersDependencies(environmentName, vdc, tierDTOlist);
        // then
    }

    /**
     * Test that an exception is launched when a tier has no name.
     * 
     * @throws AlreadyExistEntityException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     * @throws QuotaExceededException
     * @throws EntityNotFoundException
     * @throws InvalidEnvironmentInstanceException
     */
    @Test(expected = InvalidEntityException.class)
    public void shouldValidateEmptyNameTier() throws AlreadyExistEntityException, InvalidEntityException,
            InfrastructureException, QuotaExceededException, EntityNotFoundException,
            InvalidEnvironmentInstanceException {

        // given
        ClaudiaData claudiaData = mock(ClaudiaData.class);
        TierDto tierDto = new TierDto();
        tierDto.setName("");
        when(tierManager.load(anyString(), anyString(), anyString())).thenThrow(
                new EntityNotFoundException(Tier.class, "tier", tierDto.getName()));

        tierResourceValidator.validateCreate(claudiaData, tierDto, "vdc", "envName");

    }

    /**
     * Test that an exception is launched when a strange character is found in
     * the environment name.
     * 
     * @throws AlreadyExistEntityException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     * @throws QuotaExceededException
     * @throws EntityNotFoundException
     * @throws InvalidEnvironmentInstanceException
     */
    @Test(expected = InvalidEntityException.class)
    public void shouldValidateStrangeCharacteresEnvironment() throws AlreadyExistEntityException,
            InvalidEntityException, InfrastructureException, QuotaExceededException, EntityNotFoundException,
            InvalidEnvironmentInstanceException {
        // given

        TierDto tierDto = new TierDto();
        tierDto.setName("name.name");
        when(tierManager.load(anyString(), anyString(), anyString())).thenThrow(
                new EntityNotFoundException(Tier.class, "tier", tierDto.getName()));

        ClaudiaData claudiaData = mock(ClaudiaData.class);
        tierResourceValidator.validateCreate(claudiaData, tierDto, "vdc", "envName");
    }

    /**
     * Test that an exception is launched when a tier name is too long.
     * 
     * @throws AlreadyExistEntityException
     * @throws InfrastructureException
     * @throws QuotaExceededException
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     * @throws InvalidEnvironmentInstanceException
     */
    @Test(expected = InvalidEntityException.class)
    public void shouldValidateNameTooLong() throws AlreadyExistEntityException, InfrastructureException,
            QuotaExceededException, InvalidEntityException, EntityNotFoundException,
            InvalidEnvironmentInstanceException {
        // given

        TierDto tierDto = new TierDto();
        tierDto.setName("aaaaaaaaaaaaaaaaaaaaaaaahhhhhhhhhhhhhhhhhhhhhaaaaaaaaaaa");
        when(tierManager.load(anyString(), anyString(), anyString())).thenThrow(
                new EntityNotFoundException(Tier.class, "tier", tierDto.getName()));

        ClaudiaData claudiaData = mock(ClaudiaData.class);
        tierResourceValidator.validateCreate(claudiaData, tierDto, "vdc", "envName");

    }

    /**
     * Test that an abstract tier is created properly.
     * 
     * @throws EntityNotFoundException
     * @throws InvalidEntityException
     * @throws AlreadyExistEntityException
     * @throws InvalidEnvironmentInstanceException
     */
    @Test
    public void shouldValidateAbstractTier() throws EntityNotFoundException, InvalidEntityException,
            AlreadyExistEntityException, InvalidEnvironmentInstanceException {

        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        when(tierManager.load(anyString(), anyString(), anyString())).thenThrow(
                new EntityNotFoundException(Tier.class, "tier", tierDTO.getName()));

        tierResourceValidator.validateCreateAbstract(tierDTO, "en");

    }

    /**
     * Test the operation to update a tier.
     * 
     * @throws AlreadyExistEntityException
     * @throws InfrastructureException
     * @throws QuotaExceededException
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     */
    @Test
    public void shouldValidateUpdateTier() throws AlreadyExistEntityException, InfrastructureException,
            QuotaExceededException, InvalidEntityException, EntityNotFoundException {
        // given

        TierDto tierDTO = new TierDto();
        tierDTO.setName("aaaa");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        when(tierManager.load(anyString(), anyString(), anyString())).thenReturn(tierDTO.fromDto("vdc", "env"));

        tierResourceValidator.validateUpdate("vdc", "envName", tierDTO.getName(), tierDTO);

    }

    /**
     * Test that an exception is launched when ti tries to update a error tier.
     * 
     * @throws AlreadyExistEntityException
     * @throws InfrastructureException
     * @throws QuotaExceededException
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     */
    @Test(expected = InvalidEntityException.class)
    public void shouldValidateUpdateTierError() throws AlreadyExistEntityException, InfrastructureException,
            QuotaExceededException, InvalidEntityException, EntityNotFoundException {
        // given

        TierDto tierDTO = new TierDto();
        tierDTO.setName("aaaa");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        when(tierManager.load(anyString(), anyString(), anyString())).thenReturn(tierDTO.fromDto("vdc", "env"));

        tierResourceValidator.validateUpdate("vdc", "envName", "ddd", tierDTO);

    }

    /**
     * Test the operation to delete a tier.
     * 
     * @throws InvalidEntityException
     * @throws EntityNotFoundException
     */
    @Test
    public void shouldValidateDeleteTier() throws InvalidEntityException, EntityNotFoundException {
        // given

        TierDto tierDTO = new TierDto();
        tierDTO.setName("aaaa");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        when(tierManager.load(anyString(), anyString(), anyString())).thenReturn(tierDTO.fromDto("vdc", "env"));

        tierResourceValidator.validateDelete("vdc", "envName", "tierName");

    }

}
