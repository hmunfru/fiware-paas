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
package com.telefonica.euro_iaas.paasmanager.util.auth;

import net.sf.json.JSONObject;

public class OpenStackAccess {

    private String token;

    private String tenantId;

    private String tenantName;

    private JSONObject accessJSON;

    private String api;

    private OpenStackKeystone openStackKeystone;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public JSONObject getAccessJSON() {
        return accessJSON;
    }

    public void setAccessJSON(JSONObject accessJSON) {
        this.accessJSON = accessJSON;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public OpenStackKeystone getOpenStackKeystone() {
        return openStackKeystone;
    }

    public void setOpenStackKeystone(OpenStackKeystone openStackKeystone) {
        this.openStackKeystone = openStackKeystone;
    }
}
