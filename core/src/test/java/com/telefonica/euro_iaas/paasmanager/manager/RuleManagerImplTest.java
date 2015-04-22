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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.claudia.FirewallingClient;
import com.telefonica.euro_iaas.paasmanager.dao.RuleDao;
import com.telefonica.euro_iaas.paasmanager.manager.impl.RuleManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.Rule;

/**
 * @author jesus.movilla
 */
public class RuleManagerImplTest {

    private RuleManagerImpl ruleManager;
    private RuleDao ruleDao;
    private FirewallingClient firewallingClient = null;

    @Before
    public void setUp() throws Exception {

        ruleManager = new RuleManagerImpl();
        ruleDao = mock(RuleDao.class);
        ruleManager.setRuleDao(ruleDao);
        firewallingClient = mock(FirewallingClient.class);
        ruleManager.setFirewallingClient(firewallingClient);

    }

    @Test
    public void testCreteRule() throws Exception {

        Rule rule = new Rule("TCP", "8080", "8080", "", "0.0.0.0/0");
        String region = "region";
        String token = "1234567689";
        String vdc = "6c23123kdn";

        Mockito.doThrow(new EntityNotFoundException(Rule.class, "test", rule)).when(ruleDao).load(any(String.class));

        when(firewallingClient.deployRule(eq(region), eq(token), eq(vdc), any(Rule.class))).thenReturn("Id");
        when(ruleDao.create(any(Rule.class))).thenReturn(rule);
        Rule rule2 = ruleManager.create(region, token, vdc, rule);

    }

    @Test
    public void testDeleteRule() throws Exception {

        Rule rule = new Rule("TCP", "8080", "8080", "", "0.0.0.0/0");
        String region = "region";
        String token = "1234567689";
        String vdc = "6c23123kdn";

        Mockito.doNothing().doThrow(new RuntimeException()).when(firewallingClient)
                .destroyRule(eq(region), eq(token), eq(vdc), any(Rule.class));
        when(ruleDao.create(any(Rule.class))).thenReturn(rule);
        ruleManager.destroy(region, token, vdc, rule);

    }

}
