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

import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by fla on 18/03/15.
 */
public class RuleTest {


    /**
     * Configure the Test Case.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * Test the equal operations of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testEquals() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();

        assertFalse(aRule.equals((Object) otherRule));

    }


    /**
     * Test the equal protocol of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testProtocolEqualProtocol1() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();
        
        aRule.setIpProtocol("TCP");
        otherRule.setIpProtocol("TCP");
        
        aRule.setFromPort("8080");
        otherRule.setFromPort("8080");
        
        aRule.setToPort("");
        otherRule.setToPort("");
        
        aRule.setCidr("hi");
        otherRule.setCidr("hi");

        assertTrue(aRule.checkProtocol(otherRule));
        
    }

    /**
     * Test the equal protocol of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testProtocolEqualProtocol2() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();

        aRule.setIpProtocol("TCP");
        otherRule.setIpProtocol("TCP");

        aRule.setFromPort("8080");
        otherRule.setFromPort("8080");

        aRule.setToPort("");
        otherRule.setToPort("9");

        aRule.setCidr("hi");
        otherRule.setCidr("hi");
        
        assertFalse(aRule.checkProtocol(otherRule));

    }

    /**
     * Test the equal protocol of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testProtocolEqualProtocol3() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();

        aRule.setIpProtocol("TCP");
        otherRule.setIpProtocol("TCP");

        aRule.setFromPort("8080");
        otherRule.setFromPort("8081");

        aRule.setToPort("9");
        otherRule.setToPort("9");

        aRule.setCidr("hi");
        otherRule.setCidr("hi");
        
        assertFalse(aRule.checkProtocol(otherRule));

    }

    /**
     * Test the equal protocol of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testProtocolEqualProtocol4() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();

        aRule.setIpProtocol("TCP");
        otherRule.setIpProtocol("TCP");

        aRule.setFromPort("8080");
        otherRule.setFromPort("8081");

        aRule.setToPort("9");
        otherRule.setToPort("");

        aRule.setCidr("hi");
        otherRule.setCidr("hi");
        
        assertFalse(aRule.checkProtocol(otherRule));

    }

    /**
     * Test the equal protocol of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testProtocolEqualProtocol5() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();

        aRule.setIpProtocol("TCP");
        otherRule.setIpProtocol("TCP");

        aRule.setFromPort("8080");
        otherRule.setFromPort("8080");

        aRule.setToPort("");
        otherRule.setToPort("");

        aRule.setCidr("hi");
        otherRule.setCidr("ho");

        assertFalse(aRule.checkProtocol(otherRule));

    }

    /**
     * Test the equal protocol of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testProtocolEqualProtocol6() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();

        aRule.setIpProtocol("TCP");
        otherRule.setIpProtocol("TCP");

        aRule.setFromPort("8080");
        otherRule.setFromPort("8080");

        aRule.setToPort("");
        otherRule.setToPort("");

        aRule.setIdParent("1234");
        otherRule.setIdParent("2345");

        assertFalse(aRule.checkProtocol(otherRule));

    }

    /**
     * Test the equal protocol of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testProtocolEqualProtocol7() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();

        aRule.setIpProtocol("TCP");
        otherRule.setIpProtocol("TCP");

        aRule.setFromPort("8080");
        otherRule.setFromPort("8080");

        aRule.setToPort("");
        otherRule.setToPort("");

        aRule.setIdParent("1234");
        otherRule.setCidr("a CIDR");

        assertFalse(aRule.checkProtocol(otherRule));

    }

    /**
     * Test the equal protocol of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testProtocolEqualProtocol8() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();

        aRule.setIpProtocol("TCP");
        otherRule.setIpProtocol("TCP");

        aRule.setFromPort("8080");
        otherRule.setFromPort("8080");

        aRule.setToPort("");
        otherRule.setToPort("");

        aRule.setCidr("1234");
        otherRule.setIdParent("a CIDR");

        assertFalse(aRule.checkProtocol(otherRule));

    }

    /**
     * Test the equal protocol of a Rule.
     *
     * @throws Exception
     */
    @Test
    public void testProtocolNotEqualProtocol() throws Exception {
        Rule aRule = new Rule();
        Rule otherRule = new Rule();

        aRule.setIpProtocol("TCP");
        otherRule.setIpProtocol("UDP");
        
        assertFalse(aRule.checkProtocol(otherRule));

    }

    /**
     * Test Rule object are identical
     * * 
     */
    @Test
    public void testIdenticalObjects() {
        Rule aRule = new Rule();

        assertTrue(aRule.checkObject(aRule));
    }

    /**
     * Test Rule object are different
     * * 
     */
    @Test
    public void testNotIdenticalObjects1() {
        Rule aRule = new Rule();

        assertFalse(aRule.checkObject(null));
    }

    /**
     * Test Rule object are different
     * * 
     */
    @Test
    public void testNotIdenticalObjects2() {
        Rule aRule = new Rule();
        int i = 0;

        assertFalse(aRule.checkObject(i));
    }
}