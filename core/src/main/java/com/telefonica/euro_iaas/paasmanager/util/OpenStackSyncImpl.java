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

import static com.telefonica.euro_iaas.paasmanager.util.Configuration.OPENSTACK_SYNCHRONIZATION_POLLING_PERIOD;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.claudia.ClaudiaClient;
import com.telefonica.euro_iaas.paasmanager.claudia.FirewallingClient;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentInstanceDao;
import com.telefonica.euro_iaas.paasmanager.dao.RuleDao;
import com.telefonica.euro_iaas.paasmanager.dao.SecurityGroupDao;
import com.telefonica.euro_iaas.paasmanager.dao.TierDao;
import com.telefonica.euro_iaas.paasmanager.dao.TierInstanceDao;
import com.telefonica.euro_iaas.paasmanager.dao.keystone.TokenDao;
import com.telefonica.euro_iaas.paasmanager.dao.keystone.UserDao;
import com.telefonica.euro_iaas.paasmanager.exception.ClaudiaResourceNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackSynchronizationException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.Rule;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.keystone.Token;
import com.telefonica.euro_iaas.paasmanager.model.keystone.User;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;

/**
 * @author jesus.movilla
 */
public class OpenStackSyncImpl extends Thread implements OpenStackSync {
    private TierDao tierDao;
    private TierInstanceDao tierInstanceDao;
    private EnvironmentInstanceDao environmentInstanceDao;
    private RuleDao ruleDao;
    private SecurityGroupDao securityGroupDao;
    private UserDao userDao;
    private TokenDao tokenDao;
    private FirewallingClient firewallingClient;
    private ClaudiaClient claudiaClient;
    private SystemPropertiesProvider systemPropertiesProvider;
    private boolean onecycle;
    private Connection connection;
    private OpenStackRegion openStackRegion;

    private static Logger log = LoggerFactory.getLogger(OpenStackSyncImpl.class);

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.util.OpenStackSync#syncronize()
     */
    public OpenStackSyncImpl(Connection connection, boolean onecycle, TierDao tierDao, TierInstanceDao tierInstanceDao,
            FirewallingClient firewallingClient, ClaudiaClient claudiaClient,
            SystemPropertiesProvider systemPropertiesProvider, EnvironmentInstanceDao environmentInstanceDao,
            RuleDao ruleDao, SecurityGroupDao securityGroupDao, UserDao userDao, TokenDao tokenDao) {
        this.connection = connection;
        this.onecycle = onecycle;
        this.tierDao = tierDao;
        this.tierInstanceDao = tierInstanceDao;
        this.firewallingClient = firewallingClient;
        this.claudiaClient = claudiaClient;
        this.systemPropertiesProvider = systemPropertiesProvider;
        this.environmentInstanceDao = environmentInstanceDao;
        this.ruleDao = ruleDao;
        this.securityGroupDao = securityGroupDao;
        this.userDao = userDao;
        this.tokenDao = tokenDao;

    }

    public void run() {
        try {
            syncronize(connection, onecycle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncronize(Connection connection, boolean onecycle) throws OpenStackSynchronizationException,
            InfrastructureException {
        boolean run = true;
        while (run) {
            try {
                log.info("synchronize STARTING");
                Thread.sleep(OPENSTACK_SYNCHRONIZATION_POLLING_PERIOD);

                List<PaasManagerUser> users = getUsersFromKeystoneDataBase(connection);

                for (int i = 0; i < users.size(); i++) {
                    PaasManagerUser user = users.get(i);
                    ClaudiaData claudiaData = new ClaudiaData(null, user.getTenantId(), null);
                    claudiaData.setUser(user);

                    try {
                        // Synchronizing secGroups
                        //securityGroupsSync(user.getToken(), user.getTenantId());
                        // Synchronizing instances
                        tierInstancesSync(claudiaData);
                    } catch (OpenStackSynchronizationException e) {
                        String message = "Error accesing OpenSatck. Token out of date for user " + user.getUserName();
                        log.info(message);
                        throw new OpenStackSynchronizationException (message);
                    }

                    if (onecycle)
                        run = false;
                }

            } catch (OpenStackSynchronizationException e) {
                String message = "Exception syncronizing secGroups and instances " + e.getMessage();
                log.error(message);
            } catch (NumberFormatException e) {
                String message = "NumberFormatException " + e.getMessage();
                log.error(message);
            } catch (InterruptedException e) {
                String message = "Interrupting the thread " + e.getMessage();
                log.error(message);
                run = false;
            }
        }
    }

    private List<PaasManagerUser> getUsersFromKeystoneDataBase(Connection connection) {
        List<PaasManagerUser> paasManagerUsers = new ArrayList<PaasManagerUser>();
        Collection<? extends GrantedAuthority> gas = new ArrayList();

        List<User> keystoneUsers = userDao.findAll(connection);

        for (int i = 0; i < keystoneUsers.size(); i++) {
            try {
                Token token;

                token = tokenDao.findLastTokenFromUser(connection, keystoneUsers.get(i).getId());
                log.debug("User " + keystoneUsers.get(i).getName() + " " + token.getTenantId() + " token "
                        + token.getId());

                PaasManagerUser paasManagerUser = new PaasManagerUser(keystoneUsers.get(i).getName(), token.getId(),
                        gas);

                paasManagerUser.setUsername(keystoneUsers.get(i).getName());
                paasManagerUser.setTenantId(token.getTenantId());
                paasManagerUser.setToken(token.getId());
                paasManagerUsers.add(paasManagerUser);
            } catch (EntityNotFoundException enfe) {
                String message = "The User " + keystoneUsers.get(i).getName() + " does not have "
                        + "any token associated";
                log.info(message);
            } catch (OpenStackException e) {
                String message = "Error to obtain the token for the user  " + keystoneUsers.get(i).getName() + " : "
                        + e.getMessage();
                log.info(message);
            }

        }
        return paasManagerUsers;

    }

    private void tierInstancesSync(ClaudiaData claudiaData) throws OpenStackSynchronizationException {
        log.info("Sinconizing tier instances ");
        boolean included = false;
        List<TierInstance> tierInstances = findAllTierInstancesDB(claudiaData);
        List<String> servers = new ArrayList<String>();

        try {
            List<String> regions = openStackRegion.getRegionNames(claudiaData.getUser().getToken());
            for (String region : regions) {

                servers.addAll(claudiaClient.findAllVMs(claudiaData, region));
            }
        } catch (ClaudiaResourceNotFoundException e) {
            String message = "No servers in Openstack";
            log.warn(message);
        } catch (OpenStackException e) {
            String message = "No regions detected in Openstack";
            log.warn(message);
        }

        for (int i = 0; i < tierInstances.size(); i++) {
            TierInstance tierInstance = tierInstances.get(i);
            for (int j = 0; j < servers.size(); j++) {
                if (tierInstance.getName().equals(servers.get(j))) {
                    included = true;
                }
            }
            if (!included) {
                log.info("Server not include " + tierInstance.getName() + "  " + tierInstance.getVM().getFqn());
                removeTierInstanceDB(tierInstance);
            }
        }
    }

    /*private void securityGroupsSync(String token, String vdc) throws OpenStackSynchronizationException,
            InfrastructureException, OpenStackException {
        boolean included = false;
        log.debug("Sincronizing security groups ...  ");
        List<SecurityGroup> securityGroups = findAllSecurityGroupsDB(vdc);

        List<String> regions = openStackRegion.getRegionNames(token);

        List<SecurityGroup> securityGroupsOpenStack = new ArrayList<SecurityGroup>(10);

        for (String region : regions) {
            List<SecurityGroup> securityGroups1 = firewallingClient.loadAllSecurityGroups(region, token, vdc);
            securityGroupsOpenStack.addAll(securityGroups1);
        }

        for (int i = 0; i < securityGroups.size(); i++) {
            SecurityGroup securityGroup = securityGroups.get(i);
            for (int j = 0; j < securityGroupsOpenStack.size(); j++) {
                if (securityGroup.getName().equals(securityGroupsOpenStack.get(j).getName())) {
                    included = true;
                }
            }
            if (!included) {
                log.debug("It is not included  " + securityGroup.getName() + " . Deploying ...");
                try {

                    String region = null;
                    try {
                        region = tierDao.findRegionBySecurityGroup(securityGroup.getIdSecurityGroup());
                    } catch (EntityNotFoundException e) {
                        region = "";
                    }

                    String securityGroupId = firewallingClient.deploySecurityGroup(region, token, vdc, securityGroup);
                    securityGroup.setIdSecurityGroup(securityGroupId);
                    for (int j = 0; j < securityGroup.getRules().size(); j++) {
                        Rule rule = securityGroup.getRules().get(j);
                        rule.setIdParent(securityGroupId);
                        String idrule = firewallingClient.deployRule(region, token, vdc, rule);
                        rule.setIdRule(idrule);

                        ruleDao.update(rule);
                    }
                    log.info("SecurityGroup " + securityGroup.getIdSecurityGroup()
                            + " deployed and syncrhonized with Database");
                    securityGroupDao.updateSecurityGroupId(securityGroupId, securityGroup);
                } catch (InfrastructureException e1) {
                    String message = "Error re-creating the securityGroup : " + securityGroup.getIdSecurityGroup();
                    log.error(message);
                    throw new OpenStackSynchronizationException(message, e1);
                } catch (InvalidEntityException ie) {
                    String message = "Error updating the DB with a securitygroup : "
                            + securityGroup.getIdSecurityGroup() + " Name: " + securityGroup.getName();
                    log.error(message);
                    throw new OpenStackSynchronizationException(message, ie);
                }
            }
        }
    }*/

    /*private List<SecurityGroup> findAllSecurityGroupsDB(String vdc) {
        List<SecurityGroup> securityGroups = new ArrayList<SecurityGroup>();
        TierSearchCriteria criteria = new TierSearchCriteria();
        criteria.setVdc(vdc);
        try {
            List<Tier> tiers = tierDao.findByCriteria(criteria);
            for (int i = 0; i < tiers.size(); i++) {
                if (tiers.get(i).getSecurityGroup() != null)
                    securityGroups.add(tiers.get(i).getSecurityGroup());
            }
        } catch (EntityNotFoundException e) {
            String message = "No SecurityGroups found in PaasManager Database for vdc " + vdc;
            log.info(message);
        }
        return securityGroups;
    }*/

    private List<TierInstance> findAllTierInstancesDB(ClaudiaData claudiaData) {
        List<TierInstance> tierInstances = new ArrayList<TierInstance>();
        TierInstanceSearchCriteria criteria = new TierInstanceSearchCriteria();
        criteria.setVdc(claudiaData.getVdc());
        try {
            tierInstances = tierInstanceDao.findByCriteria(criteria);
        } catch (EntityNotFoundException e) {
            String message = "No SecurityGroups found in PaasManager Database for vdc " + claudiaData.getVdc();
            log.info(message);
        }
        return tierInstances;
    }

    /*
     * private SecurityGroup findRightSecurityGroup(SecurityGroup securityGroup, List<SecurityGroup>
     * securityGroupsOpenStack) { for (int i=0; i < securityGroupsOpenStack.size(); i ++) { if
     * (securityGroup.getName().equals(securityGroupsOpenStack.get(i).getName())){
     * securityGroup.setDescription(securityGroupsOpenStack.get(i).getDescription());
     * securityGroup.setIdSecurityGroup(securityGroupsOpenStack.get(i).getIdSecurityGroup());
     * securityGroup.setRules(securityGroupsOpenStack.get(i).getRules()); return securityGroup; } } return
     * securityGroup; }
     */

    private void removeTierInstanceDB(TierInstance tierInstance) throws OpenStackSynchronizationException {

        EnvironmentInstanceSearchCriteria criteria = new EnvironmentInstanceSearchCriteria();
        criteria.setTierInstance(tierInstance);
        criteria.setVdc(tierInstance.getVdc());

        List<EnvironmentInstance> environmentInstances = environmentInstanceDao.findByCriteria(criteria);

        for (int i = 0; i < environmentInstances.size(); i++) {
            EnvironmentInstance environmentInstance = environmentInstances.get(i);
            List<TierInstance> tierInstances = environmentInstance.getTierInstances();

            try {
                environmentInstance.setTierInstances(null);
                environmentInstance = environmentInstanceDao.update(environmentInstance);

                tierInstances.remove(tierInstance);
                environmentInstance.setTierInstances(tierInstances);
                environmentInstance = environmentInstanceDao.update(environmentInstance);
            } catch (Exception e) {
                String message = " Error updating tierInstances of environmentInstance "
                        + environmentInstance.getName();
                log.error(message);
                throw new OpenStackSynchronizationException(message, e);
            }
        }
        tierInstanceDao.remove(tierInstance);
    }

    public OpenStackRegion getOpenStackRegion() {
        return openStackRegion;
    }

    public void setOpenStackRegion(OpenStackRegion openStackRegion) {
        this.openStackRegion = openStackRegion;
    }
}
