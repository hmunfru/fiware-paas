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

package com.telefonica.euro_iaas.paasmanager.installator.sdc.util;

import static com.telefonica.euro_iaas.paasmanager.util.Configuration.SDC_SERVER_MEDIATYPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.util.OpenStackRegion;
import com.telefonica.euro_iaas.sdc.model.Task;

/**
 * @author jesus.movilla
 */
public class SDCUtilImpl implements SDCUtil {

    private SDCClient sDCClient;
    private OpenStackRegion openStackRegion;

    private static Logger log = LoggerFactory.getLogger(SDCUtilImpl.class);
    private static String VM_PATH = "/rest/vm";
    private int MAX_TIME = 60000;

    public void checkTaskStatus(Task task, String token, String vdc) throws ProductInstallatorException {

        String msgerror = null;
        String sdcServerUrl;
        try {
            sdcServerUrl = getSdcUtil();
        } catch (OpenStackException e1) {
            msgerror = "Error to obtain the SDC endpoint or the default region: " + e1.getMessage();
            log.error(msgerror);
            throw new ProductInstallatorException(msgerror);
        }

        log.debug("sdc url " + sdcServerUrl);

        com.telefonica.euro_iaas.sdc.client.services.TaskService taskService = sDCClient.getTaskService(sdcServerUrl,
                SDC_SERVER_MEDIATYPE);

        while (true) {

            task = taskService.load(task.getHref(), vdc, token);

            if (task.getStatus().equals(com.telefonica.euro_iaas.sdc.model.Task.TaskStates.ERROR)) {
                msgerror = "SDCException. " + task.getError().getMajorErrorCode() + ": "
                        + task.getError().getMajorErrorCode() + ". " + task.getError().getMessage();
                log.error(msgerror);
                throw new ProductInstallatorException(msgerror);
            } else if ((task.getStatus().equals(com.telefonica.euro_iaas.sdc.model.Task.TaskStates.RUNNING))
                    || (task.getStatus().equals(com.telefonica.euro_iaas.sdc.model.Task.TaskStates.PENDING))
                    || (task.getStatus().equals(com.telefonica.euro_iaas.sdc.model.Task.TaskStates.QUEUED))) {
                msgerror = "Task status is : " + task.getStatus();
                log.info(msgerror);
            } else if (task.getStatus().equals(com.telefonica.euro_iaas.sdc.model.Task.TaskStates.SUCCESS)) {
                break;
            } else {
                msgerror = "Unexpected task Status SDCException. Description: " + task.getStatus();
                log.error(msgerror);
                throw new ProductInstallatorException(msgerror);
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                msgerror = "SDCException. ThreadInterruptedException when polling SDCIntallation Task";
                log.error(msgerror);
                throw new ProductInstallatorException(msgerror);
            }
        }
    }

    public String getSdcUtil() throws OpenStackException {
        String regionName = openStackRegion.getDefaultRegion();
        return openStackRegion.getSdcEndPoint(regionName);
    }

    /**
     * @param sDCClient
     *            the sDCClient to set
     */
    public void setSDCClient(SDCClient sDCClient) {
        this.sDCClient = sDCClient;
    }

    public void setOpenStackRegion(OpenStackRegion openStackRegion) {
        this.openStackRegion = openStackRegion;
    }

}
