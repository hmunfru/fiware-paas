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

package com.telefonica.euro_iaas.paasmanager.manager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.claudia.FirewallingClient;
import com.telefonica.euro_iaas.paasmanager.dao.SecurityGroupDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.manager.RuleManager;
import com.telefonica.euro_iaas.paasmanager.manager.SecurityGroupManager;
import com.telefonica.euro_iaas.paasmanager.model.Rule;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;

public class SecurityGroupManagerImpl implements SecurityGroupManager {

    private SecurityGroupDao securityGroupDao = null;
    private RuleManager ruleManager = null;
    private FirewallingClient firewallingClient = null;

    private static Logger log = LoggerFactory.getLogger(SecurityGroupManagerImpl.class);

    /**
     * Creates a SecurityGroup in a openstack of a certan region and insert info in database
     * @param region
     * @param token
     * @param vdc
     * @param securityGroup
     * @return
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     */
    public SecurityGroup create(String region, String token, String vdc, SecurityGroup securityGroup)
            throws InvalidEntityException, AlreadyExistsEntityException, InfrastructureException {

        SecurityGroup securityGroupDB = new SecurityGroup();
        String idSecurityGroup = firewallingClient.deploySecurityGroup(region, token, vdc, securityGroup);
        log.info("Create security group " + securityGroup.getName() + " with idSecurityGroup " + idSecurityGroup);
        
        List<Rule> rules = securityGroup.getRules();
        for (Rule rule : rules) {
            rule.setIdParent(idSecurityGroup);
            rule = ruleManager.create(region, token, vdc, rule);
            securityGroupDB.addRule(rule);
        }
        securityGroup.setIdSecurityGroup(idSecurityGroup);
        securityGroupDB = insert(securityGroup);
        return securityGroupDB;
    }
    
    /**
     * Creates a SecurityGroup only in a openstack of a certain region
     * @param region
     * @param token
     * @param vdc
     * @param securityGroup
     * @return
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     */
    /*public SecurityGroup createInOpenstack(String region, String token, String vdc, SecurityGroup securityGroup)
            throws InvalidEntityException, AlreadyExistsEntityException, InfrastructureException {

        String idSecurityGroup = firewallingClient.deploySecurityGroup(region, token, vdc, securityGroup);
        log.info("Create security group " + securityGroup.getName() + " with idSecurityGroup " + idSecurityGroup);
        
        List<Rule> rules = securityGroup.getRules();
        //if (securityGroup.getRules() != null) {
        for (Rule rule : rules) {
            rule.setIdParent(idSecurityGroup);
            rule = ruleManager.create(region, token, vdc, rule);
            securityGroup.addRule(rule);
        //    }
        }
        securityGroup.setIdSecurityGroup(idSecurityGroup);
        return securityGroup;
    }*/
    
    /**
     * Adds a rule to a security group
     * @param region
     * @param token
     * @param vdc
     * @param securityGroup
     * @param rule
     * @return
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     * @throws InfrastructureException
     */
    public void addRule(String region, String token, String vdc, SecurityGroup securityGroup, Rule rule)
            throws InvalidEntityException, AlreadyExistsEntityException, InfrastructureException {

        rule.setIdParent(securityGroup.getIdSecurityGroup());
        rule = ruleManager.create(region, token, vdc, rule);
        securityGroup.addRule(rule);
        securityGroupDao.update(securityGroup);

    }

    /**
     * Insert a SecurityGroup in the database
     * @param securityGroup
     * @return
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     */
    public SecurityGroup insert(SecurityGroup securityGroup) throws AlreadyExistsEntityException,
            InvalidEntityException {
        SecurityGroup securityGroupDB = new SecurityGroup();
        try {
            securityGroupDB = securityGroupDao.load(securityGroup.getName());
            String errorMessage = "The securityGroup Object " + securityGroup.getName() + " already exist in Database";
            throw new InvalidEntityException(errorMessage);

        } catch (EntityNotFoundException e) {
        }

        securityGroupDB.setName(securityGroup.getName());
        securityGroupDB.setDescription(securityGroup.getDescription());
        securityGroupDB.setIdSecurityGroup(securityGroup.getIdSecurityGroup());

        if (securityGroup.getRules() != null) {
            securityGroupDB.setRules(securityGroup.getRules());
        }

        try {
            securityGroupDB = securityGroupDao.create(securityGroupDB);
        } catch (AlreadyExistsEntityException aee) {
            throw new AlreadyExistsEntityException(securityGroup);
        } catch (Exception aee) {
            String errorMessage = "The securityGroup Object " + securityGroupDB.getName() + " is " + "NOT valid";
            throw new InvalidEntityException(errorMessage);
        }

        return securityGroupDB;
    }

    /**
     * It destroy a security group.
     * @param region
     * @param token
     * @param vdc
     * @param secGroup
     * @return
     * @throws AlreadyExistsEntityException
     * @throws InvalidEntityException
     */
    public void destroy(String region, String token, String vdc, SecurityGroup secGroup)
            throws InvalidEntityException, InfrastructureException {
        log.info("Destroying securitygroup: " + secGroup.getName());
        List<Rule> rules = secGroup.getRules();
    	if (rules.isEmpty()) {
            log.warn("There is not any rule associated to the security group");
        } else {
            List<Rule> cloneRules = secGroup.cloneRules();
            secGroup.setRules(null);
            securityGroupDao.update(secGroup);

            for (Rule rule : cloneRules) {
                try {
                    ruleManager.destroy(region, token, vdc, rule);
                } catch (Exception e) {
                    log.warn("Error to destroy the rule " + e.getMessage());
                }
            }
        }

        try {
            firewallingClient.destroySecurityGroup(region, token, vdc, secGroup);
        } catch (Exception e) {
            log.warn("There is not any rule associated to the security group");
        }
        securityGroupDao.remove(secGroup);
    }

    /**
     * Updates a securitygroup in database
     * @param secGroup
     * @return SecurityGroup
     * @throws InvalidEntityException
     */
    public SecurityGroup update(SecurityGroup securityGroup) throws InvalidEntityException {
        return securityGroupDao.update(securityGroup);
    }

    /**
     * List all security groups that are in te database
     * @return
     * @throws
     */    
    public List<SecurityGroup> findAll() {
        return securityGroupDao.findAll();
    }

    /**
     * Load a security group by name
     * @param name
     * @return SecurityGroup
     * @throws EntityNotFoundException
     */    
    public SecurityGroup load(String name) throws EntityNotFoundException {
        return securityGroupDao.load(name);
    }
    
    /**
     * @param securityGroupDao
     *            the securityGroupDao to set
     */
    public void setSecurityGroupDao(SecurityGroupDao securityGroupDao) {
        this.securityGroupDao = securityGroupDao;
    }
    
    /**
     * @param ruleManager
     *            the ruleManager to set
     */
    public void setRuleManager(RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }
    
    /**
     * @param firewallingClient
     *            the firewallingClient to set
     */
    public void setFirewallingClient(FirewallingClient firewallingClient) {
        this.firewallingClient = firewallingClient;
    }
}
