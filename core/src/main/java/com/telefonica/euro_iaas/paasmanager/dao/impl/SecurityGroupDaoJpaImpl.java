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

import com.telefonica.euro_iaas.commons.dao.AbstractBaseDao;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.SecurityGroupDao;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;

/**
 * @author Henar
 */
@Transactional(propagation = Propagation.REQUIRED)
public class SecurityGroupDaoJpaImpl extends AbstractBaseDao<SecurityGroup, String> implements SecurityGroupDao {

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.commons.dao.BaseDAO#findAll()
     */
    public List<SecurityGroup> findAll() {
        // TODO Auto-generated method stub
        return super.findAll(SecurityGroup.class);
    }

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.commons.dao.BaseDAO#load(java.io.Serializable)
     */
    public SecurityGroup load(String arg0) throws EntityNotFoundException {
        return super.loadByField(SecurityGroup.class, "name", arg0);
    }

    public SecurityGroup loadWithRules(String name) throws EntityNotFoundException {

        Query query = getEntityManager().createQuery(
                "select p from SecurityGroup p left join " + " fetch p.rules where p.name = :name");
        query.setParameter("name", name);
        SecurityGroup securityGroup = null;
        try {
        	securityGroup = (SecurityGroup) query.getResultList().get(0);
        } catch (Exception e) {
            String message = " No SecurityGroup found in the database with name: " + name + " Exception: "
                    + e.getMessage();
            throw new EntityNotFoundException(SecurityGroup.class, "name", name);
        }
        return securityGroup;
    }
    public SecurityGroup updateSecurityGroupId(String idSecurityGroup, SecurityGroup securityGroup)
            throws InvalidEntityException {
        Query query = getEntityManager().createQuery(
                "UPDATE SecurityGroup sg " + "SET sg.idSecurityGroup= :idSecurityGroup" + "  where sg.name = :name");
        query.setParameter("idSecurityGroup", idSecurityGroup);
        query.setParameter("name", securityGroup.getName());
        SecurityGroup secGroup = null;
        try {
            query.executeUpdate();
            secGroup = load(securityGroup.getName());
        } catch (NoResultException e) {
            throw new InvalidEntityException(securityGroup);
        } catch (EntityNotFoundException e) {
            throw new InvalidEntityException(securityGroup);
        }
        return secGroup;
    }
}
