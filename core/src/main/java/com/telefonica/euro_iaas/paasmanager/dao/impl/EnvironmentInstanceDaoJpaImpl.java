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

package com.telefonica.euro_iaas.paasmanager.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentInstanceDao;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentInstanceSearchCriteria;

@Transactional(propagation = Propagation.REQUIRED)
public class EnvironmentInstanceDaoJpaImpl extends AbstractBaseDao<EnvironmentInstance, String> implements
        EnvironmentInstanceDao {

    public List<EnvironmentInstance> findAll() {
        return super.findAll(EnvironmentInstance.class);
    }


    public EnvironmentInstance load(String name) throws EntityNotFoundException {
        return null;
    }

    public EnvironmentInstance load(String name, String vdc) throws EntityNotFoundException {

        try {
            return findByEnvironmentInstanceName(name, vdc);
        } catch (Exception e) {
            try {
                return findByEnvironmentInstanceNameNoTierInstances(name, vdc);
            } catch (Exception e2) {
                throw new EntityNotFoundException(EnvironmentInstance.class, name, e2);
            }
        }

    }

    public List<EnvironmentInstance> findByCriteria(EnvironmentInstanceSearchCriteria criteria) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria baseCriteria = session.createCriteria(EnvironmentInstance.class);

        if (!StringUtils.isEmpty(criteria.getVdc())) {
            baseCriteria.add(Restrictions.eq(InstallableInstance.VDC_FIELD, criteria.getVdc()));
        }
        if (!StringUtils.isEmpty(criteria.getEnvironmentName())) {
            baseCriteria.add(Restrictions.eq("blueprintName", criteria.getEnvironmentName()));
        }
        if (criteria.getEnvironment() != null) {
            baseCriteria.createAlias("environment", "env");
            baseCriteria.add(Restrictions.eq("env.name", criteria.getEnvironment().getName()));
        }

        List<EnvironmentInstance> environments = setOptionalPagination(criteria, baseCriteria).list();

        // TODO sarroyo: try to do this filter using hibernate criteria.
        if (criteria.getTierInstance() != null) {
            environments = filterByTierInstance(environments, criteria.getTierInstance());
        }
        return environments;
    }

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.dao.TierDao#findByTierId(java.lang .String)
     */
    private EnvironmentInstance findByEnvironmentInstanceName(String envInstanceName, String vdc)
            throws EntityNotFoundException {

        Query query = getEntityManager().createQuery(
                "select p from EnvironmentInstance" + " p where p.blueprintName = :blueprintName and p.vdc =:vdc");
        query.setParameter("blueprintName", envInstanceName);
        query.setParameter("vdc", vdc);
        EnvironmentInstance environmentInstance = null;
        try {
            environmentInstance = (EnvironmentInstance) query.getResultList().get(0);
            environmentInstance.getTierInstances().size();
        } catch (Exception e) {
            String message = " No EnvironmentInstance found in the database with tiers" + "with blueprintName: "
                    + envInstanceName + " and vdc " + vdc;
            throw new EntityNotFoundException(EnvironmentInstance.class, message, envInstanceName);
        }
        return environmentInstance;
    }

    private EnvironmentInstance findByEnvironmentInstanceNameNoTierInstances(String envInstanceName, String vdc)
            throws EntityNotFoundException {

        Query query = getEntityManager().createQuery(
                "select p from EnvironmentInstance" + " p where p.blueprintName = :blueprintName and p.vdc =:vdc");
        query.setParameter("blueprintName", envInstanceName);
        query.setParameter("vdc", vdc);
        EnvironmentInstance environmentInstance = null;
        try {
            environmentInstance = (EnvironmentInstance) query.getResultList().get(0);
            getEntityManager().flush();
        } catch (Exception e) {
            String message = " No EnvironmentInstance found in the database no tiers " + "with blueprintName: "
                    + envInstanceName;
            throw new EntityNotFoundException(EnvironmentInstance.class, message, envInstanceName);
        }
        return environmentInstance;
    }

    private List<EnvironmentInstance> filterByTierInstance(List<EnvironmentInstance> environmentInstances,
            TierInstance tierInstance) {
        List<EnvironmentInstance> envInstancesFiltered = new ArrayList<EnvironmentInstance>();
        for (int i = 0; i < environmentInstances.size(); i++) {
            List<TierInstance> tierInstances = environmentInstances.get(i).getTierInstances();
            for (int j = 0; j < tierInstances.size(); j++) {
                if (tierInstance.getName().equals(tierInstances.get(j).getName())) {
                    envInstancesFiltered.add(environmentInstances.get(i));
                }
            }
        }
        return envInstancesFiltered;
    }
}
