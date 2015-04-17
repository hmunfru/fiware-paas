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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;

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
        openStackAccess.setOpenStackKeystone(this);
        if (jsonObjectResponse.containsKey("access")) {
            openStackAccess.setAccessJSON(jsonObjectResponse);

            JSONObject tokenObject = (JSONObject) ((JSONObject) jsonObjectResponse.get("access")).get("token");
            String token = (String) tokenObject.get("id");
            String tenantId = (String) ((JSONObject) tokenObject.get("tenant")).get("id");
            String tenantName = (String) ((JSONObject) tokenObject.get("tenant")).get("name");

            openStackAccess.setToken(token);
            openStackAccess.setTenantId(tenantId);
            openStackAccess.setTenantName(tenantName);
            openStackAccess.setApi(VERSION);
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

    public String parseEndpoint(JSONObject jsonObject, String type, String regionName) throws OpenStackException {
        String url = null;
        Map<String, String> urlMap = new HashMap<String, String>();
        if (jsonObject.containsKey("access")) {
            JSONArray servicesArray = jsonObject.getJSONObject("access").getJSONArray("serviceCatalog");

            boolean notFound = true;
            Iterator it = servicesArray.iterator();
            JSONObject serviceJSON;
            while (notFound && it.hasNext()) {

                serviceJSON = JSONObject.fromObject(it.next());
                String name1 = serviceJSON.get("type").toString();

                if (type.equals(name1)) {
                    JSONArray endpointsArray = serviceJSON.getJSONArray("endpoints");
                    Iterator it2 = endpointsArray.iterator();

                    while (notFound && it2.hasNext()) {
                        JSONObject endpointJson = JSONObject.fromObject(it2.next());

                        String regionName1 = endpointJson.get("region").toString();
                        url = endpointJson.get("publicURL").toString();
                        if (regionName.equals(regionName1)) {
                            notFound = false;
                        }
                        urlMap.put(regionName1, url);

                    }

                }
            }
            if (!notFound) {
                return url;
            }

        }

        String defaultRegion = parseRegionNames(jsonObject, "nova").get(0);
        return urlMap.get(defaultRegion);
    }

    public List<String> parseRegionNames(JSONObject jsonObject, String name) {

        List<String> names = new ArrayList<String>(2);
        if (jsonObject.containsKey("access")) {

            JSONArray servicesArray = jsonObject.getJSONObject("access").getJSONArray("serviceCatalog");

            boolean notFound = true;
            Iterator it = servicesArray.iterator();
            JSONObject serviceJSON;
            while (notFound && it.hasNext()) {

                serviceJSON = JSONObject.fromObject(it.next());
                String name1 = serviceJSON.get("name").toString();

                if (name.equals(name1)) {
                    JSONArray endpointsArray = serviceJSON.getJSONArray("endpoints");

                    Iterator it2 = endpointsArray.iterator();
                    while (it2.hasNext()) {
                        JSONObject endpointJson = JSONObject.fromObject(it2.next());

                        String regionName1 = endpointJson.get("region").toString();
                        if (!names.contains(regionName1)) {
                            names.add(regionName1);
                        }

                    }
                    notFound = false;

                }
            }
        }
        return names;
    }

    @Override
    public String[] checkToken(String token, String tenantId, Response response) {
        if (response.getStatus() == 200) {
            JSONObject jsonObject = JSONObject.fromObject(response.readEntity(String.class));
            jsonObject = (JSONObject) jsonObject.get("access");
            JSONObject tokenJSONObject = (JSONObject) jsonObject.get("token");

            String responseTenantId = (String) ((JSONObject) tokenJSONObject.get("tenant")).get("id");
            String responseTenantName = (String) ((JSONObject) tokenJSONObject.get("tenant")).get("name");
            JSONObject userObject = (JSONObject) jsonObject.get("user");
            String responseUserName = (String) (userObject.get("username"));

            if (!tenantId.equals(responseTenantId)) {
                throw new AuthenticationServiceException("Token " + token + " not valid for the tenantId provided:"
                        + tenantId);
            }

            return new String[] { responseUserName, responseTenantName };

        } else {
            log.warn("response status:" + response.getStatus());

            if (response.getStatus() == 401) {
                throw new BadCredentialsException("Invalid token");
            }

            throw new AuthenticationServiceException("Invalid token");
        }
    }
}
