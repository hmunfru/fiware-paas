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

import java.util.List;

import com.telefonica.fiware.commons.dao.AbstractSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;

/**
 * @author jesus.movilla
 */
public class EnvironmentInstanceSearchCriteria extends AbstractSearchCriteria {

    private List<Status> status;
    private String vdc;
    private String environmentName;
    /**
     * The environment
     */
    private Environment environment;

    /**
     * The tierInstance
     */
    private TierInstance tierInstance;

    /**
     * Default constructor
     */
    public EnvironmentInstanceSearchCriteria() {
    }

    /**
     * @param page
     * @param pagesize
     * @param orderBy
     * @param orderType
     * @param environment
     * @param applicationInstance
     */
    public EnvironmentInstanceSearchCriteria(Integer page, Integer pageSize, String orderBy, String orderType,
            List<Status> status, String vdc, Environment environment, TierInstance tierInstance) {
        super(page, pageSize, orderBy, orderType);
        this.status = status;
        this.vdc = vdc;
        this.tierInstance = tierInstance;
        this.environment = environment;
    }

    /**
     * @return the status
     */
    public List<Status> getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(List<Status> status) {
        this.status = status;
    }

    /**
     * @return the vdc
     */
    public String getVdc() {
        return vdc;
    }

    /**
     * @param vdc
     *            the vdc to set
     */
    public void setVdc(String vdc) {
        this.vdc = vdc;
    }

    public String getEnvironmentName() {
        return this.environmentName;
    }

    /**
     * @param vdc
     *            the vdc to set
     */
    public void setEnviromentName(String environmentName) {
        this.environmentName = environmentName;
    }

    /**
     * @param environment
     */
    public EnvironmentInstanceSearchCriteria(Environment environment) {
        this.environment = environment;
    }

    /**
     * @param tierInstance
     */
    public EnvironmentInstanceSearchCriteria(TierInstance tierInstance) {
        this.tierInstance = tierInstance;
    }

    /**
     * @return the environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * @param environment
     *            the environment to set
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * @return the tierInstance
     */
    public TierInstance getTierInstance() {
        return tierInstance;
    }

    /**
     * @param applicationInstance
     *            the applicationInstance to set
     */
    public void setTierInstance(TierInstance tierInstance) {
        this.tierInstance = tierInstance;
    }

    @Override
    public String toString() {
        return "EnvironmentInstanceSearchCriteria [Environment=" + environment + ", tierInstance=" + tierInstance + "]";
    }

}
