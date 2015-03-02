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

package com.telefonica.euro_iaas.paasmanager.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.claudia.FirewallingClient;
import com.telefonica.euro_iaas.paasmanager.dao.SecurityGroupDao;
import com.telefonica.euro_iaas.paasmanager.manager.impl.SecurityGroupManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Rule;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;

/**
 * @author jesus.movilla
 */
public class SecurityGroupManagerImplTest {

    private SecurityGroupManagerImpl securityGroupManager;
    private FirewallingClient firewallingClient;
    private RuleManager ruleManager;
    private SecurityGroupDao securityGroupDao;

    @Before
    public void setUp() throws Exception {

        firewallingClient = mock(FirewallingClient.class);
        ruleManager = mock(RuleManager.class);
        securityGroupDao = mock(SecurityGroupDao.class);
        securityGroupManager = new SecurityGroupManagerImpl();
        securityGroupManager.setFirewallingClient(firewallingClient);
        securityGroupManager.setRuleManager(ruleManager);
        securityGroupManager.setSecurityGroupDao(securityGroupDao);

        when(firewallingClient.deploySecurityGroup(anyString(), anyString(), anyString(), any(SecurityGroup.class)))
                .thenReturn("2");

    }

    @Test
    public void testCreateSecurityGroup() throws Exception {

        SecurityGroup securityGroup = new SecurityGroup("name", "description");

        Rule rule = new Rule("TCP", "8080", "8080", "", "0.0.0.0/0");

        securityGroup.addRule(rule);

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "dd");

        Mockito.doThrow(new EntityNotFoundException(SecurityGroup.class, "test", securityGroup)).when(securityGroupDao)
                .load(any(String.class));
        when(ruleManager.create(anyString(), anyString(), anyString(), any(Rule.class))).thenReturn(rule);
        when(securityGroupDao.create(any(SecurityGroup.class))).thenReturn(securityGroup);

        SecurityGroup securityGroup2 = securityGroupManager.create("region", "token", "vdc", securityGroup);

        assertNotNull(securityGroup2);
        assertEquals(securityGroup2.getName(), "name");
        assertEquals(securityGroup2.getDescription(), "description");
        assertEquals(securityGroup2.getRules().size(), 1);

    }

    @Test
    public void testDeleteSecurityGroup() throws Exception {

        SecurityGroup securityGroup = new SecurityGroup("name", "description");

        Rule rule = new Rule("TCP", "8080", "8080", "", "0.0.0.0/0");

        securityGroup.addRule(rule);

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "dd");

        Mockito.doNothing().doThrow(new RuntimeException()).when(ruleManager)
                .destroy(anyString(), anyString(), anyString(), any(Rule.class));

        Mockito.doNothing().doThrow(new RuntimeException()).when(securityGroupDao).remove(any(SecurityGroup.class));

        Mockito.doNothing().doThrow(new RuntimeException()).when(firewallingClient)
                .destroySecurityGroup(anyString(), anyString(), anyString(), any(SecurityGroup.class));

        when(securityGroupDao.create(any(SecurityGroup.class))).thenReturn(securityGroup);
        when(securityGroupDao.load(anyString())).thenReturn(securityGroup);
        
        securityGroupManager.destroy("region", "token", "vdc", securityGroup);

    }

}
