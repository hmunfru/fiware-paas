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

package com.telefonica.euro_iaas.paasmanager.util;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.bean.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;

public class OperationUtilImplTest {

    private SystemPropertiesProvider systemPropertiesProvider;
    private CloseableHttpClient closeableHttpClientMock;
    private StatusLine statusLine;
    private CloseableHttpResponse httpResponse;
    private PaasManagerUser paasManagerUser;
    private OperationUtilImplTestable openOperationUtil;

    private OpenStackRegion openStackRegion;
    final int TWICE = 2;
    final int SEVEN_TIMES = 7;
    final int FOUR_TIMES = 4;
    private static int http_code_accepted = 202;
    /**
     * HTTP code for accepted requests.
     */
    private static int http_code_ok = 200;
    /**
     * HTTP code for created requests.
     */
    private static int http_code_created = 201;
    /**
     * HTTP code for no content response.
     */
    private static int http_code_deleted = 204;

    String CONTENT_NETWORKS = "{ " + "\"networks\": [ " + "{ " + "\"status\": \"ACTIVE\", " + "\"subnets\": [ "
            + "\"81f10269-e0a2-46b0-9583-2c83aa4cc76f\" " + " ], " + "\"name\": \"jesuspg-net\", "
            + "\"provider:physical_network\": null, " + "\"admin_state_up\": true, "
            + "\"tenant_id\": \"67c979f51c5b4e89b85c1f876bdffe31\", " + "\"router:external\": false, "
            + "\"shared\": false, " + "\"id\": \"047e6dd3-3101-434e-af1e-eea571ab57a4\", "
            + "\"provider:segmentation_id\": 29 " + "}, " + "{ " + "\"status\": \"ACTIVE\", " + "\"subnets\": [ "
            + "\"e2d10e6b-33c3-400c-88d6-f905d4cd02f2\" " + " ], " + "\"name\": \"ext-net\", "
            + "\"provider:physical_network\": null, " + "\"admin_state_up\": true, "
            + "\"tenant_id\": \"08bed031f6c54c9d9b35b42aa06b51c0\", " + "\"router:external\": true, "
            + "\"shared\": false, " + "\"id\": \"080b5f2a-668f-45e0-be23-361c3a7d11d0\", "
            + "\"provider:segmentation_id\": 1 " + "} ]} ";

    @Before
    public void setUp() throws OpenStackException, ClientProtocolException, IOException {
        openOperationUtil = new OperationUtilImplTestable();
        systemPropertiesProvider = mock(SystemPropertiesProvider.class);
        openStackRegion = mock(OpenStackRegion.class);
        openOperationUtil.setOpenStackRegion(openStackRegion);
        HttpClientConnectionManager httpClientConnectionManager = mock(HttpClientConnectionManager.class);
        openOperationUtil.setHttpConnectionManager(httpClientConnectionManager);
        openOperationUtil.setSystemPropertiesProvider(systemPropertiesProvider);

        paasManagerUser = new PaasManagerUser("user", "aa");
        paasManagerUser.setToken("1234567891234567989");
        paasManagerUser.setTenantId("08bed031f6c54c9d9b35b42aa06b51c0");

        httpResponse = mock(CloseableHttpResponse.class);
        statusLine = mock(StatusLine.class);
        closeableHttpClientMock = mock(CloseableHttpClient.class);

        when(openStackRegion.getNovaEndPoint(anyString())).thenReturn("http://localhost/v2.0");

    }

    @Test
    public void shouldCreateNovaPostRequest() throws OpenStackException, IOException {

        HttpPost response = openOperationUtil.createNovaPostRequest("resource", "payload", "content", "accept",
                "region", "token", "vdc");

        // then
        assertNotNull(response);
    }

    private class OperationUtilImplTestable extends OpenOperationUtilImpl {

        public CloseableHttpClient getHttpClient() {

            return closeableHttpClientMock;
        }
    }
}
