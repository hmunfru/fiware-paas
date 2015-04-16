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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import org.junit.Test;

public class OpenStackKeystoneV3Test {

    @Test
    public void shouldGetValidPayload() {
        // given
        String expectedPayload = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],"
                + "\"password\":{\"user\":{\"domain\":{\"id\":\"default\"}," + "\"name\":\"myUser\","
                + "\"password\":\"secret\"}}}}}";
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV3();

        // when
        String payload = openStackKeystone.getPayload("myUser", "secret", null);

        // then
        assertNotNull(payload);
        assertEquals(expectedPayload, payload);

    }

    @Test
    public void shouldGetVersion3() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV3();

        // when
        String version = openStackKeystone.getVersion();

        // then
        assertNotNull(version);
        assertEquals("v3", version);
    }

    @Test
    public void shouldGetKeystoneUrlV3() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV3();

        // when
        String url = openStackKeystone.getKeystoneURL("http://localhost/v3");

        // then
        assertEquals("http://localhost/v3/auth/tokens", url);

    }

    @Test
    public void shouldGetKeystoneUrlV3WithSlashCharacter() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV3();

        // when
        String url = openStackKeystone.getKeystoneURL("http://localhost/v3/");

        // then
        assertEquals("http://localhost/v3/auth/tokens", url);

    }

    @Test
    public void shouldParseResponse() {
        // given
        OpenStackKeystone openStackKeystone = new OpenStackKeystoneV3();
        OpenStackAccess openStackAccess = new OpenStackAccess();

        Response response = mock(Response.class);
        String responseString = "{\"token\":{\"methods\":[\"password\"],"
                + "\"roles\":[{\"id\":\"13abab31bc194317a009b25909f390a6\",\"name\":\"owner\"}],"
                + "\"expires_at\":\"2015-04-16T07:50:47.439389Z\",\"project\":{\"domain\":{\"id\":\"default\","
                + "\"name\":\"Default\"},\"id\":\"tenantId1\",\"name\":\"tenantName1\"},"
                + "\"extras\":{},\"user\":{\"domain\":{\"id\":\"default\",\"name\":\"Default\"},"
                + "\"id\":\"userid\",\"name\":\"name\"},"
                + "\"audit_ids\":[\"_pkz5NUESaWns0aTtDsZ_A\"],\"issued_at\":\"2015-04-15T11:50:47.439442Z\"}}";
        JSONObject jsonObjectResponse = JSONObject.fromObject(responseString);
        when(response.getHeaderString("X-Subject-Token")).thenReturn("token1");

        // when
        openStackKeystone.parseResponse(openStackAccess, response, jsonObjectResponse);

        // then
        assertEquals("token1", openStackAccess.getToken());
        assertEquals("tenantId1", openStackAccess.getTenantId());
        assertEquals("tenantName1", openStackAccess.getTenantName());
        assertNotNull(openStackAccess.getAccessJSON());
    }
}
