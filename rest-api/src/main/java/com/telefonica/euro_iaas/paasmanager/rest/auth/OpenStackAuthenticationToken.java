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

package com.telefonica.euro_iaas.paasmanager.rest.auth;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.paasmanager.bean.OpenStackAccess;
import com.telefonica.euro_iaas.paasmanager.rest.exception.AuthenticationConnectionException;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Class to obtain a valid token from the OpenStack.
 * 
 * @author fernandolopezaguilar
 */
public class OpenStackAuthenticationToken {

    /**
     * The url of the keystone service.
     */
    private String url;
    /**
     * The tenant name.
     */
    private String tenant;
    /**
     * The user of the keystone admin.
     */
    private String user;
    /**
     * The pass of the keystone admin.
     */
    private String pass;

    /**
     * The log.
     */
    private static Logger log = LoggerFactory.getLogger(OpenStackAuthenticationToken.class);

    /**
     * The default constructor of the class OpenStackAuthenticationToken.
     */
    OpenStackAuthenticationToken(SystemPropertiesProvider systemPropertiesProvider) {
        url = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL) + "tokens";

        user = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_USER);

        pass = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_PASS);

        tenant = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_TENANT);
    }

    /**
     * Request to OpenStack a valid token/tenantId.
     * 
     * @return The new credential (tenant id and token).
     */
    public OpenStackAccess getAdminCredentials(Client client) {

        OpenStackAccess openStackAccess = new OpenStackAccess();

        log.info("generate new valid token for admin");

        Response response = null;
        try {

            WebTarget wr = client.target(url);

            String payload = "{\"auth\": {\"tenantName\": \"" + tenant + "\", \""
                    + "passwordCredentials\":{\"username\": \"" + user + "\"," + " \"password\": \"" + pass + "\"}}}";

            Invocation.Builder builder = wr.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);

            response = builder.post(Entity.entity(payload, MediaType.APPLICATION_JSON));

            if (response.getStatus() == 200) {

                JSONObject jsonObject = JSONObject.fromObject(response.readEntity(String.class));
                jsonObject = (JSONObject) jsonObject.get("access");
                openStackAccess.setAccessJSON(jsonObject);

                if (jsonObject.containsKey("token")) {

                    JSONObject tokenObject = (JSONObject) jsonObject.get("token");
                    String token = (String) tokenObject.get("id");
                    String tenantId = (String) ((JSONObject) tokenObject.get("tenant")).get("id");

                    openStackAccess.setToken(token);
                    openStackAccess.setTenantId(tenantId);
                    log.info("generated new token for tenantId:" + tenantId);

                }
            } else {
                String exceptionMessage = "Failed : HTTP error code : (" + url + ")" + response.getStatus()
                        + " message: " + response;
                log.error(exceptionMessage);
                throw new AuthenticationConnectionException(exceptionMessage);

            }
        } catch (Exception ex) {
            throw new AuthenticationConnectionException("Error in authentication: " + ex);
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return openStackAccess;

    }

    /**
     * get keystone url
     * 
     * @return
     */
    public String getKeystoneURL() {
        return this.url;
    }
}
