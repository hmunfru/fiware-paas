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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.bean.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;

public class OperationUtilImplTest {

    private CloseableHttpClient closeableHttpClientMock;

    private PaasManagerUser paasManagerUser;
    private OperationUtilImplTestable openOperationUtil;

    private OpenStackRegion openStackRegion;

    @Before
    public void setUp() throws OpenStackException, IOException {
        openOperationUtil = new OperationUtilImplTestable();
        openStackRegion = mock(OpenStackRegion.class);
        openOperationUtil.setOpenStackRegion(openStackRegion);
        HttpClientConnectionManager httpClientConnectionManager = mock(HttpClientConnectionManager.class);
        openOperationUtil.setHttpConnectionManager(httpClientConnectionManager);

        paasManagerUser = new PaasManagerUser("user", "aa");
        paasManagerUser.setToken("1234567891234567989");
        paasManagerUser.setTenantId("08bed031f6c54c9d9b35b42aa06b51c0");

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
