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

package com.telefonica.euro_iaas.paasmanager.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.Metadata;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;

/**
 * @author jesus.movilla
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-test-db-config.xml", "classpath:/spring-dao-config.xml" })
public class ProductReleaseDaoJpaImplTest {

    @Autowired
    private ProductReleaseDao productReleaseDao;
    @Autowired
    private AttributeDao attributeDao;

    @Test
    public void testProductReleasesNotAttributes() throws Exception {

        ProductRelease productTomcat = new ProductRelease("mysql", "2", "tomcat 7", null);

        productTomcat = productReleaseDao.create(productTomcat);
        assertNotNull(productTomcat);
        assertEquals(productTomcat.getProduct(), "mysql");
        assertEquals(productTomcat.getVersion(), "2");

        List<ProductRelease> productReleases = productReleaseDao.findAll();
        assertNotNull(productReleases);

        ProductRelease productRelease = productReleaseDao.load("mysql-2");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "mysql");
        assertEquals(productRelease.getVersion(), "2");

    }

    @Test(expected = com.telefonica.fiware.commons.dao.EntityNotFoundException.class)
    public void testProductReleasesNotAttributesError() throws EntityNotFoundException {

        productReleaseDao.load("otro-2");
    }

    @Test(expected = com.telefonica.fiware.commons.dao.EntityNotFoundException.class)
    public void testProductReleasesNotAttributesError2() throws EntityNotFoundException {

        productReleaseDao.load("otro", "3", "tier");
    }

    @Test
    public void testProductReleasesNotAttributesLoadAll() throws Exception {

        ProductRelease productTomcat = new ProductRelease("mysql", "2", "tomcat 7", null);
        productTomcat.setTierName("tierName");

        productTomcat = productReleaseDao.create(productTomcat);
        assertNotNull(productTomcat);
        assertEquals(productTomcat.getProduct(), "mysql");
        assertEquals(productTomcat.getVersion(), "2");

        List<ProductRelease> productReleases = productReleaseDao.findAll();
        assertNotNull(productReleases);

        ProductRelease productRelease = productReleaseDao.load("mysql", "2", "tierName");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "mysql");
        assertEquals(productRelease.getVersion(), "2");

    }

    @Test
    public void testProductReleasesNotAttributesLoadAllError() throws Exception {

        ProductRelease productTomcat = new ProductRelease("mysql", "2", "tomcat 7", null);
        productTomcat = productReleaseDao.create(productTomcat);

        List<ProductRelease> productReleases = productReleaseDao.findAll();

        ProductRelease productRelease = productReleaseDao.load("mysql", "2", "tierName");

    }

    @Test
    public void testProductReleasesWithAttributes() throws Exception {

        List<ProductRelease> productReleases = productReleaseDao.findAll();
        assertNotNull(productReleases);

        int number = productReleases.size();

        Set<Attribute> attproduct = new HashSet<Attribute>();
        attproduct.add(new Attribute("product", "product", "product"));

        ProductRelease productproduct = new ProductRelease("product", "0.1", "product 0.1", attproduct);

        productproduct = productReleaseDao.create(productproduct);
        assertNotNull(productproduct);
        assertEquals(productproduct.getProduct(), "product");
        assertEquals(productproduct.getVersion(), "0.1");

        productReleases = productReleaseDao.findAll();
        assertNotNull(productReleases);
        assertEquals(productReleases.size(), number + 1);

        ProductRelease productRelease = productReleaseDao.load("product-0.1");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "product");
        assertEquals(productRelease.getVersion(), "0.1");
        assertEquals(productRelease.getAttributes().size(), 1);

    }

    @Test
    public void testProductReleasesWithAttributesLoadAll() throws Exception {

        List<ProductRelease> productReleases = productReleaseDao.findAll();
        assertNotNull(productReleases);

        int number = productReleases.size();

        Set<Attribute> attproduct = new HashSet<Attribute>();
        attproduct.add(new Attribute("product", "product", "product"));

        ProductRelease productproduct = new ProductRelease("product", "0.1", "product 0.1", attproduct);
        productproduct.setTierName("tierName");

        productproduct = productReleaseDao.create(productproduct);
        assertNotNull(productproduct);
        assertEquals(productproduct.getProduct(), "product");
        assertEquals(productproduct.getVersion(), "0.1");

        productReleases = productReleaseDao.findAll();
        assertNotNull(productReleases);
        assertEquals(productReleases.size(), number + 1);

        ProductRelease productRelease = productReleaseDao.load("product", "0.1", "tierName");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "product");
        assertEquals(productRelease.getVersion(), "0.1");
        assertEquals(productRelease.getAttributes().size(), 1);

    }

    @Test
    public void testProductReleasesWithMetadata() throws Exception {

        Metadata metproduct = new Metadata("product", "product", "product");

        ProductRelease productproduct = new ProductRelease("myproduct2", "0.1");
        productproduct.addMetadata(metproduct);

        productReleaseDao.create(productproduct);

        ProductRelease productRelease = productReleaseDao.load("myproduct2-0.1");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "myproduct2");
        assertEquals(productRelease.getVersion(), "0.1");
        assertEquals(productRelease.getMetadatas().size(), 1);

    }

    @Test
    public void testProductReleasesWithMetadataAndAttributes() throws Exception {

        Metadata metproduct = new Metadata("product", "product", "product");
        Attribute attribute = new Attribute("product", "product", "product");
        // attribute = attributeDao.create(attribute);

        ProductRelease productproduct = new ProductRelease("product2", "0.1");
        productproduct.addMetadata(metproduct);
        productproduct.addAttribute(attribute);

        productproduct = productReleaseDao.create(productproduct);
        assertNotNull(productproduct);
        assertEquals(productproduct.getProduct(), "product2");
        assertEquals(productproduct.getVersion(), "0.1");
        assertEquals(productproduct.getMetadatas().size(), 1);
        assertEquals(productproduct.getAttributes().size(), 1);

        ProductRelease productRelease = productReleaseDao.load("product2-0.1");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "product2");
        assertEquals(productRelease.getVersion(), "0.1");
        assertEquals(productRelease.getAttributes().size(), 1);
        assertEquals(productRelease.getMetadatas().size(), 1);

    }

    @Test
    public void testProductReleasesWithAttributes2() throws Exception {

        Attribute att = new Attribute("product", "product", "product");

        ProductRelease productproduct = new ProductRelease("product3", "0.3");
        productproduct.addAttribute(att);

        productproduct = productReleaseDao.create(productproduct);
        assertNotNull(productproduct);
        assertEquals(productproduct.getProduct(), "product3");
        assertEquals(productproduct.getVersion(), "0.3");
        assertEquals(productproduct.getAttributes().size(), 1);
        assertEquals(productproduct.getMetadatas().size(), 0);

        ProductRelease productRelease = productReleaseDao.load("product3-0.3");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "product3");
        assertEquals(productRelease.getVersion(), "0.3");
        assertEquals(productRelease.getMetadatas().size(), 0);
        assertEquals(productRelease.getAttributes().size(), 1);

    }

    @Test
    public void testProductReleasesWithEmptyAttributes() throws Exception {

        ProductRelease productproduct = new ProductRelease("product4", "0.1");

        productproduct = productReleaseDao.create(productproduct);
        assertNotNull(productproduct);
        assertEquals(productproduct.getProduct(), "product4");
        assertEquals(productproduct.getVersion(), "0.1");
        assertEquals(productproduct.getMetadatas().size(), 0);
        assertEquals(productproduct.getAttributes().size(), 0);

        ProductRelease productRelease = productReleaseDao.load("product4-0.1");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "product4");
        assertEquals(productRelease.getVersion(), "0.1");
        assertEquals(productRelease.getAttributes().size(), 0);
        assertEquals(productRelease.getMetadatas().size(), 0);

    }

    @Test
    public void testProductReleasesWithEmptyAttributesLoadAll() throws Exception {

        ProductRelease productproduct = new ProductRelease("product4", "0.1");
        productproduct.setTierName("tiername");

        productproduct = productReleaseDao.create(productproduct);
        assertNotNull(productproduct);
        assertEquals(productproduct.getProduct(), "product4");
        assertEquals(productproduct.getVersion(), "0.1");
        assertEquals(productproduct.getMetadatas().size(), 0);
        assertEquals(productproduct.getAttributes().size(), 0);

        ProductRelease productRelease = productReleaseDao.load("product4", "0.1", "tiername");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "product4");
        assertEquals(productRelease.getVersion(), "0.1");
        assertEquals(productRelease.getAttributes().size(), 0);
        assertEquals(productRelease.getMetadatas().size(), 0);

    }

    @Test
    public void testCreateProductReleaseTierNameWithMetadataAndAttributes() throws Exception {

        Metadata metproduct = new Metadata("product", "product", "product");
        Attribute attribute = new Attribute("product", "product", "product");
        // attribute = attributeDao.create(attribute);

        ProductRelease productproduct = new ProductRelease("product2", "0.1");
        productproduct.setTierName("tierName");
        productproduct.addMetadata(metproduct);
        productproduct.addAttribute(attribute);

        productproduct = productReleaseDao.create(productproduct);
        assertNotNull(productproduct);
        assertEquals(productproduct.getProduct(), "product2");
        assertEquals(productproduct.getVersion(), "0.1");
        assertEquals(productproduct.getTierName(), "tierName");
        assertEquals(productproduct.getMetadatas().size(), 1);
        assertEquals(productproduct.getAttributes().size(), 1);

        ProductRelease productRelease = productReleaseDao.load("product2", "0.1", "tierName");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "product2");
        assertEquals(productRelease.getVersion(), "0.1");
        assertEquals(productRelease.getTierName(), "tierName");
        assertEquals(productRelease.getAttributes().size(), 1);
        assertEquals(productRelease.getMetadatas().size(), 1);

    }

    @Test
    public void testLoadProductRealseCreateProductReleaseTierNameWithMetadataAndAttributes() throws Exception {

        Metadata metproduct = new Metadata("product", "product", "product");
        Attribute attribute = new Attribute("product", "product", "product");

        ProductRelease productproduct = new ProductRelease("product2", "0.1");
        productproduct.addMetadata(metproduct);
        productproduct.addAttribute(attribute);

        productproduct = productReleaseDao.create(productproduct);
        assertNotNull(productproduct);
        assertEquals(productproduct.getProduct(), "product2");
        assertEquals(productproduct.getVersion(), "0.1");
        assertEquals(productproduct.getMetadatas().size(), 1);
        assertEquals(productproduct.getAttributes().size(), 1);

        ProductRelease productRelease = productReleaseDao.loadProductReleaseWithMetadata("product2-0.1");
        assertNotNull(productRelease);
        assertEquals(productRelease.getProduct(), "product2");
        assertEquals(productRelease.getVersion(), "0.1");
        assertEquals(productRelease.getAttributes().size(), 1);
        assertEquals(productRelease.getMetadatas().size(), 1);

    }

}
