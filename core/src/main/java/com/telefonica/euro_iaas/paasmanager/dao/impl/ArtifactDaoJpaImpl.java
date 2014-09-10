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

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.ArtifactDao;
import com.telefonica.euro_iaas.paasmanager.model.Artifact;

/**
 * Defines the methods needed to persist/to manage Artifact objects.
 * 
 * @author Jesus M. Movilla.
 */

@Transactional(propagation = Propagation.REQUIRED)
public class ArtifactDaoJpaImpl extends AbstractBaseDao<Artifact, String> implements ArtifactDao {

    /**
     * Find all the Artifacts in paas-manager database.
     * @return artifacts, the list of artifacts
     */
    public List<Artifact> findAll() {
        return super.findAll(Artifact.class);
    }

    /**
     * Find an artifact by name-searching.
     * @param name the name of the artifact
     * @return artifact, the artifact
     */
    public Artifact load(String name) throws EntityNotFoundException {
        return super.loadByField(Artifact.class, "name", name);
    }

  
}
