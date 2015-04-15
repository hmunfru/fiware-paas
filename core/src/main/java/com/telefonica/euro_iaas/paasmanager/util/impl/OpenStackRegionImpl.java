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

package com.telefonica.euro_iaas.paasmanager.util.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.paasmanager.bean.OpenStackAccess;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.util.OpenStackRegion;
import com.telefonica.euro_iaas.paasmanager.util.PoolHttpClient;
import com.telefonica.euro_iaas.paasmanager.util.RegionCache;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;
import com.telefonica.euro_iaas.paasmanager.util.TokenCache;
import com.telefonica.euro_iaas.paasmanager.util.auth.OpenStackAuthenticationToken;

/**
 * This class implements OpenStackRegion interface.<br>
 * {@inheritDoc}
 */
public class OpenStackRegionImpl implements OpenStackRegion {

    private Client client;

    private RegionCache regionCache;

    private TokenCache tokenCache;

    private OpenStackAuthenticationToken openStackAuthenticationToken;

    private HttpClientConnectionManager httpConnectionManager;

    /**
     * the properties configuration.
     */
    private SystemPropertiesProvider systemPropertiesProvider;

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(OpenStackRegionImpl.class);

    /**
     * Default constructor. Configure caches
     */
    public OpenStackRegionImpl() {

        client = PoolHttpClient.getInstance(httpConnectionManager).getClient();
        regionCache = new RegionCache();

        tokenCache = new TokenCache();
    }

    @Override
    public String getEndPointByNameAndRegionName(String type, String regionName, String token)
            throws OpenStackException {

        String url = regionCache.getUrl(regionName, type);

        if (url != null) {
            log.debug("Get url for sdc in region " + url);
            return url;
        } else {
            OpenStackAccess openStackAccess = this.getTokenAdmin();
            String responseJSON = getJSONWithEndpoints(token, openStackAccess.getToken());

            String result = parseEndpoint(token, responseJSON, type, regionName);
            if (result == null) {
                throw new OpenStackException("region not found");
            }
            regionCache.putUrl(regionName, type, result);

            return result;
        }
    }

    public OpenStackAccess getTokenAdmin() throws OpenStackException {

        OpenStackAccess openStackAccess;

        openStackAccess = tokenCache.getAdmin();

        if (openStackAccess == null) {

            if (openStackAuthenticationToken == null) {
                openStackAuthenticationToken = new OpenStackAuthenticationToken(systemPropertiesProvider);
            }
            openStackAccess = openStackAuthenticationToken.getAdminCredentials(client);
            tokenCache.putAdmin(openStackAccess);
        }
        return openStackAccess;

    }

    @Override
    public String getNovaEndPoint(String regionName, String token) throws OpenStackException {

        String url = getEndPointByNameAndRegionName("compute", regionName, token);
        log.debug("getNovaEndPoint " + regionName + " " + token + " " + url);

        Integer index = url.lastIndexOf("/");
        url = url.substring(0, index + 1);

        return url;

    }

    @Override
    public String getPuppetMasterEndPoint(String regionName, String token) throws OpenStackException {
        String url;
        try {
            log.info("Get url for puppet for default region " + regionName);
            url = getEndPointByNameAndRegionName("puppetmaster", regionName, token);
        } catch (OpenStackException e) {
            String msn = "It is not possible to obtain the Puppet Master endpoint";
            log.info(msn);
            throw new OpenStackException(msn);

        }
        return url;
    }

    @Override
    public String getQuantumEndPoint(String regionName, String token) throws OpenStackException {
        String url = getEndPointByNameAndRegionName("network", regionName, token);
        Integer index = url.lastIndexOf("/v");
        if (index == -1) {
            url = url + "v2.0/";
        }
        return url;
    }

    public String getSdcEndPoint(String regionName, String token) throws OpenStackException {
        log.debug("Get url for sdc in region " + regionName);
        String url;
        try {
            url = getEndPointByNameAndRegionName("sdc", regionName, token);
        } catch (OpenStackException e) {
            String msn = "It is not possible to obtain the SDC endpoint";
            log.error(msn);
            throw new OpenStackException(msn);

        }
        return url;
    }

    public String getDefaultRegion(String token) throws OpenStackException {
        log.debug("Get default region for token " + token);

        List<String> regions = null;
        try {
            regions = getRegionNames(token);
            log.debug("regions " + regions + " " + regions.size());
        } catch (OpenStackException e) {
            String msn = "It is not possible to obtain the SDC endpoint";
            log.error(msn);
            throw new OpenStackException(msn);
        }

        return regions.get(0);
    }

    /**
     * Get a list with the name of all regions for token.
     */
    private List<String> getRegionNames(String token) throws OpenStackException {

        OpenStackAccess openStackAccess = this.getTokenAdmin();
        String responseJSON = getJSONWithEndpoints(token, openStackAccess.getToken());
        return parseRegionName(responseJSON, "nova");

    }

    private String getJSONWithEndpoints(String token, String tokenadmin) throws OpenStackException {
        String url = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL) + "tokens/" + token
                + "/endpoints";

        WebTarget webTarget = client.target(url);
        log.debug("url " + url);
        Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);
        builder.header("X-Auth-Token", tokenadmin);

        Response response = null;

        try {
            response = builder.get();

            int code = response.getStatus();
            log.debug("code " + code);

            String responseJSON = response.readEntity(String.class);
            if (code != 200) {
                String message = "Failed : HTTP (url:" + url + ") error code : " + code + " body: " + responseJSON;

                if (code == 501) {
                    log.warn("failed, probably is essex:" + message);

                    responseJSON = getEndPointsThroughTokenRequest();
                } else {
                    log.error(message);
                    throw new OpenStackException(message);
                }

            }
            return responseJSON;

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private String getEndPointsThroughTokenRequest() throws OpenStackException {

        OpenStackAccess openStackAccess = this.getTokenAdmin();
        JSONObject jsonObject = openStackAccess.getAccessJSON();

        return jsonObject.toString();

    }

    private String parseEndpoint(String token, String response, String type, String regionName)
            throws OpenStackException {

        String url = null;
        Map<String, String> urlMap = new HashMap<String, String>();
        JSONObject jsonObject = JSONObject.fromObject(response);

        if (jsonObject.containsKey("endpoints")) {

            JSONArray endpointsArray = jsonObject.getJSONArray("endpoints");

            boolean notFound = true;
            Iterator it = endpointsArray.iterator();
            JSONObject endpointJson = null;
            while (notFound && it.hasNext()) {

                endpointJson = JSONObject.fromObject(it.next());
                String name1 = endpointJson.get("type").toString();
                String regionName1 = endpointJson.get("region").toString();

                if (type.equals(name1)) {
                    url = endpointJson.get("publicURL").toString();
                    urlMap.put(regionName1, url);
                    if ((regionName != null) && (regionName.equals(regionName1))) {
                        notFound = false;
                    }
                }
            }
            if (!notFound) {
                return url;
            }
            // return default regionName

        } else {
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

        }
        ;
        return urlMap.get(this.getDefaultRegion(token));
    }

    /**
     * Parse region name, with compatibility with essex,grizzly.
     * 
     * @param response
     * @param name
     * @return
     */
    public List<String> parseRegionName(String response, String name) {

        List<String> names = new ArrayList<String>(2);
        JSONObject jsonObject = JSONObject.fromObject(response);

        if (jsonObject.containsKey("endpoints")) {

            JSONArray endpointsArray = jsonObject.getJSONArray("endpoints");

            Iterator it = endpointsArray.iterator();
            JSONObject endpointJson;
            while (it.hasNext()) {

                endpointJson = JSONObject.fromObject(it.next());
                String name1 = endpointJson.get("name").toString();
                String regionName1 = endpointJson.get("region").toString();

                if (name.equals(name1)) {
                    if (!names.contains(regionName1)) {
                        names.add(regionName1);
                    }
                }
            }
        } else {
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
        }
        return names;
    }

    public SystemPropertiesProvider getSystemPropertiesProvider() {
        return systemPropertiesProvider;
    }

    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getFederatedQuantumEndPoint(String token) throws OpenStackException {
        String url = getEndPointByNameAndRegionName("federatednetwork", getDefaultRegion(token), token);
        return url;
    }

    public String getChefServerEndPoint(String regionName, String token) throws OpenStackException {
        String url;
        try {
            url = getEndPointByNameAndRegionName("chef-server", regionName, token);
        } catch (OpenStackException e) {
            String msn = "It is not possible to obtain the chef-server endpoint";
            log.error(msn);
            throw new OpenStackException(msn);

        }
        log.debug("Obtained chef-server endpoint " + url);
        return url;
    }

    public HttpClientConnectionManager getHttpConnectionManager() {
        return httpConnectionManager;
    }

    public void setHttpConnectionManager(HttpClientConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }

    public RegionCache getRegionCache() {
        return regionCache;
    }

}
