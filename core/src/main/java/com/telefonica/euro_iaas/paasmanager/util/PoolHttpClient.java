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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.conn.HttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;

public class PoolHttpClient {

    public static PoolHttpClient getInstance(HttpClientConnectionManager httpConnectionManager) {
        return new PoolHttpClient(httpConnectionManager);
    }

    private Client client;

    private PoolHttpClient(HttpClientConnectionManager httpConnectionManager) {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class);

        clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, httpConnectionManager);
        ApacheConnectorProvider provider = new ApacheConnectorProvider();
        clientConfig.connectorProvider(provider);
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        clientConfig.property(ClientProperties.READ_TIMEOUT, 60000);
        client = ClientBuilder.newClient(clientConfig);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
