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

package com.telefonica.euro_iaas.paasmanager.model;

import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.model.dto.ProductInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductReleaseDto;


import static org.junit.Assert.assertEquals;

/**
 * @author jesus.movilla
 */
public class ProductReleaseTest {

    private ProductRelease productRelease;
    private JSONObject productReleaseJson, productReleaseJsonNoAttributes;
    private JSONObject productReleaseJsonOneAttributeAndMetadatas, productReleaseJsonOneAttribute;
    private JSONObject productReleaseJsonNoReleaseNotesNoSSOO;

    /**
     * Configure the Test Case.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        productRelease = new ProductRelease();

        String productReleaseStringNoAttributes = "{" + "\"releaseNotes\":\"Tomcat server 7\"," + "\"version\":\"7\","
                + "\"product\":" + "{" + "\"name\":\"tomcat\"," + "\"description\":\"tomcat J2EE container\"" + "},"
                + "\"supportedOOSS\":" + "["
                + "{\"description\":\"Ubuntu 10.04\",\"name\":\"Ubuntu\",\"osType\":\"94\",\"version\":\"10.04\"},"
                + "{\"description\":\"Debian 5\",\"name\":\"Debian\",\"osType\":\"95\",\"version\":\"5\"},"
                + "{\"description\":\"Centos 2.9\",\"name\":\"Centos\",\"osType\":\"76\",\"version\":\"2.9\"}" + "]"
                + "}";

        String productReleaseString = "{" + "\"releaseNotes\":\"Tomcat server 7\"," + "\"version\":\"7\","
                + "\"product\":" + "{" + "\"name\":\"tomcat\"," + "\"description\":\"tomcat J2EE container\","
                + "\"attributes\":" + "["
                + "{\"key\":\"port\",\"value\":\"8080\",\"description\":\"The listen port\"},"
                + "{\"key\":\"ssl_port\",\"value\":\"8443\",\"description\":\"The ssl listen port\"},"
                + "{\"key\":\"ssl_port\",\"value\":\"8443\",\"description\":\"The ssl listen port\"},"
                + "{\"key\":\"id_web_server\",\"value\":\"default\",\"description\":\"The id web server\"},"
                + "{\"key\":\"sdcgroupid\",\"value\":\"id_web_server\",\"description\":\"sdcgroupid\"}" + "]" + "},"
                + "\"supportedOOSS\":" + "["
                + "{\"description\":\"Ubuntu 10.04\",\"name\":\"Ubuntu\",\"osType\":\"94\",\"version\":\"10.04\"},"
                + "{\"description\":\"Debian 5\",\"name\":\"Debian\",\"osType\":\"95\",\"version\":\"5\"},"
                + "{\"description\":\"Centos 2.9\",\"name\":\"Centos\",\"osType\":\"76\",\"version\":\"2.9\"}" + "]"
                + "}";

        String productReleaseStringOneAttribute = "{"
                + "\"releaseNotes\":\"mysql 1.2.4\","
                + "\"version\":\"1.2.4\","
                + "\"product\":"
                + "{"
                + "\"name\":\"mysql\","
                + "\"description\":\"mysql\","
                + "\"attributes\":"
                +
                // "[" +
                "{\"key\":\"port\",\"value\":\"8080\",\"description\":\"The listen port\"}"
                +
                // "]" +
                "}," + "\"supportedOOSS\":" + "[" + "{" + "\"description\":\"Ubuntu 10.04\"," + "\"name\":\"Ubuntu\","
                + "\"osType\":\"94\"," + "\"version\":\"10.04\"" + "}," + "{" + "\"description\":\"Debian 5\","
                + "\"name\":\"Debian\"," + "\"osType\":\"95\"," + "\"version\":\"5\"" + "}," + "{"
                + "\"description\":\"Centos 2.9\"," + "\"name\":\"Centos\"," + "\"osType\":\"76\",\"version\":\"2.9\""
                + "}" + "]" + "}";

        String productReleaseStringOneAttributeAndMetadatas = "{"
                + "\"releaseNotes\":\"mysql 1.2.4\","
                + "\"version\":\"1.2.4\","
                + "\"product\":"
                + "{"
                + "\"name\":\"mysql\","
                + "\"description\":\"mysql\","
                + "\"attributes\":"
                +
                // "[" +
                "{\"key\":\"port\",\"value\":\"8080\",\"description\":\"The listen port\"}"
                +
                // "]" +
                ","
                + "\"metadatas\":" + "["
                + "{\"key\":\"port\",\"value\":\"8080\",\"description\":\"The listen port\"},"
                + "{\"key\":\"ssl_port\",\"value\":\"8443\",\"description\":\"The ssl listen port\"},"
                + "{\"key\":\"ssl_port\",\"value\":\"8443\",\"description\":\"The ssl listen port\"},"
                + "{\"key\":\"id_web_server\",\"value\":\"default\",\"description\":\"The id web server\"},"
                + "{\"key\":\"sdcgroupid\",\"value\":\"id_web_server\",\"description\":\"sdcgroupid\"}" + "]" + "},"

                + "\"supportedOOSS\":" + "[" + "{" + "\"description\":\"Ubuntu 10.04\"," + "\"name\":\"Ubuntu\","
                + "\"osType\":\"94\"," + "\"version\":\"10.04\"" + "}," + "{" + "\"description\":\"Debian 5\","
                + "\"name\":\"Debian\"," + "\"osType\":\"95\"," + "\"version\":\"5\"" + "}," + "{"
                + "\"description\":\"Centos 2.9\"," + "\"name\":\"Centos\"," + "\"osType\":\"76\",\"version\":\"2.9\""
                + "}" + "]" + "}";

        String productReleaseNoReleaseNotesNoSSOO = "{" + "\"version\":\"1.0\"," + "\"product\":" + "{"
                + "\"name\":\"henar10\"," + "\"attributes\":{\"key\":\"ssl_port5\",\"value\":\"8443\"}" + "}" + "}";

        productReleaseJson =
                JSONObject.fromObject(productReleaseString);

        productReleaseJsonNoAttributes =
                JSONObject.fromObject(productReleaseStringNoAttributes);

        productReleaseJsonOneAttribute =
                JSONObject.fromObject(productReleaseStringOneAttribute);

        productReleaseJsonOneAttributeAndMetadatas =
                JSONObject.fromObject(productReleaseStringOneAttributeAndMetadatas);

        productReleaseJsonNoReleaseNotesNoSSOO =
                JSONObject.fromObject(productReleaseNoReleaseNotesNoSSOO);
    }

    /**
     * Test the creation of a product release from a json message.
     * @throws Exception
     */
    @Test
    public void testFromJson() throws Exception {
        productRelease.fromSdcJson(productReleaseJson);
        assertEquals(productRelease.getDescription(), "Tomcat server 7");
        assertEquals(productRelease.getVersion(), "7");
        assertEquals(productRelease.getProduct(), "tomcat");
        assertEquals(productRelease.getName(), "tomcat-7");
    }

    /**
     * Test the creation of a product release without attributtes in a json message.
     * @throws Exception
     */
    @Test
    public void testFromJsonNoAttributes() throws Exception {
        productRelease.fromSdcJson(productReleaseJsonNoAttributes);
        assertEquals(productRelease.getDescription(), "Tomcat server 7");
        assertEquals(productRelease.getVersion(), "7");
        assertEquals(productRelease.getProduct(), "tomcat");
        assertEquals(productRelease.getName(), "tomcat-7");
    }

    /**
     * Test the creation of a product message from a json message with only 1 attribute.
     * @throws Exception
     */
    @Test
    public void testOneAttributeFromJson() throws Exception {
        productRelease.fromSdcJson(productReleaseJsonOneAttribute);
        assertEquals(productRelease.getDescription(), "mysql 1.2.4");
        assertEquals(productRelease.getVersion(), "1.2.4");
        assertEquals(productRelease.getProduct(), "mysql");
        assertEquals(productRelease.getName(), "mysql-1.2.4");
    }

    /**
     * Test the creation of a product message from a json message with only 1 attribute and 1 metadata.
     * @throws Exception
     */
    @Test
    public void testOneAttributeAndMetadatasFromJson() throws Exception {
        productRelease.fromSdcJson(productReleaseJsonOneAttributeAndMetadatas);
        assertEquals(productRelease.getDescription(), "mysql 1.2.4");
        assertEquals(productRelease.getVersion(), "1.2.4");
        assertEquals(productRelease.getProduct(), "mysql");
        assertEquals(productRelease.getName(), "mysql-1.2.4");
    }

    /**
     * Test the creation of a product release creation from json message with only version, product and name.
     * @throws Exception
     */
    @Test
    public void testFromJsonNoReleaseNotesNoSSOO() throws Exception {
        productRelease.fromSdcJson(productReleaseJsonNoReleaseNotesNoSSOO);
        assertEquals(productRelease.getVersion(), "1.0");
        assertEquals(productRelease.getProduct(), "henar10");
        assertEquals(productRelease.getName(), "henar10-1.0");
    }

    /**
     * Test the creation of a product release.
     * @throws Exception
     */
    @Test
    public void testProductRelease() throws Exception {

        Set<Attribute> atts = new HashSet<Attribute>();
        Attribute att = new Attribute();
        att.setKey("att");
        att.setValue("value");
        att.setDescription("description");
        atts.add(att);
        ProductRelease product = new ProductRelease("name", "version", "description", atts);
        product.addAttribute(new Attribute("att2", "value", "description"));
        product.setTierName("tierName");
        product.setId(new Long(1));
        assertEquals(product.getName(), "name-version");
        assertEquals(product.getTierName(), "tierName");
        assertEquals(product.getVersion(), "version");
        assertEquals(product.getId().intValue(), 1);
        assertEquals(product.getDescription(), "description");
        assertEquals(product.getAttributes().size(), 2);
        assertEquals(product.getAttribute("att").getValue(), "value");
        assertEquals(product.getMetadatas().size(), 0);

        Metadata meta = new Metadata("key", "value", "description");
        product.addMetadata(meta);

        Metadata meta2 = new Metadata();
        meta2.setKey("meta");
        meta2.setValue("value");
        meta2.setDescription("description");
        assertEquals(meta.equals(meta2), false);

        product.addMetadata(meta2);

        assertEquals(product.getMetadata("key").getKey(), "key");
        assertEquals(product.getMetadata("key").getValue(), "value");
        assertEquals(product.getMetadata("key").getDescription(), "description");
        assertEquals(product.getAttribute("att2").getKey(), "att2");
        assertEquals(product.getAttribute("att2").getValue(), "value");
        assertEquals(product.getAttribute("att2").getDescription(), "description");

        ProductRelease product2 = new ProductRelease("name2", "version", "description", atts);
        product.setId(new Long(2));
        assertEquals(product2.equals(product), false);


    }

    /**
     * Check the creation of a product release and InstanceDto.
     */
    @Test
    public void testProductReleaseAndInstDto() {

        ProductReleaseDto productDto = new ProductReleaseDto("product", "description", "version");
        ProductReleaseDto productDto2 = new ProductReleaseDto("product", "version");
        productDto2.setProductDescription("description");

        ProductInstanceDto productInstanceDto = new ProductInstanceDto(productDto);
        productInstanceDto.setName("product");
        Set<Attribute> atts = new HashSet<Attribute>();
        atts.add(new Attribute("key", "value"));
        productInstanceDto.setAttributes(atts);
        productInstanceDto.setVdc("vdc");


        assertEquals(productInstanceDto.getAttributes().size(), 1);
        assertEquals(productInstanceDto.getProductReleaseDto().getProductName(), "product");
        assertEquals(productInstanceDto.getName(), "product");
        assertEquals(productInstanceDto.getVdc(), "vdc");

    }
}
