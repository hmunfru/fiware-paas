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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.EnvironmentDao;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentSearchCriteria;

@Transactional(propagation = Propagation.REQUIRED)
public class EnvironmentDaoJpaImpl extends AbstractBaseDao<Environment, String> implements EnvironmentDao {

    private static final String QUERY_LOAD_BY_TWO_FIELDS = "SELECT o FROM {0} o WHERE o.{1} = :{1} and o.{2} =:{2}";

    public List<Environment> findAll() {
        return super.findAll(Environment.class);
    }


    public Environment load(String envName, String vdc) throws EntityNotFoundException {
        return findByEnvironmentNameVdc(envName, vdc);
    }

    public List<Environment> findByCriteria(EnvironmentSearchCriteria criteria) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria baseCriteria = session.createCriteria(Environment.class);

        /*
         * if (criteria.getTier() != null) { baseCriteria.add(Restrictions.eq("tier", criteria.getTier())); }
         */

        List<Environment> environments = setOptionalPagination(criteria, baseCriteria).list();

        if (criteria.getTier() != null) {
            environments = filterByTier(environments, criteria.getTier());
        }

        if (criteria.getOrg() != null && criteria.getVdc() != null && criteria.getEnvironmentName() != null) {
            environments = filterByEnvironment(environments, criteria.getOrg(), criteria.getVdc(),
                    criteria.getEnvironmentName());
            return environments;
        }

        if (criteria.getOrg() != null && criteria.getVdc() != null) {
            environments = filterByOrgAndVdc(environments, criteria.getOrg(), criteria.getVdc());
            return environments;
        }

        if (criteria.getOrg() != null) {
            environments = filterByOrg(environments, criteria.getOrg());
        }

        return environments;
    }

    private List<Environment> filterByOrgAndVdc(List<Environment> environments, String org, String vdc) {
        List<Environment> result = new ArrayList<Environment>();
        for (Environment environment : environments) {
            if (environment.getVdc() == null)
                continue;
            if (environment.getOrg().equals(org) && environment.getVdc().equals(vdc)) {
                result.add(environment);
            }
        }
        return result;
    }

    private List<Environment> filterByOrg(List<Environment> environments, String org) {
        List<Environment> result = new ArrayList<Environment>();
        for (Environment environment : environments) {

            if (environment.getOrg().equals(org) && (environment.getVdc() == null || environment.getVdc().isEmpty())) {
                result.add(environment);
            }
        }
        return result;

    }

    private List<Environment> filterByEnvironment(List<Environment> environments, String org, String vdc,
            String environmentName) {
        List<Environment> result = new ArrayList<Environment>();
        for (Environment environment : environments) {
            if (environment.getOrg().equals(org) && environment.getVdc() != null && environment.getVdc().equals(vdc)
                    && environment.getName() != null && environment.getName().equals(environmentName)) {
                result.add(environment);
            }
        }
        return result;
    }

    private Environment findByEnvironmentNameVdc(String envName, String vdc) throws EntityNotFoundException {
        Query query = getEntityManager().createQuery(
                "select p from Environment p left join " + "fetch p.tiers where p.name = :name and p.vdc = :vdc");
        query.setParameter("name", envName);
        query.setParameter("vdc", vdc);
        Environment environment = null;
        try {
            environment = (Environment) query.getResultList().get(0);
        } catch (Exception e) {
            throw new EntityNotFoundException(Environment.class, "name", envName);
        }
        // return filterEqualTiers(environment);
        return environment;
    }

    /**
     * Filter the result by tier
     * 
     * @param environments
     * @return environments
     */
    private List<Environment> filterByTier(List<Environment> environments, Tier tierInput) {
        List<Environment> result = new ArrayList<Environment>();
        for (Environment environment : environments) {
            for (Tier tier : environment.getTiers()) {
                if (tier.getName().equals(tierInput.getName())) {
                    result.add(environment);
                }
            }

        }
        return result;
    }


	@Override
	public Environment load(String envName) throws EntityNotFoundException {
		 Query query = getEntityManager().createQuery(
	                "select p from Environment p left join " + "fetch p.tiers where p.name = :name and p.vdc = :vdc");
	        query.setParameter("name", envName);
	        query.setParameter("vdc", "");
	        Environment environment = null;
	        try {
	            environment = (Environment) query.getResultList().get(0);
	        } catch (Exception e) {
	            throw new EntityNotFoundException(Environment.class, "name", envName);
	        }
	        // return filterEqualTiers(environment);
	        return environment;
	}
}
