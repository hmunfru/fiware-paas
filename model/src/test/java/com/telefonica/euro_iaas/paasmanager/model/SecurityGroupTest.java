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

import junit.framework.TestCase;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

/**
 * @author jesus.movilla
 */
public class SecurityGroupTest extends TestCase {

    private SecurityGroup securityGroup;
    private JSONObject secGroupJson;

    /**
     * Configure the Test Case.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        securityGroup = new SecurityGroup();
        String secGroupString = "{\"rules\":["
                + "{\"from_port\":22,\"group\":{},\"ip_protocol\":\"tcp\",\"to_port\":22,"
                + "\"parent_group_id\":6,\"ip_range\":{\"cidr\":\"0.0.0.0/0\"},\"id\":10},"
                + "{\"from_port\":8080,\"group\":{},\"ip_protocol\":\"tcp\",\"to_port\":8080,"
                + "\"parent_group_id\":6,\"ip_range\":{\"cidr\":\"0.0.0.0/0\"},\"id\":11}]"
                + ",\"tenant_id\":\"ebe6d9ec7b024361b7a3882c65a57dda\","
                + "\"id\":6," + "\"name\":\"namedefault\","
                + "\"description\":\"default\"" + "} ";

        secGroupJson = JSONObject.fromObject(secGroupString);
    }

    /**
     * Test the creation of a security group from a json message.
     * @throws Exception
     */
    @Test
    public void testFromJson() throws Exception {
        securityGroup.fromJson(secGroupJson);
        assertEquals(securityGroup.getDescription(), "default");
        assertEquals(securityGroup.getIdSecurityGroup(), "6");
        assertEquals(securityGroup.getName(), "namedefault");
        assertEquals(securityGroup.getRules().size(), 2);
        assertEquals(securityGroup.getRules().get(0).getFromPort(), "22");
        assertEquals(securityGroup.getRules().get(1).getToPort(), "8080");
    }

    /**
     * Test that 2 rules are equals if they are created with the same values.
     * @throws Exception
     */
    @Test
    public void testEqualRules() throws Exception {
        Rule rule = new Rule("TCP", "80", "80", "", "0.0.0.0/0");
        Rule rule2 = new Rule("TCP", "80", "80", "", "0.0.0.0/0");
        assertEquals(rule.equals(rule2), true);

        rule.setIdParent("1");
        rule.setIdParent("2");
        assertEquals(rule.equals(rule2), false);
    }

    /**
     * Test the creation of a rule.
     * @throws Exception
     */
    @Test
    public void testRule() throws Exception {
        Rule rule = new Rule();
        rule.setCidr("cidr");
        rule.setFromPort("fromport");
        rule.setIdParent("idparent");
        rule.setIdRule("idrule");
        rule.setSourceGroup("sourcegroup");
        rule.setToPort("toPort");
        rule.setIpProtocol("ipProtocol");

        assertEquals(rule.getCidr(), "cidr");
        assertEquals(rule.getFromPort(), "fromport");
        assertEquals(rule.getIdParent(), "idparent");
        assertEquals(rule.getIdRule(), "idrule");
        assertEquals(rule.getSourceGroup(), "sourcegroup");
        assertNotNull(rule.toJSON());

    }
}
