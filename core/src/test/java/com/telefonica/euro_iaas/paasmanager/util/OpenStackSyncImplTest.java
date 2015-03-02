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

package com.telefonica.euro_iaas.paasmanager.util;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.claudia.ClaudiaClient;
import com.telefonica.euro_iaas.paasmanager.claudia.FirewallingClient;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentInstanceDao;
import com.telefonica.euro_iaas.paasmanager.dao.RuleDao;
import com.telefonica.euro_iaas.paasmanager.dao.SecurityGroupDao;
import com.telefonica.euro_iaas.paasmanager.dao.TierDao;
import com.telefonica.euro_iaas.paasmanager.dao.TierInstanceDao;
import com.telefonica.euro_iaas.paasmanager.dao.keystone.TokenDao;
import com.telefonica.euro_iaas.paasmanager.dao.keystone.UserDao;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackSynchronizationException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Rule;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import com.telefonica.euro_iaas.paasmanager.model.keystone.Token;
import com.telefonica.euro_iaas.paasmanager.model.keystone.User;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;

/**
 * @author jesus.movilla
 */
public class OpenStackSyncImplTest {

    private OpenStackSyncImpl openStackImpl;

    private TierDao tierDao;
    private TierInstanceDao tierInstanceDao;
    private EnvironmentInstanceDao environmentInstanceDao;
    private RuleDao ruleDao;
    private SecurityGroupDao securityGroupDao;
    private UserDao userDao;
    private TokenDao tokenDao;
    private FirewallingClient firewallingClient;
    private ClaudiaClient claudiaClient;
    private SecurityGroup secGroup;
    private SecurityGroup differentSecGroup;
    private final List<SecurityGroup> securityGroupsOS = new ArrayList<SecurityGroup>();
    private SystemPropertiesProvider systemPropertiesProvider;
    private final List<String> names = new ArrayList<String>();
    private Connection connection;

    @Before
    public void Setup() throws Exception {
        List<Tier> tiers = new ArrayList<Tier>();
        List<ProductRelease> pReleases = new ArrayList<ProductRelease>();

        /*secGroup = new SecurityGroup();
        secGroup.setName("name");
        secGroup.setDescription("desc");
        secGroup.setIdSecurityGroup("1");
        secGroup.addRule(new Rule("ipProtocol", "fromPort", "toPort", "sourceGroup", "cidr"));

        differentSecGroup = new SecurityGroup();
        differentSecGroup.setName("name2");
        differentSecGroup.setDescription("desc2");
        differentSecGroup.setIdSecurityGroup("2");
        differentSecGroup.addRule(new Rule("ipProtocol2", "fromPort2", "toPort2", "sourceGroup2", "cidr2"));
		*/
        
        Tier tier = new Tier("name", 1, 1, 1, pReleases, "flavour", "image", "icono");
        //tier.setSecurityGroup(secGroup);
        tiers.add(tier);

        List<TierInstance> tierInstances = new ArrayList<TierInstance>();
        List<ProductInstance> productInstances = new ArrayList<ProductInstance>();

        TierInstance tierInstance = new TierInstance(tier, productInstances, new VM("fqn", "ip", "hostname", "domain"));
        tierInstance.setName("name");
        tierInstances.add(tierInstance);

        EnvironmentInstance environmentInstance = new EnvironmentInstance("blueprintName", "description");
        environmentInstance.setTierInstances(tierInstances);
        List<EnvironmentInstance> environmentInstances = new ArrayList<EnvironmentInstance>();
        environmentInstances.add(environmentInstance);

        // tierDao
        tierDao = mock(TierDao.class);
        when(tierDao.findByCriteria(any(TierSearchCriteria.class))).thenReturn(tiers);

        firewallingClient = mock(FirewallingClient.class);

        tierInstanceDao = mock(TierInstanceDao.class);
        when(tierInstanceDao.findByCriteria(any(TierInstanceSearchCriteria.class))).thenReturn(tierInstances);

        environmentInstanceDao = mock(EnvironmentInstanceDao.class);
        when(environmentInstanceDao.findByCriteria(any(EnvironmentInstanceSearchCriteria.class))).thenReturn(
                environmentInstances);
        when(environmentInstanceDao.update(any(EnvironmentInstance.class))).thenReturn(environmentInstance);

        claudiaClient = mock(ClaudiaClient.class);

        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        when(systemPropertiesProvider.getProperty(any(String.class))).thenReturn("1000");

        ruleDao = mock(RuleDao.class);
        securityGroupDao = mock(SecurityGroupDao.class);

        // UserDao
        userDao = mock(UserDao.class);
        List<User> users = new ArrayList<User>();
        User user = new User("userId", "userNname", "extras");
        users.add(user);
        when(userDao.findAll(any(Connection.class))).thenReturn(users);

        // TokenDao
        tokenDao = mock(TokenDao.class);
        Token token = new Token("tokenId", "expires", "extra");
        token.setTenantId("tenantId");
        when(tokenDao.findLastTokenFromUser(any(Connection.class), any(String.class))).thenReturn(token);

        connection = mock(Connection.class);

        /*
         * String vdc = "ebe6d9ec7b024361b7a3882c65a57dda"; claudiaData = new ClaudiaData("org", vdc, "service");
         * Collection<? extends GrantedAuthority> dd = new ArrayList(); PaasManagerUser manUser = new
         * PaasManagerUser("dd", "f9f2ae5abf9e4723a89f5f2f684c74da", dd); manUser.setTenantId(vdc);
         * claudiaData.setUser(manUser);
         */

        openStackImpl = new OpenStackSyncImpl(connection, true, tierDao, tierInstanceDao, firewallingClient,
                claudiaClient, systemPropertiesProvider, environmentInstanceDao, ruleDao, securityGroupDao, userDao,
                tokenDao);
        OpenStackRegion openStackRegion = mock(OpenStackRegion.class);
        openStackImpl.setOpenStackRegion(openStackRegion);

    }

   /* @Test
    public void testSyncronizeSecGroupsNOTSynchronizedDeployingSecurityGroup() throws Exception {
        securityGroupsOS.add(differentSecGroup);
        names.add("name");
        when(firewallingClient.loadAllSecurityGroups(anyString(), anyString(), anyString())).thenReturn(
                securityGroupsOS);
        when(firewallingClient.deploySecurityGroup(anyString(), anyString(), anyString(), any(SecurityGroup.class)))
                .thenReturn("1");
        when(firewallingClient.deployRule(anyString(), anyString(), anyString(), any(Rule.class))).thenReturn("1");
        when(claudiaClient.findAllVMs(any(ClaudiaData.class), anyString())).thenReturn(names);

        try {
            openStackImpl.syncronize(connection, true);
        } catch (OpenStackSynchronizationException e) {
            e.printStackTrace();
        }
    }*/

 /*   @Test
    public void testSyncronizeSecurityGroupSynchronized() throws Exception {

        securityGroupsOS.add(secGroup);
        names.add("name");

        when(firewallingClient.loadAllSecurityGroups(anyString(), anyString(), anyString())).thenReturn(
                securityGroupsOS);
        when(claudiaClient.findAllVMs(any(ClaudiaData.class), anyString())).thenReturn(names);

        try {
            openStackImpl.syncronize(connection, true);
        } catch (OpenStackSynchronizationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSyncronizeTierInstanceNotFoundDB() throws Exception {
        names.add("NOTname");
        when(firewallingClient.loadAllSecurityGroups(anyString(), anyString(), anyString())).thenReturn(
                securityGroupsOS);
        when(claudiaClient.findAllVMs(any(ClaudiaData.class), anyString())).thenReturn(names);

        try {
            openStackImpl.syncronize(connection, true);
        } catch (OpenStackSynchronizationException e) {
            e.printStackTrace();
        }
    }*/
}
