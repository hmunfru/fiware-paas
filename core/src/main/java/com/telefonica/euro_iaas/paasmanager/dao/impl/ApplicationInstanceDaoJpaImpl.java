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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.ApplicationInstanceDao;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ApplicationInstanceSearchCriteria;

/**
 * Application Instance transaction class to DB.
 */
@Transactional(propagation = Propagation.REQUIRED)
public class ApplicationInstanceDaoJpaImpl extends AbstractBaseDao<ApplicationInstance, String> implements
        ApplicationInstanceDao {

    /**
     * Get the list of all application instances.
     * @return
     */
    public List<ApplicationInstance> findAll() {
        return super.findAll(ApplicationInstance.class);
    }

    /**
     * Get an application instance (not implemented).
     * @param arg0
     * @return
     * @throws EntityNotFoundException
     */
    public ApplicationInstance load(String arg0) throws EntityNotFoundException {
        //return super.loadByField(ApplicationInstance.class, "name", arg0);
        return null;
    }

    /**
     * Get an application instance given its name and vdc.
     * @param name
     * @param vdc
     * @return
     * @throws EntityNotFoundException
     */
    public ApplicationInstance load(String name, String vdc) throws EntityNotFoundException {
        Query query = getEntityManager().createQuery(
                "select p from ApplicationInstance p where p.name = :name and p.vdc = :vdc");
        query.setParameter("name", name);

        query.setParameter("vdc", vdc);
        if (vdc == null) {

            query.setParameter("vdc", "");
        } else {
            query.setParameter("vdc", vdc);
        }
        ApplicationInstance app = null;
        try {
            app = (ApplicationInstance) query.getSingleResult();
        } catch (NoResultException e) {
            String message = " No app found in the database with id: "
                    + name + " and vdc " + vdc + " Exception: " + e.getMessage();

            throw new EntityNotFoundException(Network.class, "name", name);
        }
        return app;
    }

    /**
     * Get a list of application instances given a search criteria.
     * @param criteria
     *            the search criteria
     * @return
     */
    public List<ApplicationInstance> findByCriteria(ApplicationInstanceSearchCriteria criteria) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria baseCriteria = session.createCriteria(ApplicationInstance.class);

        if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
            Criterion statusCr = null;
            for (Status status : criteria.getStatus()) {
                statusCr = addStatus(statusCr, status);
            }
            baseCriteria.add(statusCr);
        }

        if (!StringUtils.isEmpty(criteria.getVdc())) {
            baseCriteria.add(Restrictions.eq(ApplicationInstance.VDC_FIELD, criteria.getVdc()));
        }


        if (!StringUtils.isEmpty(criteria.getApplicationName())) {
            baseCriteria.add(Restrictions.eq(ApplicationInstance.APP_FIELD, criteria.getApplicationName()));
        }


        List<ApplicationInstance> applicationInstances = setOptionalPagination(criteria, baseCriteria).list();

        // TODO sarroyo: try to do this filter using hibernate criteria.
        if (criteria.getApplicatonRelease() != null) {
            applicationInstances = filterByApplicationRelease(applicationInstances, criteria.getApplicatonRelease());
        }

        applicationInstances = filterByVDCandEnvironmentInstance(applicationInstances, criteria.getVdc(),
                criteria.getEnvironmentInstance());

        return applicationInstances;
    }

    /**
     * Filter the result by product instance.
     *
     * @param applications
     * @param product
     * @return
     */
    private List<ApplicationInstance> filterByApplicationRelease(List<ApplicationInstance> applicationInstances,
                                                                 ApplicationRelease applicationRelease) {
        List<ApplicationInstance> result = new ArrayList<ApplicationInstance>();
        for (ApplicationInstance applicationInstance : applicationInstances) {
            if (applicationInstance.getApplicationRelease().getId().equals(applicationRelease.getId())) {
                result.add(applicationInstance);
            }
        }
        return result;
    }

    /**
     * Filter the result by product instance.
     *
     * @param applications
     * @param product
     * @return
     */
    private List<ApplicationInstance> filterByVDCandEnvironmentInstance(List<ApplicationInstance> applicationInstances,
                                                                        String vdc, String environmentInstanceName) {
        List<ApplicationInstance> result = new ArrayList<ApplicationInstance>();
        for (ApplicationInstance applicationInstance : applicationInstances) {
            if (applicationInstance.getEnvironmentInstance().getName().equals(environmentInstanceName)
                    && applicationInstance.getVdc().equals(vdc)) {
                result.add(applicationInstance);
            }
        }
        return result;
    }

    private Criterion addStatus(Criterion statusCr, Status status) {
        SimpleExpression expression = Restrictions.eq("status", status);
        if (statusCr == null) {
            statusCr = expression;
        } else {
            statusCr = Restrictions.or(statusCr, expression);
        }
        return statusCr;
    }

}
