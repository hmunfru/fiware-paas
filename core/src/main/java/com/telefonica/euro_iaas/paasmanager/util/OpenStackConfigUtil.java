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

import org.json.JSONException;

import com.telefonica.fiware.commons.openstack.auth.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.NetworkInstance;
import com.telefonica.euro_iaas.paasmanager.model.RouterInstance;
import com.telefonica.euro_iaas.paasmanager.bean.PaasManagerUser;

/**
 * @author henar munoz
 */
public interface OpenStackConfigUtil {
    
    /**
     * name of the xml type.
     */
    String APPLICATION_XML = "application/xml";
    
    String APPLICATION_JSON = "application/json";

    /**
     * name of the resource Networks.
     */
    String RESOURCE_NETWORKS = "networks";

    /**
     * name of the resource Subnets.
     */
    String RESOURCE_ROUTERS = "routers";



    /**
     * 
     * @param user
     * @param region
     * @return
     * @throws OpenStackException
     */
    String getPublicAdminNetwork(PaasManagerUser user,  String region)
        throws OpenStackException ;

    /**
     * 
     * @param user
     * @param region
     * @return
     * @throws OpenStackException 
     */
    String getPublicRouter(PaasManagerUser user,  String region, String publicNetworkId) throws OpenStackException;
    
    /**
     * 
     * @param user
     * @param region
     * @return
     * @throws OpenStackException 
     */
    String getPublicFloatingPool(PaasManagerUser user,  String region) throws OpenStackException;
    
    String getPublicRouterId (String response, String vdc, String publicNetworkId) throws JSONException;

}
