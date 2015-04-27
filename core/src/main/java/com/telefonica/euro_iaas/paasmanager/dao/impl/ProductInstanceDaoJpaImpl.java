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
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.telefonica.fiware.commons.dao.AbstractBaseDao;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.ProductInstanceDao;
import com.telefonica.euro_iaas.paasmanager.exception.NotUniqueResultException;
import com.telefonica.euro_iaas.paasmanager.exception.PaasManagerServerRuntimeException;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ProductInstanceSearchCriteria;

@Transactional(propagation = Propagation.REQUIRED)
public class ProductInstanceDaoJpaImpl extends AbstractBaseDao<ProductInstance, String> implements ProductInstanceDao {

    public List<ProductInstance> findAll() {
        return super.findAll(ProductInstance.class);
    }

    public ProductInstance load(Long arg0) throws EntityNotFoundException {
        return super.loadByField(ProductInstance.class, "id", arg0);
    }

    public ProductInstance load(String name) throws EntityNotFoundException {
        return super.loadByField(ProductInstance.class, "name", name);
    }

    public List<ProductInstance> findByCriteria(ProductInstanceSearchCriteria criteria) {
        Session session = (Session) getEntityManager().getDelegate();
        Criteria baseCriteria = session.createCriteria(ProductInstance.class);

        List<ProductInstance> productInstance = setOptionalPagination(criteria, baseCriteria).list();

        if (criteria.getTierInstance() != null) {
            productInstance = filterByTierInstance(productInstance, criteria.getTierInstance());
        }

        return productInstance;
    }

    private List<ProductInstance> filterByTierInstance(List<ProductInstance> productInstances, TierInstance tierInstance) {
        List<ProductInstance> result = new ArrayList<ProductInstance>();

        for (ProductInstance productInstance : productInstances) {
            if (tierInstance.getProductInstances().contains(productInstance))
                result.add(productInstance);
        }
        return result;
    }

    public Criterion getVMCriteria(Criteria baseCriteria, VM vm) {
        if (!StringUtils.isEmpty(vm.getFqn()) && !StringUtils.isEmpty(vm.getIp())
                && !StringUtils.isEmpty(vm.getDomain()) && !StringUtils.isEmpty(vm.getHostname())) {
            return Restrictions.eq(ProductInstance.VM_FIELD, vm);
        } else if (!StringUtils.isEmpty(vm.getFqn())) {
            return Restrictions.eq("vm.fqn", vm.getFqn());
        } else if (!StringUtils.isEmpty(vm.getIp())) {
            return Restrictions.eq("vm.ip", vm.getIp());
        } else if (!StringUtils.isEmpty(vm.getDomain()) && !StringUtils.isEmpty(vm.getHostname())) {
            return Restrictions.and(Restrictions.eq("vm.hostname", vm.getHostname()),
                    Restrictions.eq("vm.domain", vm.getDomain()));
        } else {
            throw new PaasManagerServerRuntimeException("Invalid VM while finding products by criteria");
        }
    }

    public ProductInstance findUniqueByCriteria(ProductInstanceSearchCriteria criteria) throws NotUniqueResultException {
        List<ProductInstance> instances = findByCriteria(criteria);
        if (instances.size() > 1) {
            throw new NotUniqueResultException();
        }
        if (instances.size() == 0)
            throw new NoSuchElementException();

        return instances.iterator().next();
    }


}
