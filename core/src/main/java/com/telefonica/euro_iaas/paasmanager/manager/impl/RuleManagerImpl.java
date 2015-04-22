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

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.claudia.FirewallingClient;
import com.telefonica.euro_iaas.paasmanager.dao.RuleDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.manager.RuleManager;
import com.telefonica.euro_iaas.paasmanager.model.Rule;

public class RuleManagerImpl implements RuleManager {

    private RuleDao ruleDao = null;
    private FirewallingClient firewallingClient = null;
    private static Logger log = LoggerFactory.getLogger(RuleManagerImpl.class);

    public Rule create(String region, String token, String vdc, Rule rule) throws InvalidEntityException,
            AlreadyExistsEntityException, InfrastructureException {
        log.info("Create rule " + rule.getFromPort() + " from security group " + rule.getIdParent());
        String idRule = firewallingClient.deployRule(region, token, vdc, rule);
        log.info("id rule " + idRule);
        rule.setIdRule(idRule);
        rule = ruleDao.create(rule);
        return rule;
    }

    public void destroy(String region, String token, String vdc, Rule rule) throws InvalidEntityException,
            InfrastructureException {
        log.info("Destroying rule " + rule.getFromPort() + " from security group " + rule.getIdParent());

        firewallingClient.destroyRule(region, token, vdc, rule);
        try {
            ruleDao.remove(rule);
        } catch (Exception e) {
            log.error("Error to remove the rule in BD " + e.getMessage());
            throw new InvalidEntityException(rule);
        }

    }

    public List<Rule> findAll() {
        return ruleDao.findAll();
    }

    public Rule load(String name) throws EntityNotFoundException {
        return ruleDao.load(name);
    }

    public void setRuleDao(RuleDao ruleDao) {
        this.ruleDao = ruleDao;
    }

    public void setFirewallingClient(FirewallingClient firewallingClient) {
        this.firewallingClient = firewallingClient;
    }

}
