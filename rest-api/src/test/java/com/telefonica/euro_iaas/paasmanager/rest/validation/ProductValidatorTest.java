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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentInstanceException;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

public class ProductValidatorTest {

    private ProductValidatorImpl productValidator;
    private SystemPropertiesProvider systemPropertiesProvider;

    @Before
    public void setup() {
        productValidator=new ProductValidatorImpl();
        
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.AVAILABLE_ATTRIBUTE_TYPES)).thenReturn(
                "Plain|IP|IPALL");

        productValidator.setSystemPropertiesProvider(systemPropertiesProvider);

    }

    @Test
    public void testValidateAttributes() throws InvalidEnvironmentInstanceException {
        

        Attribute attribute = new Attribute("ssl_port", "8443", "The ssl listen port", "Plain");
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(attribute);

        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        List<ProductReleaseDto> list = new ArrayList<ProductReleaseDto>();

        ProductReleaseDto pdto = new ProductReleaseDto("a", "b", "1.0");
        pdto.setPrivateAttributes(attributes);
        List<ProductReleaseDto> lpdto = new ArrayList<ProductReleaseDto>();
        lpdto.add(pdto);

        tierDTO.setProductReleaseDtos(lpdto);

        productValidator.validateAttributes(tierDTO);

        verify(systemPropertiesProvider, times(1)).getProperty(SystemPropertiesProvider.AVAILABLE_ATTRIBUTE_TYPES);
    }
    
    @Test(expected=InvalidEnvironmentInstanceException.class)
    public void testValidateAttributesBadValue() throws InvalidEnvironmentInstanceException {
        

        Attribute attribute = new Attribute("ssl_port", "8443", "The ssl listen port", "IP");
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(attribute);

        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        List<ProductReleaseDto> list = new ArrayList<ProductReleaseDto>();

        ProductReleaseDto pdto = new ProductReleaseDto("a", "b", "1.0");
        pdto.setPrivateAttributes(attributes);
        List<ProductReleaseDto> lpdto = new ArrayList<ProductReleaseDto>();
        lpdto.add(pdto);

        tierDTO.setProductReleaseDtos(lpdto);

        productValidator.validateAttributes(tierDTO);

        verify(systemPropertiesProvider, times(1)).getProperty(SystemPropertiesProvider.AVAILABLE_ATTRIBUTE_TYPES);
    }
    
    @Test
    public void testValidateAttributesOKValue() throws InvalidEnvironmentInstanceException {
        

        Attribute attribute = new Attribute("ssl_port", "IP(tierName)", "The ssl listen port", "IP");
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(attribute);

        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        List<ProductReleaseDto> list = new ArrayList<ProductReleaseDto>();

        ProductReleaseDto pdto = new ProductReleaseDto("a", "b", "1.0");
        pdto.setPrivateAttributes(attributes);
        List<ProductReleaseDto> lpdto = new ArrayList<ProductReleaseDto>();
        lpdto.add(pdto);

        tierDTO.setProductReleaseDtos(lpdto);

        productValidator.validateAttributes(tierDTO);

        verify(systemPropertiesProvider, times(1)).getProperty(SystemPropertiesProvider.AVAILABLE_ATTRIBUTE_TYPES);
    }
    
    @Test(expected=InvalidEnvironmentInstanceException.class)
    public void testValidateAttributesBadValue2() throws InvalidEnvironmentInstanceException {
        

        Attribute attribute = new Attribute("ssl_port", "8443", "The ssl listen port", "IPALL");
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(attribute);

        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        List<ProductReleaseDto> list = new ArrayList<ProductReleaseDto>();

        ProductReleaseDto pdto = new ProductReleaseDto("a", "b", "1.0");
        pdto.setPrivateAttributes(attributes);
        List<ProductReleaseDto> lpdto = new ArrayList<ProductReleaseDto>();
        lpdto.add(pdto);

        tierDTO.setProductReleaseDtos(lpdto);

        productValidator.validateAttributes(tierDTO);

        verify(systemPropertiesProvider, times(1)).getProperty(SystemPropertiesProvider.AVAILABLE_ATTRIBUTE_TYPES);
    }
    
    @Test
    public void testValidateAttributesOKValue2() throws InvalidEnvironmentInstanceException {
        

        Attribute attribute = new Attribute("ssl_port", "IPALL(tierName)", "The ssl listen port", "IPALL");
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(attribute);

        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        List<ProductReleaseDto> list = new ArrayList<ProductReleaseDto>();

        ProductReleaseDto pdto = new ProductReleaseDto("a", "b", "1.0");
        pdto.setPrivateAttributes(attributes);
        List<ProductReleaseDto> lpdto = new ArrayList<ProductReleaseDto>();
        lpdto.add(pdto);

        tierDTO.setProductReleaseDtos(lpdto);

        productValidator.validateAttributes(tierDTO);

        verify(systemPropertiesProvider, times(1)).getProperty(SystemPropertiesProvider.AVAILABLE_ATTRIBUTE_TYPES);
    }
    
    @Test
    public void testValidateAttributesDefaultType() throws InvalidEnvironmentInstanceException {
        

        Attribute attribute = new Attribute("ssl_port", "8443", "The ssl listen port");
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(attribute);

        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        List<ProductReleaseDto> list = new ArrayList<ProductReleaseDto>();

        ProductReleaseDto pdto = new ProductReleaseDto("a", "b", "1.0");
        pdto.setPrivateAttributes(attributes);
        List<ProductReleaseDto> lpdto = new ArrayList<ProductReleaseDto>();
        lpdto.add(pdto);

        tierDTO.setProductReleaseDtos(lpdto);

        productValidator.validateAttributes(tierDTO);

        verify(systemPropertiesProvider, times(1)).getProperty(SystemPropertiesProvider.AVAILABLE_ATTRIBUTE_TYPES);
    }
    
    @Test(expected=InvalidEnvironmentInstanceException.class)
    public void testValidateAttributesBadType() throws InvalidEnvironmentInstanceException {
        

        Attribute attribute = new Attribute("ssl_port", "8443", "The ssl listen port","");
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(attribute);

        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        List<ProductReleaseDto> list = new ArrayList<ProductReleaseDto>();

        ProductReleaseDto pdto = new ProductReleaseDto("a", "b", "1.0");
        pdto.setPrivateAttributes(attributes);
        List<ProductReleaseDto> lpdto = new ArrayList<ProductReleaseDto>();
        lpdto.add(pdto);

        tierDTO.setProductReleaseDtos(lpdto);

        productValidator.validateAttributes(tierDTO);

        verify(systemPropertiesProvider, times(1)).getProperty(SystemPropertiesProvider.AVAILABLE_ATTRIBUTE_TYPES);
    }
    
    @Test(expected=InvalidEnvironmentInstanceException.class)
    public void testValidateAttributesBadType2() throws InvalidEnvironmentInstanceException {
        

        Attribute attribute = new Attribute("ssl_port", "8443", "The ssl listen port","asdf");
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(attribute);

        TierDto tierDTO = new TierDto();
        tierDTO.setName("tier1");
        tierDTO.setInitialNumberInstances(new Integer(1));
        tierDTO.setMaximumNumberInstances(new Integer(1));
        tierDTO.setMinimumNumberInstances(new Integer(1));
        tierDTO.setImage("image");
        tierDTO.setFlavour("flavor");
        List<ProductReleaseDto> list = new ArrayList<ProductReleaseDto>();

        ProductReleaseDto pdto = new ProductReleaseDto("a", "b", "1.0");
        pdto.setPrivateAttributes(attributes);
        List<ProductReleaseDto> lpdto = new ArrayList<ProductReleaseDto>();
        lpdto.add(pdto);

        tierDTO.setProductReleaseDtos(lpdto);

        productValidator.validateAttributes(tierDTO);

        verify(systemPropertiesProvider, times(1)).getProperty(SystemPropertiesProvider.AVAILABLE_ATTRIBUTE_TYPES);
    }

    
}
