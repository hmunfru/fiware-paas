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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.ApplicationReleaseDao;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.Artifact;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ApplicationReleaseSearchCriteria;

/**
 * Class Application Release Dao.
 */
@Transactional(propagation = Propagation.REQUIRED)
public class ApplicationReleaseDaoJpaImpl extends AbstractBaseDao<ApplicationRelease, String> implements
        ApplicationReleaseDao {

    /**
     * Get the list of all application releases.
     * @return
     */
    public List<ApplicationRelease> findAll() {
        return super.findAll(ApplicationRelease.class);
    }

    /**
     * Get an application release given its name.
     * @param arg0
     * @return
     * @throws EntityNotFoundException
     */
    public ApplicationRelease load(String arg0) throws EntityNotFoundException {
        return super.loadByField(ApplicationRelease.class, "name", arg0);
    }

    /**
     * Get the list of application releases given a search criteria.
     * @param criteria
     *            the search criteria
     * @return
     */
    public List<ApplicationRelease> findByCriteria(ApplicationReleaseSearchCriteria criteria) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria baseCriteria = session.createCriteria(ApplicationRelease.class);

        List<ApplicationRelease> applicationReleases = setOptionalPagination(criteria, baseCriteria).list();

        if (criteria.getArtifact() != null) {
            applicationReleases = filterByArtifact(applicationReleases, criteria.getArtifact());
        }

        return applicationReleases;
    }

    /**
     * Filter the result by application release.
     * 
     * @param applicationReleases
     * @param artifact
     * @return
     */
    private List<ApplicationRelease> filterByArtifact(List<ApplicationRelease> applicationReleases, Artifact artifact) {
        List<ApplicationRelease> result = new ArrayList<ApplicationRelease>();
        for (ApplicationRelease applicationRelease : applicationReleases) {
            if (applicationRelease.getArtifacts().contains(artifact)) {
                result.add(applicationRelease);
            }
        }
        return result;
    }

}
