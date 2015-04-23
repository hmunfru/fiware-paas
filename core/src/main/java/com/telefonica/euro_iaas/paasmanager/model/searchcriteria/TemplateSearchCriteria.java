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
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;

/**
 * @author jesus.movilla
 */
public class TemplateSearchCriteria extends AbstractSearchCriteria {

    private TierInstance tierInstance;

    /**
     * Default constructor
     */
    public TemplateSearchCriteria() {
    }

    /**
     * @param page
     * @param pagesize
     * @param orderBy
     * @param orderType
     * @param tierInstance
     */
    public TemplateSearchCriteria(Integer page, Integer pageSize, String orderBy, String orderType,
            ProductRelease productRelease) {
        super(page, pageSize, orderBy, orderType);
        this.tierInstance = tierInstance;
    }

    /**
     * @param orderBy
     * @param orderType
     * @param tierInstance
     */
    public TemplateSearchCriteria(String orderBy, String orderType, TierInstance tierInstance) {
        super(orderBy, orderType);
        this.tierInstance = tierInstance;
    }

    /**
     * @param page
     * @param pagesize
     * @param tierInstance
     */
    public TemplateSearchCriteria(Integer page, Integer pageSize, TierInstance tierInstance) {
        super(page, pageSize);
        this.tierInstance = tierInstance;
    }

    /**
     * @param productRelease
     */
    public TemplateSearchCriteria(TierInstance tierInstance) {
        this.tierInstance = tierInstance;
    }

    /**
     * @return the tierInstance
     */
    public TierInstance getTierInstance() {
        return tierInstance;
    }

    /**
     * @param TierInstance
     *            the tierInstance to set
     */
    public void setTierInstance(TierInstance tierInstance) {
        this.tierInstance = tierInstance;
    }

}
