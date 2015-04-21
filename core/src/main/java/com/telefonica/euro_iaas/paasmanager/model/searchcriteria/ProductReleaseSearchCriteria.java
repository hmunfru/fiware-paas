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

/**
 * Provides some criteria to search ProductInstance entities.
 * 
 * @author Jesus M. Movilla
 */
public class ProductReleaseSearchCriteria extends AbstractSearchCriteria {

    /**
     * The productName.
     */
    private String productName;

    /**
     * The osType.
     */
    private String osType;

    /**
     * Default constructor
     */
    public ProductReleaseSearchCriteria() {
    }

    /**
     * @param page
     * @param pagesize
     * @param orderBy
     * @param orderType
     * @param productName
     */
    public ProductReleaseSearchCriteria(Integer page, Integer pageSize, String orderBy, String orderType,
            String productName) {
        super(page, pageSize, orderBy, orderType);
        this.productName = productName;
    }

    /**
     * @param orderBy
     * @param orderType
     * @param productName
     */
    public ProductReleaseSearchCriteria(String orderBy, String orderType, String productName) {
        super(orderBy, orderType);
        this.productName = productName;
    }

    /**
     * @param page
     * @param pagesize
     * @param product
     */
    public ProductReleaseSearchCriteria(Integer page, Integer pageSize, String productName) {
        super(page, pageSize);
        this.productName = productName;
    }

    /**
     * @param vm
     */
    public ProductReleaseSearchCriteria(String productName) {
        this.productName = productName;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the osType
     */
    public String getOSType() {
        return osType;
    }

    /**
     * @param osType
     *            the osTypeto set
     */
    public void setOSType(String osType) {
        this.osType = osType;
    }
}
