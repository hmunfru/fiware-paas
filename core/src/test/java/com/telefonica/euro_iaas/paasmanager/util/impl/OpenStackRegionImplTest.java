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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileReader;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;
import com.telefonica.fiware.commons.openstack.auth.OpenStackAccess;
import com.telefonica.fiware.commons.openstack.auth.OpenStackKeystoneV3;
import com.telefonica.fiware.commons.openstack.auth.exception.OpenStackException;
import com.telefonica.fiware.commons.util.RegionCache;
import com.telefonica.fiware.commons.util.TokenCache;

public class OpenStackRegionImplTest {

    SystemPropertiesProvider systemPropertiesProvider;
    TokenCache tokenCache = new TokenCache();
    JSONObject serviceCatalogJSON;

    @Before
    public void setUp() throws IOException, ParseException {

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(this.getClass().getResource("/service_catalog.json").getPath()));

        serviceCatalogJSON = JSONObject.fromObject(obj.toString());
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL)).thenReturn(
                "http://domain.com/v3/");

        RegionCache regionCache = new RegionCache();
        regionCache.clear();

    }

    @Test
    public void testShouldGetEndPointsForNovaAndARegionNameWithAPIV3() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();
        openStackRegion.setSystemPropertiesProvider(systemPropertiesProvider);
        Client client = mock(Client.class);
        openStackRegion.setClient(client);
        OpenStackAccess openStackAccess = new OpenStackAccess();
        openStackAccess.setToken("tokenAdminCached");
        openStackAccess.setTenantId("00000001");
        openStackAccess.setAccessJSON(serviceCatalogJSON);
        openStackAccess.setOpenStackKeystone(new OpenStackKeystoneV3());

        tokenCache.putAdmin(openStackAccess);

        // when

        String resultURL = openStackRegion.getNovaEndPoint("Spain");
        // then
        assertNotNull(resultURL);
        assertEquals("http://dev-havana-controller:8774/v2/", resultURL);
    }

    @Test
    public void testShouldGetEndPointsForQuantumAndARegionNameWithAPIV3() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();
        openStackRegion.setSystemPropertiesProvider(systemPropertiesProvider);
        Client client = mock(Client.class);
        openStackRegion.setClient(client);

        OpenStackAccess openStackAccess = new OpenStackAccess();
        openStackAccess.setToken("tokenAdminCached");
        openStackAccess.setTenantId("00000001");
        openStackAccess.setAccessJSON(serviceCatalogJSON);
        openStackAccess.setOpenStackKeystone(new OpenStackKeystoneV3());
        tokenCache.putAdmin(openStackAccess);

        // when
        String resultURL = openStackRegion.getQuantumEndPoint("Spain");
        // then
        assertNotNull(resultURL);
        assertEquals("http://dev-havana-neutron:9696/v2.0/", resultURL);
    }

    @Test
    public void testShouldGetEndPointsForNovaAndARegionNameUsingCache() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();

        String regionName = "RegionOne";

        RegionCache regionCache = new RegionCache();
        regionCache.putUrl("RegionOne", "compute", "http://130.206.80.58:8774/v3/12321312312312321");

        // when

        String resultURL = openStackRegion.getNovaEndPoint(regionName);
        // then
        assertNotNull(resultURL);
        assertEquals("http://130.206.80.58:8774/v3/", resultURL);
    }

    @Test
    public void testShouldGetDefaultRegionWithAdminTokenInCacheWithAPIV3() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();
        openStackRegion.setSystemPropertiesProvider(systemPropertiesProvider);

        Client client = mock(Client.class);
        openStackRegion.setClient(client);

        OpenStackAccess openStackAccess = new OpenStackAccess();
        openStackAccess.setToken("tokenAdminCached");
        openStackAccess.setTenantId("00000001");
        openStackAccess.setAccessJSON(serviceCatalogJSON);
        openStackAccess.setOpenStackKeystone(new OpenStackKeystoneV3());

        tokenCache.putAdmin(openStackAccess);

        // when
        String result = openStackRegion.getDefaultRegion();
        // then
        assertNotNull(result);
        assertEquals("Trento", result);
    }

    @Test
    public void testShouldGetDefaultRegionWithoutAdminTokenInCache() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();
        openStackRegion.setSystemPropertiesProvider(systemPropertiesProvider);
        Client client = mock(Client.class);
        openStackRegion.setClient(client);

        tokenCache.getCache().removeAll();

        WebTarget webTarget = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);
        Response response = mock(Response.class);
        when(client.target("http://domain.com/v3/auth/tokens")).thenReturn(webTarget);

        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);

        when(builder.post(any(Entity.class))).thenReturn(response);
        when(response.readEntity(String.class)).thenReturn(serviceCatalogJSON.toString());
        when(response.getStatus()).thenReturn(200);

        // when
        String result = openStackRegion.getDefaultRegion();
        // then
        assertNotNull(result);
        assertEquals("Trento", result);
        verify(response).getStatus();
        verify(response).readEntity(String.class);
    }

    // @Test
    public void testShouldGetEndPointsForFederatedNetwork() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();
        openStackRegion.setSystemPropertiesProvider(systemPropertiesProvider);

        OpenStackAccess openStackAccess = new OpenStackAccess();
        openStackAccess.setToken("tokenAdminCached");
        openStackAccess.setTenantId("00000001");
        openStackAccess.setAccessJSON(serviceCatalogJSON);
        tokenCache.putAdmin(openStackAccess);

        RegionCache regionCache = new RegionCache();
        regionCache.putUrl("Spain", "federatednetwork", "http://130.206.80.58:8774/v2.0/12321312312312321");

        // when

        String resultURL = openStackRegion.getFederatedQuantumEndPoint();
        // then
        assertNotNull(resultURL);
        assertEquals("http://130.206.80.58:8774/v2.0/12321312312312321", resultURL);
    }

    @Test
    public void testShouldGetEndPointsForSdcAndARegionNameUsingCache() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();

        String regionName = "RegionOne";

        RegionCache regionCache = new RegionCache();
        regionCache.putUrl("RegionOne", "sdc", "https://saggita.lab.fiware.org:8443/rest/");

        // when

        String resultURL = openStackRegion.getSdcEndPoint(regionName);
        // then
        assertNotNull(resultURL);
        assertEquals("https://saggita.lab.fiware.org:8443/rest/", resultURL);
    }

    @Test
    public void testShouldGetEndPointsForChefAndARegionNameUsingCache() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();

        String regionName = "RegionOne";

        RegionCache regionCache = new RegionCache();
        regionCache.putUrl("RegionOne", "chef-server", "https://chefserver.lab.fiware.org:8443/rest/");

        // when

        String resultURL = openStackRegion.getChefServerEndPoint(regionName);
        // then
        assertNotNull(resultURL);
        assertEquals("https://chefserver.lab.fiware.org:8443/rest/", resultURL);
    }

    @Test
    public void testShouldGetEndPointsForPuppetMasterAndARegionNameUsingCache() throws OpenStackException {
        // given
        OpenStackRegionImpl openStackRegion = new OpenStackRegionImpl();

        String regionName = "RegionOne";

        RegionCache regionCache = new RegionCache();
        regionCache.putUrl("RegionOne", "puppetmaster", "https://puppetmaster.lab.fiware.org:8443/rest/");

        // when

        String resultURL = openStackRegion.getPuppetMasterEndPoint(regionName);
        // then
        assertNotNull(resultURL);
        assertEquals("https://puppetmaster.lab.fiware.org:8443/rest/", resultURL);
    }
}
