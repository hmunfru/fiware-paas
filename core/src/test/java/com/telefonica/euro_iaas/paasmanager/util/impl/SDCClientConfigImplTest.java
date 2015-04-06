package com.telefonica.euro_iaas.paasmanager.util.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.ws.rs.client.Client;

import org.apache.http.conn.HttpClientConnectionManager;
import org.junit.Test;

public class SDCClientConfigImplTest {

    @Test
    public void shouldCreateAClient() {
        // given

        HttpClientConnectionManager httpConnectionManager = mock(HttpClientConnectionManager.class);
        SDCClientConfigImpl sdcClientConfigImpl = new SDCClientConfigImpl(httpConnectionManager);

        // when
        Client client = sdcClientConfigImpl.getClient();

        // then
        assertNotNull(client);
    }

}
