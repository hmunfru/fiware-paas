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
import com.telefonica.euro_iaas.paasmanager.dao.SubNetworkInstanceDao;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;

/**
 * @author Henar Munoz
 */
@Transactional(propagation = Propagation.REQUIRED)
public class SubNetworkInstanceDaoJpaImpl extends AbstractBaseDao<SubNetworkInstance, String> implements
        SubNetworkInstanceDao {

    /**
     * find all networks.
     * 
     * @return network list
     */
    public List<SubNetworkInstance> findAll() {
        return super.findAll(SubNetworkInstance.class);
    }

    /**
     * Loads the subnet.
     */
    public SubNetworkInstance load(String name, String vdc, String region) throws EntityNotFoundException {
        return findByNetworkInstanceName(name, vdc, region);
    }

    public boolean exists(String name, String vdc, String region) {
        try {
        	findByNetworkInstanceName (name, vdc, region);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
    
    private SubNetworkInstance findByNetworkInstanceName(String name, String vdc, String region) throws EntityNotFoundException {
        Query query = getEntityManager().createQuery(
                "select p from SubNetworkInstance p where p.name = :name and p.vdc = :vdc and p.region = :region");
        query.setParameter("name", name);
        query.setParameter("vdc", vdc);
        query.setParameter("region", region);
        SubNetworkInstance subNetworkInstance = null;
        try {
        	subNetworkInstance = (SubNetworkInstance) query.getSingleResult();
        } catch (NoResultException e) {
            String message = " No subNetworkInstance found in the database with id: " + name + " Exception: "
                    + e.getMessage();
            throw new EntityNotFoundException(NetworkInstance.class, "name", name);
        }
        return subNetworkInstance;
    }

	@Override
	public SubNetworkInstance load(String arg0) throws EntityNotFoundException {
		return null;
	}

}
