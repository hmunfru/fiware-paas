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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.fiware.commons.dao.AbstractBaseDao;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.SubNetworkDao;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.SubNetwork;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;

/**
 * @author Henar Munoz
 */
@Transactional(propagation = Propagation.REQUIRED)
public class SubNetworkDaoJpaImpl extends AbstractBaseDao<SubNetwork, String> implements SubNetworkDao {

    /**
     * find all networks.
     * 
     * @return network list
     */
    public List<SubNetwork> findAll() {
        return super.findAll(SubNetwork.class);
    }

    /**
     * Loads the subnet.
     */
    public SubNetwork load(String arg0) throws EntityNotFoundException {
        return null;
    }
    
    public SubNetwork load(String name, String vdc, String region) throws EntityNotFoundException {
    	return findByNetworkName(name, vdc,  region);
    }
    private SubNetwork findByNetworkName(String name, String vdc, String region) throws EntityNotFoundException {
        Query query = getEntityManager().createQuery(
                "select p from SubNetwork p where p.name = :name and p.vdc = :vdc and p.region = :region");
        query.setParameter("name", name);
        query.setParameter("vdc", vdc);
        query.setParameter("region", region);
        SubNetwork subNetwork= null;
        try {
        	subNetwork = (SubNetwork) query.getSingleResult();
        } catch (NoResultException e) {
            String message = " No subNetwork found in the database with id: " + name + " vdc " + vdc + " region " + region + " Exception: "
                    + e.getMessage();
            throw new EntityNotFoundException(SubNetwork.class, "name", name);
        }
        return subNetwork;
    }

}
