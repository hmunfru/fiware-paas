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

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.ProductReleaseDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidSecurityGroupRequestException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.NetworkDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;
import com.telefonica.euro_iaas.paasmanager.rest.validation.TierResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Test the TierResource class.
 */
public class TierResourceTest extends TestCase {

    private TierResourceImpl tierResource;
    private TierManager tierManager;
    private SystemPropertiesProvider systemPropertiesProvider;
    private EnvironmentManager environmentManager;
    private TierResourceValidator tierResourceValidator;
    private static String vdc = "VDC";
    private static String org = "ORG";
    private static String env = "env";

    /**
     * Initialize the Unit Test.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        tierResource = new TierResourceImpl();
        tierManager = mock(TierManager.class);
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        environmentManager = mock(EnvironmentManager.class);
        tierResourceValidator = mock(TierResourceValidator.class);
        ProductReleaseDao productReleaseDao = mock(ProductReleaseDao.class);
        tierResource.setTierManager(tierManager);
        tierResource.setSystemPropertiesProvider(systemPropertiesProvider);
        tierResource.setEnvironmentManager(environmentManager);

        tierResource.setProductReleaseDao(productReleaseDao);
        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("FsIWARE");


        Mockito.doNothing().when(tierResourceValidator).validateCreate(any(ClaudiaData.class), any(TierDto.class),
                any(String.class), any(String.class));

        Mockito.doNothing().when(tierResourceValidator).validateDelete(any(String.class), any(String.class),
                any(String.class));

        Mockito.doNothing().when(tierResourceValidator).validateUpdate(any(String.class), any(String.class),
                any(String.class), any(TierDto.class));

        tierResource.setTierResourceValidator(tierResourceValidator);

        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(new ProductRelease("test", "0.1"));
        Tier tier = new Tier("tiername", new Integer(1), new Integer(1), new Integer(1), productReleases);
        tier.setImage("image");
        tier.setIcono("icono");
        tier.setFlavour("flavour");
        tier.setFloatingip("floatingip");
        tier.setKeypair("keypair");

        Environment environment = new Environment("name", "description", null);
        environment.addTier(tier);


        ProductRelease productRelease = new ProductRelease("test", "0.1");
        when(productReleaseDao.load(any(String.class))).thenReturn(productRelease);

        when(environmentManager.load(any(String.class), any(String.class))).thenReturn(environment);


    }

    /**
     * Test the operation to insert a new tier.
     * @throws APIException
     * @throws InvalidEntityException
     * @throws InvalidSecurityGroupRequestException
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */
    @Test
    public void testInsertTier()
            throws APIException, InvalidEntityException, InvalidSecurityGroupRequestException, InfrastructureException,
            EntityNotFoundException, AlreadyExistsEntityException {

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

        when(tierManager.create(any(ClaudiaData.class), any(String.class), any(Tier.class)))
                .thenReturn(tierDto.fromDto(vdc, "env"));

        tierResource.insert(org, vdc, env, tierDto);

    }

    /**
     * Test the operation to insert a new tier with network details.
     * @throws APIException
     * @throws InvalidEntityException
     * @throws InvalidSecurityGroupRequestException
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */
    @Test
    public void testInsertTierWithNetwork()
            throws APIException, InvalidEntityException, InvalidSecurityGroupRequestException, InfrastructureException,
            EntityNotFoundException, AlreadyExistsEntityException {

        List<ProductReleaseDto> productReleaseDto = new ArrayList<ProductReleaseDto>();
        productReleaseDto.add(new ProductReleaseDto("test", "0.1"));
        NetworkDto net = new NetworkDto("net");
        TierDto tierDto = new TierDto("tiername", new Integer(1), new Integer(1), new Integer(1), productReleaseDto);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        tierDto.setFloatingip("floatingip");
        tierDto.setKeypair("keypair");
        tierDto.addNetworkDto(net);

        List<TierDto> tiers = new ArrayList<TierDto>();
        tiers.add(tierDto);

        when(tierManager.create(any(ClaudiaData.class), any(String.class), any(Tier.class)))
                .thenReturn(tierDto.fromDto(vdc, "env"));

        tierResource.insert(org, vdc, env, tierDto);

    }

    /**
     * Test the insertion of a new Tier with no products information.
     * @throws Exception
     */
    @Test
    public void testTierNoProducts() throws Exception {

        TierDto tierDto = new TierDto("tiername", new Integer(1), new Integer(1), new Integer(1), null);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        tierDto.setFloatingip("floatingip");
        tierDto.setKeypair("keypair");

        List<TierDto> tiers = new ArrayList<TierDto>();
        tiers.add(tierDto);
        when(tierManager.create(any(ClaudiaData.class), any(String.class), any(Tier.class)))
                .thenReturn(tierDto.fromDto(vdc, "env"));

        tierResource.insert(org, vdc, env, tierDto);

    }

    /**
     * Test the launch of a exception.
     * @throws APIException
     * @throws InvalidEntityException
     * @throws InvalidSecurityGroupRequestException
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */
    @Test(expected = APIException.class)
    public void testTierException()
            throws APIException, InvalidEntityException, InvalidSecurityGroupRequestException, InfrastructureException,
            EntityNotFoundException, AlreadyExistsEntityException {

        TierDto tierDto = new TierDto("", new Integer(1), new Integer(1), new Integer(1), null);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        tierDto.setFloatingip("floatingip");
        tierDto.setKeypair("keypair");
        when(tierManager.create(any(ClaudiaData.class), any(String.class), any(Tier.class)))
                .thenReturn(tierDto.fromDto(vdc, "env"));

        tierResource.insert(org, vdc, env, tierDto);
    }

    /**
     * Test the operation to delete a Tier.
     * @throws APIException
     * @throws InvalidEntityException
     * @throws InvalidSecurityGroupRequestException
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */
    @Test
    public void testDeleteTier()
            throws APIException, InvalidEntityException, InvalidSecurityGroupRequestException, InfrastructureException,
            EntityNotFoundException, AlreadyExistsEntityException {

        List<ProductReleaseDto> productReleaseDto = new ArrayList<ProductReleaseDto>();
        productReleaseDto.add(new ProductReleaseDto("test", "0.1"));
        TierDto tierDto = new TierDto("tiername22", new Integer(1), new Integer(1), new Integer(1), productReleaseDto);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        tierDto.setFloatingip("floatingip");
        tierDto.setKeypair("keypair");
        when(tierManager.load(any(String.class), any(String.class), any(String.class)))
                .thenReturn(tierDto.fromDto(vdc, "env"));

        tierResource.delete(org, vdc, env, tierDto.getName());
    }

    /**
     * Test the operation to delete a Tier with network details.
     * @throws APIException
     * @throws InvalidEntityException
     * @throws InvalidSecurityGroupRequestException
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */
    @Test
    public void testDeleteTierWithNetwork()
            throws APIException, InvalidEntityException, InvalidSecurityGroupRequestException, InfrastructureException,
            EntityNotFoundException, AlreadyExistsEntityException {

        List<ProductReleaseDto> productReleaseDto = new ArrayList<ProductReleaseDto>();
        productReleaseDto.add(new ProductReleaseDto("test", "0.1"));
        NetworkDto net = new NetworkDto("net");
        TierDto tierDto = new TierDto("tiername22", new Integer(1), new Integer(1), new Integer(1), productReleaseDto);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        tierDto.setFloatingip("floatingip");
        tierDto.setKeypair("keypair");
        tierDto.addNetworkDto(net);
        when(tierManager.load(any(String.class), any(String.class), any(String.class)))
                .thenReturn(tierDto.fromDto(vdc, "env"));

        tierResource.delete(org, vdc, env, tierDto.getName());
    }

    /**
     * Test the operation to update a tier.
     * @throws APIException
     * @throws InvalidEntityException
     * @throws InvalidSecurityGroupRequestException
     * @throws InfrastructureException
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */
    @Test
    public void testUpdateTier()
            throws APIException, InvalidEntityException, InvalidSecurityGroupRequestException, InfrastructureException,
            EntityNotFoundException, AlreadyExistsEntityException {

        List<ProductReleaseDto> productReleaseDto = new ArrayList<ProductReleaseDto>();
        productReleaseDto.add(new ProductReleaseDto("test", "0.1"));
        TierDto tierDto = new TierDto("tiername22", new Integer(1), new Integer(1), new Integer(1), productReleaseDto);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        tierDto.setFloatingip("floatingip");
        tierDto.setKeypair("keypair");
        tierDto.setRegion("region1");
        when(tierManager.create(any(ClaudiaData.class), any(String.class), any(Tier.class)))
                .thenReturn(tierDto.fromDto(vdc, "env"));

        when(tierManager.load(any(String.class), any(String.class), any(String.class)))
                .thenReturn(tierDto.fromDto(vdc, "env"));

        tierResource.insert(org, vdc, env, tierDto);

        TierDto tierDto2 = new TierDto("tiername22", new Integer(1), new Integer(1), new Integer(1), productReleaseDto);
        tierDto2.setImage("image2");
        tierDto2.setIcono("icono");
        tierDto2.setFlavour("flavour");
        tierDto2.setFloatingip("floatingip");
        tierDto2.setKeypair("keypair");
        tierDto.setRegion("region2");
        Mockito.doNothing().when(tierManager).updateTier(any(ClaudiaData.class), any(Tier.class), any(Tier.class));
        tierResource.update(org, vdc, env, tierDto.getName(), tierDto2);
    }

    /**
     * Test the operation to update a Tier with different information.
     * @throws APIException
     * @throws EntityNotFoundException
     */
    @Test(expected = APIException.class)
    public void testUpdateTierDifferent() throws APIException, EntityNotFoundException {

        List<ProductReleaseDto> productReleaseDto = new ArrayList<ProductReleaseDto>();
        productReleaseDto.add(new ProductReleaseDto("test", "0.1"));
        TierDto tierDto = new TierDto("tiername3", new Integer(1), new Integer(1), new Integer(1), productReleaseDto);
        tierDto.setImage("image");
        tierDto.setIcono("icono");
        tierDto.setFlavour("flavour");
        tierDto.setFloatingip("floatingip");
        tierDto.setKeypair("keypair");

        when(tierManager.load(any(String.class), any(String.class), any(String.class)))
                .thenReturn(tierDto.fromDto(vdc, "env"));

        TierDto tierDto2 = new TierDto("tiername4", new Integer(1), new Integer(1), new Integer(1), productReleaseDto);
        tierResource.update(org, vdc, env, tierDto.getName(), tierDto2);
    }
}
