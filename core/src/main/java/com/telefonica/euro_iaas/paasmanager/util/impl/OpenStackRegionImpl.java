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
    public String getEndPointByNameAndRegionName(String type, String regionName) throws OpenStackException {

        String url = regionCache.getUrl(regionName, type);

        if (url != null) {
            log.debug("Get url for sdc in region " + url);
            return url;
        } else {
            OpenStackAccess openStackAccess = this.getTokenAdmin();

            String result = parseEndpoint(openStackAccess.getAccessJSON(), type, regionName);
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
    public String getNovaEndPoint(String regionName) throws OpenStackException {

        String url = getEndPointByNameAndRegionName("compute", regionName);
        log.debug("getNovaEndPoint " + regionName + " " + url);

        Integer index = url.lastIndexOf("/");
        url = url.substring(0, index + 1);

        return url;

    }

    @Override
    public String getPuppetMasterEndPoint(String regionName) throws OpenStackException {
        String url;
        try {
            log.info("Get url for puppet for default region " + regionName);
            url = getEndPointByNameAndRegionName("puppetmaster", regionName);
        } catch (OpenStackException e) {
            String msn = "It is not possible to obtain the Puppet Master endpoint";
            log.info(msn);
            throw new OpenStackException(msn);

        }
        return url;
    }

    @Override
    public String getQuantumEndPoint(String regionName) throws OpenStackException {
        String url = getEndPointByNameAndRegionName("network", regionName);
        Integer index = url.lastIndexOf("/v");
        if (index == -1) {
            url = url + "v3/";
        }
        return url;
    }

    public String getSdcEndPoint(String regionName) throws OpenStackException {
        log.debug("Get url for sdc in region " + regionName);
        String url;
        try {
            url = getEndPointByNameAndRegionName("sdc", regionName);
        } catch (OpenStackException e) {
            String msn = "It is not possible to obtain the SDC endpoint";
            log.error(msn);
            throw new OpenStackException(msn);

        }
        return url;
    }

    public String getDefaultRegion() throws OpenStackException {
        log.debug("Get default region");

        List<String> regions;
        try {
            regions = getRegionNames();
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
    private List<String> getRegionNames() throws OpenStackException {

        OpenStackAccess openStackAccess = this.getTokenAdmin();
        return parseRegionName(openStackAccess.getAccessJSON(), "nova");

    }

    private String parseEndpoint(JSONObject jsonObject, String type, String regionName) throws OpenStackException {

        String url = null;
        Map<String, String> urlMap = new HashMap<String, String>();

        JSONObject tokenJSON = jsonObject.getJSONObject("token");

        if (tokenJSON.containsKey("catalog")) {
            JSONArray servicesArray = tokenJSON.getJSONArray("catalog");

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
                        String interfaceValue = endpointJson.get("interface").toString();

                        if ("public".equals(interfaceValue)) {
                            url = endpointJson.get("url").toString();
                            if (regionName.equals(regionName1)) {
                                notFound = false;
                            }
                            urlMap.put(regionName1, url);
                        }

                    }

                }
            }
            if (!notFound) {
                return url;
            }
        }
        return urlMap.get(this.getDefaultRegion());
    }

    /**
     * Parse region name, with compatibility with essex,grizzly.
     * 
     * @param jsonObject
     * @param name
     * @return
     */
    public List<String> parseRegionName(JSONObject jsonObject, String name) {

        List<String> names = new ArrayList<String>(2);

        JSONObject tokenJSON = jsonObject.getJSONObject("token");

        if (tokenJSON.containsKey("catalog")) {
            JSONArray servicesArray = tokenJSON.getJSONArray("catalog");

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

    public String getFederatedQuantumEndPoint() throws OpenStackException {
        String url = getEndPointByNameAndRegionName("federatednetwork", getDefaultRegion());
        return url;
    }

    public String getChefServerEndPoint(String regionName) throws OpenStackException {
        String url;
        try {
            url = getEndPointByNameAndRegionName("chef-server", regionName);
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
