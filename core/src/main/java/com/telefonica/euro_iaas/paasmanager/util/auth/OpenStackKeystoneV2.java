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

import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.paasmanager.bean.OpenStackAccess;

/**
 * OpenStackKeystoneV2
 */
public class OpenStackKeystoneV2 implements OpenStackKeystone {
    /**
     * API version
     */
    public static final String VERSION = "v2.0";

    /**
     * The log.
     */
    private static Logger log = LoggerFactory.getLogger(OpenStackAuthenticationToken.class);

    /**
     * parse response for /auth/tokens (identity API v2)
     * 
     * @param openStackAccess
     * @param response
     * @param jsonObjectResponse
     */
    public void parseResponse(OpenStackAccess openStackAccess, Response response, JSONObject jsonObjectResponse) {
        if (jsonObjectResponse.containsKey("access")) {
            openStackAccess.setAccessJSON(jsonObjectResponse);

            JSONObject tokenObject = (JSONObject) ((JSONObject) jsonObjectResponse.get("access")).get("token");
            String token = (String) tokenObject.get("id");
            String tenantId = (String) ((JSONObject) tokenObject.get("tenant")).get("id");
            String tenantName = (String) ((JSONObject) tokenObject.get("tenant")).get("name");

            openStackAccess.setToken(token);
            openStackAccess.setTenantId(tenantId);
            openStackAccess.setTenantName(tenantName);
            log.info("generated new token for tenantId:" + tenantId + " with tenantName: " + tenantName);

        }
    }

    /**
     * Payload for identity API v2
     * 
     * @param user
     * @param password
     * @param tenant
     * @return
     */
    public String getPayload(String user, String password, String tenant) {
        return "{\"auth\":{\"tenantName\":\"" + tenant + "\",\"passwordCredentials\":{\"username\":\"" + user + "\","
                + "\"password\":\"" + password + "\"}}}";
    }

    /**
     * get version supported
     * 
     * @return
     */
    public String getVersion() {
        return VERSION;
    }

    /**
     * get keystone url
     * 
     * @return
     */
    public String getKeystoneURL(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            return baseUrl + "tokens";
        } else {
            return baseUrl + "/tokens";

        }
    }
}
