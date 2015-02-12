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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;

/**
 * @author jesus.movilla
 */
public class OpenOperationUtilImpl implements OpenOperationUtil {

    /**
     * The log.
     */

    private static Logger log = LoggerFactory.getLogger(OpenOperationUtilImpl.class);
    /**
     * the properties configuration.
     */
    private SystemPropertiesProvider systemPropertiesProvider;
    /**
     * HTTP code for accepted requests.
     */
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

    /**
     * authToken to be used.
     */
    private String authToken;
    /**
     * tenant to be used.
     */
    private String tenant;

    /**
     * user to be used.
     */
    private String user;

    private HttpClientConnectionManager httpConnectionManager;

    private OpenStackRegion openStackRegion;

    /**
     * The constructor.
     */
    public OpenOperationUtilImpl() {
    }

    public HttpClientConnectionManager getHttpConnectionManager() {
        return httpConnectionManager;
    }

    public void setHttpConnectionManager(HttpClientConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }

    /**
     * Returns an InputStream as String.
     * 
     * @param is
     *            InputStream from response
     * @return Compute Compute
     * @throws OpenStackException
     *             OCCIException
     */
    private static String convertStreamToString(InputStream is) throws OpenStackException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            throw new OpenStackException(e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new OpenStackException(e.getMessage());
            }
        }
        return sb.toString();
    }

    /**
     * Checks if metadatas (authToken and tenant) were initialized.
     * 
     * @throws OpenStackException
     */
    private void checkParam(String tenantId, String token) throws OpenStackException {
        if (token == null || tenantId == null) {
            throw new OpenStackException(ERROR_AUTHENTICATION_HEADERS);
        }
    }

    /**
     * It obtains the credentials to invoke as a admin user.
     * 
     * @return
     * @throws OpenStackException
     */
    public PaasManagerUser getAdminUser(PaasManagerUser user) throws OpenStackException {
        HttpPost postRequest = createKeystonePostRequest();
        ArrayList<Object> response = executePostRequest(postRequest);
        return extractData(response, user);
    }

    /**
     * It obtains the request for invoking Openstack keystone with admin credentials.
     * 
     * @return
     * @throws OpenStackException
     */
    public HttpPost createKeystonePostRequest() throws OpenStackException {
        // curl -d '{"auth": {"tenantName": "demo", "passwordCredentials":
        // {"username": "admin", "password": "temporal"}}}'
        // -H "Content-type: application/json"
        // -H "Accept: application/xml"�
        // http://10.95.171.115:35357/v2.0/tokens

        String keystoneURL = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_URL);
        String adminUser = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_USER);
        String adminPass = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_PASS);
        String adminTenant = systemPropertiesProvider.getProperty(SystemPropertiesProvider.KEYSTONE_TENANT);

        HttpEntity entity = null;
        HttpPost postRequest = new HttpPost(keystoneURL + "tokens");
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setHeader("Accept", "application/xml");

        String msg = "{\"auth\": {\"tenantName\": \"" + adminTenant + "\", \""
                + "passwordCredentials\":{\"username\": \"" + adminUser + "\"," + " \"password\": \"" + adminPass
                + "\"}}}";

        try {
            entity = new StringEntity(msg);
        } catch (UnsupportedEncodingException ex) {
            log.error("Unsupported encoding exception");
            throw new OpenStackException("Unsupported encoding exception " + ex.getMessage());
        }
        postRequest.setEntity(entity);
        return postRequest;
    }

    public ArrayList<Object> executePostRequest(HttpPost postRequest) throws OpenStackException {
        HttpResponse response;
        CloseableHttpClient httpClient = getHttpClient();
        ArrayList<Object> message = new ArrayList();

        Date localDate = null;
        String aux;
        try {
            response = httpClient.execute(postRequest);

            localDate = new Date();
            if ((response.getStatusLine().getStatusCode() != 201) && (response.getStatusLine().getStatusCode() != 200)) {
                log.error("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String temp = "";

            while ((aux = br.readLine()) != null) {
                temp += aux;
            }

            message.add(temp);

            String aux1 = response.getHeaders("Date")[0].getValue();
            log.info("Date recibido: " + aux1);
            message.add(response.getHeaders("Date")[0].getValue());
            HttpEntity ent = response.getEntity();
            if (ent != null) {
                EntityUtils.consume(ent);
            }

        } catch (ClientProtocolException ex) {
            log.error("Client protocol exception");
            throw new OpenStackException("Client protocol exception " + ex.getMessage());
        } catch (IOException ex) {
            log.error("I/O exception of some sort has occurred");
            throw new OpenStackException("I/O exception of some sort has occurred " + ex.getMessage());
        }
        return message;
    }

    protected PaasManagerUser extractData(ArrayList<Object> response, PaasManagerUser user) {
        String payload = (String) response.get(0);

        int i = payload.indexOf("token");
        int j = payload.indexOf(">", i);
        String token = payload.substring(i - 1, j + 1);
        String tenantId = "";

        // token = "<token expires=\"2012-11-13T15:01:51Z\" id=\"783bec9d7d734f1e943986485a90966d\">";
        // Regular Expression <\s*token\s*(issued_at=\".*?\"\s*)?expires=\"(.*?)(\"\s*id=\")(.*)\"\/*>
        // as a Java string "<\\s*token\\s*(issued_at=\\\".*?\\\"\\s*)?expires=\\\"(.*?)(\\\"\\s*id=\\\")(.*)\\\"\\/*>"
        String pattern1 = "<\\s*token\\s*(issued_at=\\\".*?\\\"\\s*)?expires=\\\"(.*?)(\\\"\\s*id=\\\")(.*)\\\"\\/*>";

        if (token.matches(pattern1)) {

            token = token.replaceAll(pattern1, "$4");
            log.info("token id: " + token);
        } else {
            log.error("Token format unknown: " + token);

            throw new RuntimeException("Token format unknown:\n " + token);
        }

        i = payload.indexOf("tenant");
        j = payload.indexOf(">", i);
        tenantId = payload.substring(i - 1, j + 1);

        // Regular Expression (<\s*tenant\s*.*)("\s*id=")(.*?)("\s*.*/*>)
        // as a Java string "(<\\s*tenant\\s*.*)(\"\\s*id=\")(.*?)(\"\\s*.*/*>)"
        pattern1 = "(<\\s*tenant\\s*.*)(\"\\s*id=\")(.*?)(\"\\s*.*/*>)";

        if (tenantId.matches(pattern1)) {
            tenantId = tenantId.replaceAll(pattern1, "$3");
        } else {
            log.error("Tenant format unknown:\n " + tenantId);

            throw new RuntimeException("Tenant format unknown:\n " + tenantId);
        }
        PaasManagerUser user2 = new PaasManagerUser(tenantId, token, user.getAuthorities());
        return user2;
    }

    /**
     * Returns a request for a NOVA DELETE petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    public HttpUriRequest createNovaDeleteRequest(String resource, String region, String token, String vdc)
            throws OpenStackException {
        HttpUriRequest request;

        try {
            checkParam(vdc, token);
        } catch (OpenStackException ex) {
            java.util.logging.Logger.getLogger(OpenOperationUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        String novaUrl = openStackRegion.getNovaEndPoint(region, token);
        log.debug("novaUrl" + novaUrl);
        request = new HttpDelete(novaUrl + vdc + "/" + resource);

        // request.setHeader(OpenStackConstants.CONTENT_TYPE,
        // OpenStackConstants.APPLICATION_JSON);
        log.debug("NOVA DELETE url " + request.getURI().toString());
        log.debug("NOVA token " + token);
        request.setHeader(ACCEPT, APPLICATION_JSON);
        request.setHeader(X_AUTH_TOKEN, token);

        return request;
    }

    /**
     * Returns a request for a NOVA GET petition.
     */
    public HttpUriRequest createNovaGetRequest(String resource, String accept, String region, String token, String vdc)
            throws OpenStackException {
        HttpUriRequest request;

        try {
            checkParam(vdc, token);
        } catch (OpenStackException ex) {
            java.util.logging.Logger.getLogger(OpenOperationUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        String novaUrl = openStackRegion.getNovaEndPoint(region, token);
        request = new HttpGet(novaUrl + vdc + "/" + resource);

        // request.setHeader(OpenStackConstants.CONTENT_TYPE,
        // OpenStackConstants.APPLICATION_XML);
        log.debug("NOVA GET url " + request.getURI().toString());
        log.debug("NOVA token " + token);
        request.setHeader(ACCEPT, accept);
        request.setHeader(X_AUTH_TOKEN, token);

        return request;
    }

    /**
     * Returns a request for a NOVA POST petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    public HttpPost createNovaPostRequest(String resource, String payload, String content, String accept,
            String region, String token, String vdc) throws OpenStackException {
        HttpPost request;

        // Check that the authtoken, tenant and user was initialized
        // previously.
        try {
            checkParam(vdc, token);
        } catch (OpenStackException ex) {
            java.util.logging.Logger.getLogger(OpenOperationUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info("Payload " + payload);

        String novaUrl = openStackRegion.getNovaEndPoint(region, token);

        request = new HttpPost(novaUrl + vdc + "/" + resource);

        try {
            request.setEntity(new StringEntity(payload));

        } catch (NullPointerException e) {
            log.warn(e.getMessage());
        } catch (UnsupportedEncodingException ex) {
            throw new OpenStackException(ex.getMessage());
        }

        log.debug("NOVA POST url " + request.getURI().toString());
        log.debug("NOVA token " + token);

        request.setHeader(CONTENT_TYPE, content);
        request.setHeader(ACCEPT, accept);
        request.setHeader(X_AUTH_TOKEN, token);

        return request;
    }

    /**
     * Returns a request for a QUANTUM DELETE petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    public HttpUriRequest createQuantumDeleteRequest(String resource, String region, String vdc, String token)
            throws OpenStackException {
        HttpUriRequest request;

        // Check that the authtoken, tenant and user was initialized
        // previously.
        try {
            checkParam(vdc, token);
        } catch (OpenStackException ex) {
            java.util.logging.Logger.getLogger(OpenOperationUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        String quantumUrl = null;
        quantumUrl = openStackRegion.getQuantumEndPoint(region, token);
        request = new HttpDelete(quantumUrl + resource);

        // request.setHeader(OpenStackConstants.CONTENT_TYPE, OpenStackConstants.APPLICATION_JSON);
        request.setHeader(ACCEPT, APPLICATION_JSON);
        log.debug(X_AUTH_TOKEN + " " + token);
        request.setHeader(X_AUTH_TOKEN, token);

        return request;
    }

    /**
     * Returns a request for a Quantum GET petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    public HttpUriRequest createQuantumGetRequest(String resource, String accept, String region, String token,
            String vdc) throws OpenStackException {
        HttpUriRequest request;

        // Check that the auth token, tenant and user was initialized previously.
        try {
            checkParam(vdc, token);
        } catch (OpenStackException ex) {
            java.util.logging.Logger.getLogger(OpenOperationUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        String quantumUrl = openStackRegion.getQuantumEndPoint(region, token);
        log.debug("quantumUrl for region " + region + " " + quantumUrl);
        request = new HttpGet(quantumUrl + resource);

        request.setHeader(ACCEPT, accept);
        request.setHeader(X_AUTH_TOKEN, token);

        return request;
    }

    /**
     * Returns a request for a Quantum POST petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    public HttpPost createQuantumPostRequest(String resource, String payload, String content, String region,
            String token, String vdc) throws OpenStackException {
        log.debug("createQuantumPostRequest " + resource);
        HttpPost request;

        // Check that the authtoken, tenant and user was initialized
        // previously.
        try {
            checkParam(vdc, token);
        } catch (OpenStackException ex) {
            java.util.logging.Logger.getLogger(OpenOperationUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info("Payload " + payload);

        String quantumUrl = openStackRegion.getQuantumEndPoint(region, token);
        request = new HttpPost(quantumUrl + resource);

        try {

            request.setEntity(new StringEntity(payload));

        } catch (NullPointerException e) {
            log.warn(e.getMessage());
        } catch (UnsupportedEncodingException ex) {
            throw new OpenStackException(ex.getMessage());
        }

        request.setHeader(ACCEPT, APPLICATION_JSON);

        request.setHeader(CONTENT_TYPE, content);
        log.debug("Content " + content);

        request.setHeader(X_AUTH_TOKEN, token);
        log.debug("user.getToken() " + token);

        return request;
    }

    /**
     * Returns a request for a Quantum POST petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    public HttpPut createQuantumPutRequest(String resource, String payload, String content, String region,
            String token, String vdc) throws OpenStackException {
        HttpPut request;

        // Check that the authtoken, tenant and user was initialized
        // previously.
        try {
            checkParam(vdc, token);
        } catch (OpenStackException ex) {
            java.util.logging.Logger.getLogger(OpenOperationUtilImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info("Payload " + payload);

        String quantumUrl = openStackRegion.getQuantumEndPoint(region, token);
        request = new HttpPut(quantumUrl + resource);

        try {

            request.setEntity(new StringEntity(payload));

        } catch (NullPointerException e) {
            log.warn(e.getMessage());
        } catch (UnsupportedEncodingException ex) {
            throw new OpenStackException(ex.getMessage());
        }

        request.setHeader(ACCEPT, APPLICATION_JSON);

        request.setHeader(CONTENT_TYPE, content);

        request.setHeader(X_AUTH_TOKEN, token);

        return request;
    }

    /**
     * Method to execute a request and get the response from NOVA.
     * 
     * @param request
     *            the request to be executed
     * @return HttpUriRequest the response from server
     * @throws OpenStackException
     */
    public String executeNovaRequest(HttpUriRequest request) throws OpenStackException {
        log.debug("executeNovaRequest " + request.getURI().toString());
        String[] newHeaders = null;
        // Where the response is located. 0 for json, 1 for XML (it depends on
        // the \n)
        int responseLocation = 0;

        CloseableHttpClient httpClient = getHttpClient();

        if (request.containsHeader(ACCEPT) && request.getFirstHeader(ACCEPT).getValue().equals(APPLICATION_XML)) {
            responseLocation = 1;
        }
        HttpResponse response = null;

        try {
            response = httpClient.execute(request);
            log.debug("Status : " + response.getStatusLine().getStatusCode());
            // if (response.getEntity() != null) {
            if ((response.getStatusLine().getStatusCode() != http_code_deleted)) {

                InputStream is = response.getEntity().getContent();
                String result = convertStreamToString(is);
                log.debug("Result " + result);

                is.close();

                if ((response.getStatusLine().getStatusCode() == http_code_ok)
                        || (response.getStatusLine().getStatusCode() == http_code_accepted)
                        || (response.getStatusLine().getStatusCode() == http_code_created)) {

                    newHeaders = result.split("\n");
                } else {
                    log.debug(" HttpResponse " + response.getStatusLine().getStatusCode());
                    if (result.indexOf("badRequest") != -1 || result.indexOf("itemNotFound") != -1) {
                        String error = result.substring(result.indexOf("message") + "message".length() + 3,
                                result.indexOf("code") - 3);
                        log.debug("Error in the request " + error);
                        throw new OpenStackException(error);
                    }

                    throw new OpenStackException(result);
                }

                response.getEntity().getContent().close();
            } else {
                return response.getStatusLine().getReasonPhrase();
            }

        } catch (Exception e) {
            log.warn("Error to execute the request " + e.getMessage());
            if (response.getStatusLine().getStatusCode() == http_code_accepted) {
                return response.getStatusLine().getReasonPhrase();
            } else {
                throw new OpenStackException(e.getMessage());
            }

        }

        if (response.containsHeader("Location")
                && response.getFirstHeader("Location").getValue().contains(RESOURCE_IMAGES)) {

            return response.getFirstHeader("Location").getValue();
        }

        return newHeaders[responseLocation];
    }

    protected CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(httpConnectionManager).build();

    }

    /**
     * Obtains the attribute value from a node.
     * 
     * @param node
     * @param attribute
     * @return
     */
    private String findAttributeValueInNode(Node node, String attribute) {
        return node.getAttributes().getNamedItem(attribute).getTextContent();
    }

    /*
     * Obtains the list of nodes whose tag is nodeListTag
     */
    private NodeList findNodeList(String xmlDoc, String nodeListTag) throws OpenStackException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;
        NodeList nodeList = null;

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlDoc)));

            nodeList = doc.getElementsByTagName(nodeListTag);

        } catch (SAXException e) {
            String errorMessage = "SAXException when obtaining nodeList." + " Desc: " + e.getMessage();
            log.warn(errorMessage);
            throw new OpenStackException(errorMessage);
        } catch (ParserConfigurationException e) {
            String errorMessage = "ParserConfigurationException when obtaining " + "NodelIst. Desc: " + e.getMessage();
            log.warn(errorMessage);
            throw new OpenStackException(errorMessage);
        } catch (IOException e) {
            String errorMessage = "IOException when obtaining " + "NodeList. Desc: " + e.getMessage();
            log.warn(errorMessage);
            throw new OpenStackException(errorMessage);
        } catch (Exception e) {
            String errorMessage = "Unexpected exception : " + e.getMessage();
            log.warn(errorMessage);
            throw new OpenStackException(errorMessage);
        }
        return nodeList;
    }

    /**
     * Return a string with absolute limits values by tenantId.
     * 
     * <pre>
     * GET http://host:port/v2/tenantId/limits
     * Accept: application/json
     * X-Auth-Token: ea90309ce14b4da490fe035c618515db
     * </pre>
     */
    public String getAbsoluteLimits(PaasManagerUser user, String region) throws OpenStackException {
        PaasManagerUser user2 = this.getAdminUser(user);

        log.debug("tenantid " + user.getTenantId());
        log.debug("token " + user2.getToken());
        log.debug("user name " + user2.getUserName());

        HttpUriRequest request = createNovaGetRequest("limits", APPLICATION_JSON, region, user.getToken(),
                user.getTenantId());

        String response = executeNovaRequest(request);

        return response;
    }

    /**
     * @param systemPropertiesProvider
     *            the systemPropertiesProvider to set
     */
    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

    public OpenStackRegion getOpenStackRegion() {
        return openStackRegion;
    }

    public void setOpenStackRegion(OpenStackRegion openStackRegion) {
        this.openStackRegion = openStackRegion;
    }

    public HttpUriRequest createJoinQuantumPostRequestRequest(String resource, String payload, String applicationJson,
            String token) throws OpenStackException {
        log.debug("createJoinQuantumPostRequestRequest " + resource);
        HttpPost request;

        log.info("Payload " + payload);

        String quantumUrl = openStackRegion.getFederatedQuantumEndPoint(token);
        request = new HttpPost(quantumUrl + resource);

        try {

            request.setEntity(new StringEntity(payload));

        } catch (NullPointerException e) {
            log.warn(e.getMessage());
        } catch (UnsupportedEncodingException ex) {
            throw new OpenStackException(ex.getMessage());
        }

        request.setHeader(ACCEPT, APPLICATION_JSON);
        request.setHeader(X_AUTH_TOKEN, token);
        log.debug("user.getToken() " + token);

        return request;
    }

}
