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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;

public class OpenStackKeystoneV2Test {

    private JSONObject serviceCatalogJSON;

    @Before
    public void setUp() {

        if (serviceCatalogJSON == null) {
            JSONParser parser = new JSONParser();
            Object obj = null;
            try {
                obj = parser
                        .parse(new FileReader(this.getClass().getResource("/service_catalog_apiV2.json").getPath()));
            } catch (IOException e) {
                e.printStackTrace();  // To change body of catch statement use File | Settings | File Templates.
            } catch (ParseException e) {
                e.printStackTrace();  // To change body of catch statement use File | Settings | File Templates.
            }

            serviceCatalogJSON = JSONObject.fromObject(obj.toString());
        }
    }

    @Test
    public void shouldGetValidPayload() {
        // given
        String expectedPayload = "{\"auth\":{\"tenantName\":\"tenantName1\","
                + "\"passwordCredentials\":{\"username\":\"myUser\"," + "\"password\":\"secret\"}}}";
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        // when
        String payload = openStackKeystone.getPayload("myUser", "secret", "tenantName1");

        // then
        assertNotNull(payload);
        assertEquals(expectedPayload, payload);

    }

    @Test
    public void shouldGetVersion2() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        // when
        String version = openStackKeystone.getVersion();

        // then
        assertNotNull(version);
        assertEquals("v2.0", version);
    }

    @Test
    public void shouldGetKeystoneUrlV3() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        // when
        String url = openStackKeystone.getKeystoneURL("http://localhost/v2.0");

        // then
        assertEquals("http://localhost/v2.0/tokens", url);

    }

    @Test
    public void shouldGetKeystoneUrlV2WithSlashCharacter() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        // when
        String url = openStackKeystone.getKeystoneURL("http://localhost/v2.0/");

        // then
        assertEquals("http://localhost/v2.0/tokens", url);

    }

    @Test
    public void shouldParseResponse() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();
        OpenStackAccess openStackAccess = new OpenStackAccess();

        Response response = mock(Response.class);
        String responseString = "{\"access\": {\"token\": {\"issued_at\": \"2015-04-16T10:33:42.669361\", "
                + "\"expires\": \"2015-04-17T06:33:42Z\", \"id\": \"token1\", "
                + "\"tenant\": {\"description\": \"desc\", \"enabled\": true, "
                + "\"id\": \"tenantId1\", \"name\": \"tenantName1\"}, "
                + "\"audit_ids\": [\"MK83_VRlQRSUIjJieGqN0A\"]}, \"user\": {\"username\": \"username1\", "
                + "\"roles_links\": [], \"id\": \"e12249b99b3e4b9394dd85703b04e851\", "
                + "\"roles\": [{\"name\": \"admin\"}], \"name\": \"user name\"}, \"metadata\": {\"is_admin\": 0, "
                + "\"roles\": [\"bb780354f545410b9cc144809e845148\"]}}}";
        JSONObject jsonObjectResponse = JSONObject.fromObject(responseString);

        // when
        openStackKeystone.parseResponse(openStackAccess, response, jsonObjectResponse);

        // then
        assertEquals("token1", openStackAccess.getToken());
        assertEquals("tenantId1", openStackAccess.getTenantId());
        assertEquals("tenantName1", openStackAccess.getTenantName());
        assertNotNull(openStackAccess.getAccessJSON());
    }

    @Test
    public void shouldCheckTokenWithValidToken() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();
        String responseJSON = "{\"access\": {\"token\": {\"issued_at\": \"2015-04-16T10:33:42.669361\", "
                + "\"expires\": \"2015-04-17T06:33:42Z\", \"id\": \"token1\", "
                + "\"tenant\": {\"description\": \"desc\", \"enabled\": true, "
                + "\"id\": \"tenantId1\", \"name\": \"tenantName1\"}, "
                + "\"audit_ids\": [\"MK83_VRlQRSUIjJieGqN0A\"]}, \"user\": {\"username\": \"username1\", "
                + "\"roles_links\": [], \"id\": \"e12249b99b3e4b9394dd85703b04e851\", "
                + "\"roles\": [{\"name\": \"admin\"}], \"name\": \"user name\"}, \"metadata\": {\"is_admin\": 0, "
                + "\"roles\": [\"bb780354f545410b9cc144809e845148\"]}}}";
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn(responseJSON);
        // when
        String result[] = openStackKeystone.checkToken("token1", "tenantId1", response);

        // then
        assertNotNull(result);
        assertEquals("username1", result[0]);
        assertEquals("tenantName1", result[1]);
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldFailInCheckTokenWithInvalidToken() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(401);
        // when
        openStackKeystone.checkToken("tokenInvalid", "tenantId1", response);

        // then
    }

    @Test(expected = AuthenticationServiceException.class)
    public void shouldFailInCheckTokenWithValidTenantId() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();
        String responseJSON = "{\"access\": {\"token\": {\"issued_at\": \"2015-04-16T10:33:42.669361\", "
                + "\"expires\": \"2015-04-17T06:33:42Z\", \"id\": \"token1\", "
                + "\"tenant\": {\"description\": \"desc\", \"enabled\": true, "
                + "\"id\": \"tenantId1\", \"name\": \"tenantName1\"}, "
                + "\"audit_ids\": [\"MK83_VRlQRSUIjJieGqN0A\"]}, \"user\": {\"username\": \"username1\", "
                + "\"roles_links\": [], \"id\": \"e12249b99b3e4b9394dd85703b04e851\", "
                + "\"roles\": [{\"name\": \"admin\"}], \"name\": \"user name\"}, \"metadata\": {\"is_admin\": 0, "
                + "\"roles\": [\"bb780354f545410b9cc144809e845148\"]}}}";
        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn(responseJSON);
        // when
        openStackKeystone.checkToken("token1", "tenantIdInvalid", response);

        // then
    }

    @Test(expected = AuthenticationServiceException.class)
    public void shouldFailInCheckTokenWithOpenStackError() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        Response response = mock(Response.class);
        when(response.getStatus()).thenReturn(500);
        // when
        openStackKeystone.checkToken("token1", "tenantId1", response);

        // then
    }

    @Test
    public void shouldParseRegionByNova() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        // when

        List<String> names = openStackKeystone.parseRegionNames(serviceCatalogJSON, "nova");
        // then
        assertNotNull(names);
        assertEquals(2, names.size());
        assertTrue(names.contains("Spain"));
        assertTrue(names.contains("Trento"));
    }

    @Test
    public void shouldParseRegionByInvalidService() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        // when

        List<String> names = openStackKeystone.parseRegionNames(serviceCatalogJSON, "invalid");
        // then
        assertNotNull(names);
        assertEquals(0, names.size());
    }

    @Test
    public void shouldParseEndPointByComputeAndSpainRegion() throws OpenStackException {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        // when
        String url = openStackKeystone.parseEndpoint(serviceCatalogJSON, "compute", "Spain");

        // then
        assertNotNull(url);
        assertEquals("http://dev-havana-controller:8774/v2/00000000000004769113056827337000", url);
    }

    @Test
    public void shouldParseEndPointByComputeAndInvalidRegionAndReturnDefault() throws OpenStackException {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV2();

        // when
        String url = openStackKeystone.parseEndpoint(serviceCatalogJSON, "compute", "Invalid");

        // then
        assertNotNull(url);
        assertEquals("http://dev-havana-controller:8774/v2/00000000000004769113056827337000", url);
    }
}
