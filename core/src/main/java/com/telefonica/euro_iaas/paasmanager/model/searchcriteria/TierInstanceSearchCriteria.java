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

package com.telefonica.euro_iaas.paasmanager.model.searchcriteria;

import com.telefonica.fiware.commons.dao.AbstractSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;


/**
 * Provides some criteria to search TierInstance entities.
 * 
 * @author Jesus M. Movilla
 */
public class TierInstanceSearchCriteria extends AbstractSearchCriteria {

    private ProductInstance productInstance;
    private String vdc;
    private EnvironmentInstance environmentInstance;

    /**
     * Default constructor
     */
    public TierInstanceSearchCriteria() {
    }

    /**
     * @param page
     * @param pagesize
     * @param orderBy
     * @param orderType
     * @param productInstance
     * @param environment
     */
    public TierInstanceSearchCriteria(Integer page, Integer pageSize, String orderBy, String orderType,
            ProductInstance productInstance, EnvironmentInstance environmentInstance, String vdc) {
        super(page, pageSize, orderBy, orderType);
        this.productInstance = productInstance;
        this.environmentInstance = environmentInstance;
        this.vdc = vdc;
    }

    /**
     * @param orderBy
     * @param orderType
     * @param productInstance
     * @param environment
     */
    public TierInstanceSearchCriteria(String orderBy, String orderType, ProductInstance productInstance) {
        super(orderBy, orderType);
        this.productInstance = productInstance;

    }

    /**
     * @param page
     * @param pagesize
     * @param productInstance
     * @param environment
     */
    public TierInstanceSearchCriteria(Integer page, Integer pageSize, ProductInstance productInstance) {
        super(page, pageSize);
        this.productInstance = productInstance;
    }

    /**
     * @param instance
     * @param environment
     */
    public TierInstanceSearchCriteria(ProductInstance productInstance) {
        this.productInstance = productInstance;
    }

    /**
     * @return the productInstance
     */
    public ProductInstance getProductInstance() {
        return productInstance;
    }

    /**
     * @param ProductRelease
     *            the ProductRelease to set
     */
    public void setProductInstance(ProductInstance productInstance) {
        this.productInstance = productInstance;
    }

    public String getVdc() {
        return vdc;
    }

    public void setVdc(String vdc) {
        this.vdc = vdc;
    }

    public EnvironmentInstance getEnvironmentInstance() {
        return environmentInstance;
    }

    public void setEnvironmentInstance(EnvironmentInstance environmentInstance) {
        this.environmentInstance = environmentInstance;
    }

}
