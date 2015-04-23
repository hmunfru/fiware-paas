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

package com.telefonica.euro_iaas.paasmanager.claudia.impl;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.paasmanager.claudia.QuotaClient;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.fiware.commons.openstack.auth.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Limits;
import com.telefonica.euro_iaas.paasmanager.util.OpenStackUtil;

public class OpenStackQuotaClientImpl implements QuotaClient {

    private static Logger log = LoggerFactory.getLogger(OpenStackQuotaClientImpl.class);
    private OpenStackUtil openStackUtil = null;

    public OpenStackUtil getOpenStackUtil() {
        return openStackUtil;
    }

    public void setOpenStackUtil(OpenStackUtil openStackUtil) {
        this.openStackUtil = openStackUtil;
    }

    /**
     * Connect to openstack and get absolute limits.
     * 
     * @param claudiaData
     * @return
     * @throws InfrastructureException
     */
    public Limits getLimits(ClaudiaData claudiaData, String region) throws InfrastructureException {

        try {
            String result = openStackUtil.getAbsoluteLimits(claudiaData.getUser(), region);

            JSONObject jsonObject = JSONObject.fromObject(result);

            String limitsString = jsonObject.getString("limits");

            JSONObject limitsJson = JSONObject.fromObject(limitsString);

            String absoluteString = limitsJson.getString("absolute");
            JSONObject absoluteJson = JSONObject.fromObject(absoluteString);

            Limits limits = new Limits();
            limits.fromJson(absoluteJson);
            return limits;

        } catch (OpenStackException e) {
            String message = "Error to obtain the absolute limits of the environment " + e.getMessage();
            log.error(message);
            throw new InfrastructureException(message, e);
        }

    }
}
