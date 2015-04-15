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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.bean.OpenStackAccess;
import com.telefonica.euro_iaas.paasmanager.exception.AuthenticationConnectionException;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

public class OpenStackAuthenticationTokenTest {

    private SystemPropertiesProvider systemPropertiesProvider;
    private final String keystoneUrl = "http://keystone";

    @Before
    public void setUp() {

        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL)).thenReturn(keystoneUrl);
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_USER)).thenReturn("user");
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_PASS)).thenReturn("pass");
        when(systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_TENANT)).thenReturn("tenant");

    }

    @Test
    public void getCredentialsTest() throws AuthenticationConnectionException, IOException {

        OpenStackAuthenticationToken openStackAuthenticationToken;

        // Given

        String responseJSON = "{\n" + "    \"token\": {\n" + "        \"methods\": [\n" + "            \"password\"\n"
                + "        ],\n" + "        \"roles\": [\n" + "            {\n"
                + "                \"id\": \"bb780354f545410b9cc144809e845148\",\n"
                + "                \"name\": \"admin\"\n" + "            }\n" + "        ],\n"
                + "        \"expires_at\": \"2015-04-16T05:46:46.510754Z\",\n" + "        \"project\": {\n"
                + "            \"domain\": {\n" + "                \"id\": \"default\",\n"
                + "                \"name\": \"Default\"\n" + "            },\n"
                + "            \"id\": \"tenantId1\",\n" + "            \"name\": \"tenant name\"\n" + "        },\n"
                + "        \"extras\": {},\n" + "        \"user\": {\n" + "            \"domain\": {\n"
                + "                \"id\": \"default\",\n" + "                \"name\": \"Default\"\n"
                + "            },\n" + "            \"id\": \"e12249b99b3e4b9394dd85703b04e851\",\n"
                + "            \"name\": \"admin\"\n" + "        },\n" + "        \"audit_ids\": [\n"
                + "            \"hbiTO5lCTfm5ScW7acVVYg\"\n" + "        ],\n"
                + "        \"issued_at\": \"2015-04-15T09:46:46.510825Z\"\n" + "    }\n" + "}";

        String xSubjectToken = "fffef88341aa4df39514c251a0ff9ff4";

        Client client = mock(Client.class);
        WebTarget webTarget = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);

        openStackAuthenticationToken = new OpenStackAuthenticationToken(systemPropertiesProvider);
        when(client.target(keystoneUrl + "auth/tokens")).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);
        Response response = mock(Response.class);
        when(builder.post(any(Entity.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(200);
        when(response.getHeaderString("X-Subject-Token")).thenReturn(xSubjectToken);
        when(response.readEntity(String.class)).thenReturn(responseJSON);

        // when
        OpenStackAccess openStackAccess = openStackAuthenticationToken.getAdminCredentials(client);

        // then
        verify(client).target(keystoneUrl + "auth/tokens");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(builder).accept(MediaType.APPLICATION_JSON);
        verify(response).getStatus();
        verify(response).readEntity(String.class);
        verify(response).getHeaderString("X-Subject-Token");
        assertNotNull(openStackAccess);
        assertEquals("fffef88341aa4df39514c251a0ff9ff4", openStackAccess.getToken());
        assertEquals("tenantId1", openStackAccess.getTenantId());
        assertEquals("tenant name", openStackAccess.getTenantName());

    }

    @Test(expected = AuthenticationConnectionException.class)
    public void shouldGetCredentialsWithErrorAfterRequestToken() throws IOException {
        OpenStackAuthenticationToken openStackAuthenticationToken;

        // Given

        Client client = mock(Client.class);
        WebTarget webTarget = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);

        openStackAuthenticationToken = new OpenStackAuthenticationToken(systemPropertiesProvider);

        when(client.target(keystoneUrl + "auth/tokens")).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);
        Response response = mock(Response.class);
        when(builder.post(any(Entity.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(400);

        // when
        openStackAuthenticationToken.getAdminCredentials(client);

        // then

    }

    @Test
    public void getCredentialsWithReturningCode201() throws AuthenticationConnectionException, IOException {

        OpenStackAuthenticationToken openStackAuthenticationToken;

        // Given
        String responseJSON = "{\n" + "    \"token\": {\n" + "        \"methods\": [\n" + "            \"password\"\n"
                + "        ],\n" + "        \"roles\": [\n" + "            {\n"
                + "                \"id\": \"bb780354f545410b9cc144809e845148\",\n"
                + "                \"name\": \"admin\"\n" + "            }\n" + "        ],\n"
                + "        \"expires_at\": \"2015-04-16T05:46:46.510754Z\",\n" + "        \"project\": {\n"
                + "            \"domain\": {\n" + "                \"id\": \"default\",\n"
                + "                \"name\": \"Default\"\n" + "            },\n"
                + "            \"id\": \"tenantId1\",\n" + "            \"name\": \"tenant name\"\n" + "        },\n"
                + "        \"extras\": {},\n" + "        \"user\": {\n" + "            \"domain\": {\n"
                + "                \"id\": \"default\",\n" + "                \"name\": \"Default\"\n"
                + "            },\n" + "            \"id\": \"e12249b99b3e4b9394dd85703b04e851\",\n"
                + "            \"name\": \"admin\"\n" + "        },\n" + "        \"audit_ids\": [\n"
                + "            \"hbiTO5lCTfm5ScW7acVVYg\"\n" + "        ],\n"
                + "        \"issued_at\": \"2015-04-15T09:46:46.510825Z\"\n" + "    }\n" + "}";
        String xSubjectToken = "fffef88341aa4df39514c251a0ff9ff4";

        Client client = mock(Client.class);
        WebTarget webTarget = mock(WebTarget.class);
        Invocation.Builder builder = mock(Invocation.Builder.class);

        openStackAuthenticationToken = new OpenStackAuthenticationToken(systemPropertiesProvider);
        when(client.target(keystoneUrl + "auth/tokens")).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON)).thenReturn(builder);
        Response response = mock(Response.class);
        when(builder.post(any(Entity.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.readEntity(String.class)).thenReturn(responseJSON);
        when(response.getHeaderString("X-Subject-Token")).thenReturn(xSubjectToken);

        // when
        OpenStackAccess openStackAccess = openStackAuthenticationToken.getAdminCredentials(client);

        // then
        verify(client).target(keystoneUrl + "auth/tokens");
        verify(webTarget).request(MediaType.APPLICATION_JSON);
        verify(builder).accept(MediaType.APPLICATION_JSON);
        verify(response).getStatus();
        verify(response).readEntity(String.class);
        assertNotNull(openStackAccess);
        assertEquals("fffef88341aa4df39514c251a0ff9ff4", openStackAccess.getToken());
        assertEquals("tenantId1", openStackAccess.getTenantId());
        assertEquals("tenant name", openStackAccess.getTenantName());

    }
}
