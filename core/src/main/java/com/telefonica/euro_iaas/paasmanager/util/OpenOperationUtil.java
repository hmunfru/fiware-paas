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

// import org.openstack.docs.compute.api.v1.Server;

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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;
import com.telefonica.euro_iaas.paasmanager.bean.PaasManagerUser;

/**
 * @author jesus.movilla
 */
public interface OpenOperationUtil {
    
    /**
     * pool name in nova *
     */
    // public static final String IPFLOATING_POOL_NAME = "fiprt1";
    /**
     * name of the json type.
     */
    String APPLICATION_JSON = "application/json";
    /**
     * name of the xml type.
     */
    String APPLICATION_XML = "application/xml";
    /**
     * name of the accept header.
     */
    String ACCEPT = "Accept";
    /**
     * name of the content-Type header.
     */
    String CONTENT_TYPE = "Content-Type";
    /**
     * name of the Authentication header.
     */
    String X_AUTH_TOKEN = "X-Auth-Token";
    /**
     * name of the resource Images.
     */
    String RESOURCE_IMAGES = "images/";
    /**
     * name of the resource Flavors.
     */
    String RESOURCE_FLAVORS = "flavors/";
    /**
     * name of the resource Networks.
     */
    String RESOURCE_NETWORKS = "networks";
    
    /**
     * name of the resource Networks.
     */
    String RESOURCE_PORTS = "ports";
    /**
     * name of the resource Subnets.
     */
    String RESOURCE_SUBNETS = "subnets";
    /**
     * name of the resource Subnets.
     */
    String RESOURCE_ROUTERS = "routers";
    /**
     * name of the resource Servers.
     */
    String RESOURCE_SERVERS = "servers";
    /**
     * path for a detailed resource .
     */
    String RESOURCE_DETAIL = "detail";
    /**
     * path for actions.
     */
    String RESOURCE_ACTION = "action";
    /**
     * path for add interfaces to routers.
     */
    String RESOURCE_ADD_INTERFACE = "add_router_interface";
    /**
     * path for remove interfaces to routers.
     */
    String RESOURCE_REMOVE_INTERFACE = "remove_router_interface";

    /**
     * path for floatingIPS.
     */
    String RESOURCE_FLOATINGIP = "os-floating-ips";
    /**
     * name of OpenStack constant name: org.openstack.compute.storage.root. Amount of root Disk.
     */
    String OPENSTACK_COMPUTE_STORAGE_ROOT = "org.openstack.compute.storage.root";
    /**
     * name of OpenStack constant name: org.openstack.compute.storage.root. Amount of root Disk.
     */
    String OPENSTACK_COMPUTE_STORAGE_EPHEMERAL = "org.openstack.compute.storage.ephemeral";
    /**
     * name of OpenStack constant name: org.openstack.compute.swap. Amount of swap.
     */
    String OPENSTACK_COMPUTE_SWAP = "org.openstack.compute.swap";
    /**
     * path for a detailed resource.
     */

    String ERROR_AUTHENTICATION_HEADERS = "Authentication Token, Tenant ID and User must be initialized...";


    PaasManagerUser getAdminUser(PaasManagerUser user) throws OpenStackException; 
    /**
     * It obtains the request for invoking Openstack keystone with admin credentials.
     * 
     * @return
     * @throws OpenStackException
     */
    HttpPost createKeystonePostRequest() throws OpenStackException; 

    ArrayList<Object> executePostRequest(HttpPost postRequest) throws OpenStackException ; 

   

    /**
     * Returns a request for a NOVA DELETE petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    HttpUriRequest createNovaDeleteRequest(String resource, String region, String token, String vdc)
            throws OpenStackException; 
    /**
     * Returns a request for a NOVA GET petition.
     */
    HttpUriRequest createNovaGetRequest(String resource, String accept, String region, String token, String vdc)
            throws OpenStackException ; 
    /**
     * Returns a request for a NOVA POST petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    HttpPost createNovaPostRequest(String resource, String payload, String content, String accept,
            String region, String token, String vdc) throws OpenStackException; 
    /**
     * Returns a request for a QUANTUM DELETE petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    HttpUriRequest createQuantumDeleteRequest(String resource, String region, String vdc, String token)
            throws OpenStackException; 

    /**
     * Returns a request for a Quantum GET petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    HttpUriRequest createQuantumGetRequest(String resource, String accept, String region, String token,
            String vdc) throws OpenStackException; 
    /**
     * Returns a request for a Quantum POST petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    HttpPost createQuantumPostRequest(String resource, String payload, String content, String region,
            String token, String vdc) throws OpenStackException; 
    /**
     * Returns a request for a Quantum POST petition.
     * 
     * @param resource
     *            the target resource
     * @return HttpUriRequest the request
     */
    HttpPut createQuantumPutRequest(String resource, String payload, String content, String region,
            String token, String vdc) throws OpenStackException ;    

    /**
     * Method to execute a request and get the response from NOVA.
     * 
     * @param request
     *            the request to be executed
     * @return HttpUriRequest the response from server
     * @throws OpenStackException
     */
    String executeNovaRequest(HttpUriRequest request) throws OpenStackException ;
	HttpUriRequest createJoinQuantumPostRequestRequest(
			String resourceNetwoksFederated, String payload,
			String applicationJson, String token) throws OpenStackException;

}
