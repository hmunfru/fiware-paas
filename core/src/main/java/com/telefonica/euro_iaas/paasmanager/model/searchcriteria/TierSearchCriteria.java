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
import com.telefonica.euro_iaas.paasmanager.model.Environment;

/**
 * Provides some criteria to search TierInstance entities.
 * 
 * @author Jesus M. Movilla
 */
public class TierSearchCriteria extends AbstractSearchCriteria {

    private Environment environment;

    private String name;
    private String environmentName;
    private String vdc;

    /**
     * Default constructor
     */
    public TierSearchCriteria() {
    }

    /**
     * @param page
     * @param pagesize
     * @param orderBy
     * @param orderType
     * @param vdc
     * @param name
     * @param environmentName
     * @param environment
     */
    public TierSearchCriteria(Integer page, Integer pageSize, String orderBy, String orderType, String vdc,
            String name, String environmentName, Environment environment) {
        super(page, pageSize, orderBy, orderType);
        this.vdc = vdc;
        this.environmentName = environmentName;
        this.vdc = vdc;
        this.environment = environment;

    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVdc() {
        return vdc;
    }

    public void setVdc(String vdc) {
        this.vdc = vdc;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

}
