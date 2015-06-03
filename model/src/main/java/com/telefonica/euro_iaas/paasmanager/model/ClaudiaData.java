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

package com.telefonica.euro_iaas.paasmanager.model;

import com.telefonica.euro_iaas.paasmanager.bean.PaasManagerUser;

/**
 * Defines the data to be used with Claudia.
 *
 * @author jesus.movilla
 */
public class ClaudiaData {

    /**
     * The org.
     */
    private final String org;
    /**
     * The vdc.
     */
    private final String vdc;
    /**
     * The service.
     */
    private final String service;

    /**
     * The user. *
     */
    private PaasManagerUser user;

    /**
     * The replica. *
     */
    private String replica;

    /**
     * @param org
     * @param vdc
     * @param service
     */
    public ClaudiaData(String org, String vdc, String service) {
        this.org = org;
        this.vdc = vdc;
        this.service = service;
    }

    /**
     * @return the org
     */
    public String getOrg() {
        return org;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @return the user
     */
    public PaasManagerUser getUser() {
        return user;
    }

    /**
     * @return the vdc
     */
    public String getVdc() {
        return vdc;
    }

    /**
     * @param user the user to set
     */
    public void setUser(PaasManagerUser user) {
        this.user = user;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[ClaudiaData]");
        sb.append("[org = ").append(this.org).append("]");
        sb.append("[vdc = ").append(this.vdc).append("]");
        sb.append("[service = ").append(this.service).append("]");
        sb.append("[user = ").append(this.user).append("]");
        sb.append("[replica = ").append(this.replica).append("]");
        sb.append("]");
        return sb.toString();
    }


}
