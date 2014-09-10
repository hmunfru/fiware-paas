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

import com.telefonica.euro_iaas.commons.dao.AbstractSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;

/**
 * Provides some criteria to search Application Instance entities.
 * 
 * @author Jesus M. Movilla
 */
public class ApplicationInstanceSearchCriteria extends AbstractSearchCriteria {

    private ApplicationRelease applicationRelease;
    private String vdc;
    private List<Status> status;
    private String environmentInstance;
    private String applicationName;
    private String productInstanceName;

    /**
     * Default constructor.
     */
    public ApplicationInstanceSearchCriteria() {
    }

    /**
     * @param page
     * @param pageSize
     * @param orderBy
     * @param orderType
     * @param applicationRelease
     * @param vdc
     */
    public ApplicationInstanceSearchCriteria(Integer page, Integer pageSize, String orderBy, String orderType,
            List<Status> status, ApplicationRelease applicationRelease, String vdc, String environmentInstance) {
        super(page, pageSize, orderBy, orderType);
        this.applicationRelease = applicationRelease;
        this.vdc = vdc;
        this.environmentInstance = environmentInstance;
        this.status = status;
    }

    /**
     * @param page
     * @param pageSize
     * @param orderBy
     * @param orderType
     * @param applicationRelease
     * @param vdc
     */
    public ApplicationInstanceSearchCriteria(Integer page, Integer pageSize, String orderBy, String orderType,
            List<Status> status, ApplicationRelease applicationRelease, String vdc, String environmentInstance,
            String productInstanceName, String applicationName) {
        super(page, pageSize, orderBy, orderType);
        this.applicationRelease = applicationRelease;
        this.vdc = vdc;
        this.environmentInstance = environmentInstance;
        this.productInstanceName = productInstanceName;
        this.applicationName = applicationName;
        this.status = status;
    }

    /**
     * @param page
     * @param pageSize
     * @param applicationRelease
     * @param vdc
     */
    public ApplicationInstanceSearchCriteria(String orderBy, String orderType, List<Status> status,
            ApplicationRelease applicationRelease, String vdc, String environmentInstance) {
        super(orderBy, orderType);
        this.applicationRelease = applicationRelease;
        this.vdc = vdc;
        this.environmentInstance = environmentInstance;
        this.status = status;
    }

    /**
     * @param page
     * @param pageSize
     * @param applicationRelease
     * @param vdc
     */
    public ApplicationInstanceSearchCriteria(Integer page, Integer pageSize, List<Status> status,
            ApplicationRelease applicationRelease, String vdc, String environmentInstance) {
        super(page, pageSize);
        this.applicationRelease = applicationRelease;
        this.vdc = vdc;
        this.environmentInstance = environmentInstance;
        this.status = status;
    }

    /**
     * @param applicationRelease
     * @param vdc
     */
    public ApplicationInstanceSearchCriteria(List<Status> status, ApplicationRelease applicationRelease,
            String environmentInstance, String vdc) {
        this.status = status;
        this.applicationRelease = applicationRelease;
        this.environmentInstance = environmentInstance;
        this.vdc = vdc;
    }

    /**
     * @return the applicationRelease
     */
    public ApplicationRelease getApplicatonRelease() {
        return applicationRelease;
    }

    /**
     * @param applicationRelease
     *            the applicationRelease to set
     */
    public void setApplicatonRelease(ApplicationRelease applicationRelease) {
        this.applicationRelease = applicationRelease;
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

    /**
     * @return the environmentInstance
     */
    public String getEnvironmentInstance() {
        return environmentInstance;
    }

    /**
     * @param environmentInstance
     *            the vdc to set
     */
    public void setEnvironmentInstance(String environmentInstance) {
        this.environmentInstance = environmentInstance;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param applicationName
     *            the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * @return the applicationName
     */
    public String getProductInstanceName() {
        return productInstanceName;
    }

    /**
     * @param productInstanceName
     *            the applicationName to set
     */
    public void setProductInstanceName(String productInstanceName) {
        this.productInstanceName = productInstanceName;
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
}
