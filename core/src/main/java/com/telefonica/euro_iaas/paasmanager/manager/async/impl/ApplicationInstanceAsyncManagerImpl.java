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

package com.telefonica.euro_iaas.paasmanager.manager.async.impl;

import static com.telefonica.euro_iaas.paasmanager.util.Configuration.APPLICATION_RELEASE_PATH;
import static com.telefonica.euro_iaas.paasmanager.util.Configuration.ENVIRONMENT_INSTANCE_PATH;
import static com.telefonica.euro_iaas.paasmanager.util.Configuration.PRODUCT_RELEASE_PATH;
import static com.telefonica.euro_iaas.paasmanager.util.Configuration.TASK_PATH;
import static com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider.PAAS_MANAGER_URL;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;

import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductReleaseNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.TaskNotFoundException;
import com.telefonica.euro_iaas.paasmanager.manager.ApplicationInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.ApplicationInstanceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Task.TaskStates;
import com.telefonica.euro_iaas.paasmanager.model.TaskError;
import com.telefonica.euro_iaas.paasmanager.model.TaskReference;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;
import com.telefonica.euro_iaas.paasmanager.util.TaskNotificator;

/**
 * Application Instance Manager operations: install
 * 
 * @author Jesus M. Movilla
 */
public class ApplicationInstanceAsyncManagerImpl implements ApplicationInstanceAsyncManager {

    private static Logger log = LoggerFactory.getLogger(ApplicationInstanceAsyncManagerImpl.class);
    private ApplicationInstanceManager applicationInstanceManager;
    private TaskManager taskManager;
    private SystemPropertiesProvider propertiesProvider;
    private TaskNotificator taskNotificator;
    private EnvironmentInstanceManager environmentInstanceManager;

    /**
     * Install an applicationRelease on an already existent EnvironmentInstance
     * 
     * @param environmentInstanceName
     *            on which applicationRelease is going to be deployed
     * @param applicationRelease
     *            to be deployed
     * @param task
     *            the task which contains the information about the async execution
     * @param callback
     *            if not empty, contains the url where the result of the execution will be sent
     */
    public void install(ClaudiaData data, String environmentInstanceName, ApplicationRelease applicationRelease,
            Task task, String callback) {
        log.info("Install aplication " + applicationRelease.getName() + " " + applicationRelease.getVersion() + " on "
                + " enviornment environmentInstance");

        try {
            EnvironmentInstance environmentInstance = environmentInstanceManager.load(data.getVdc(),
                    environmentInstanceName);

            ApplicationInstance applicationInstance = applicationInstanceManager.install(data, environmentInstance,
                    applicationRelease);
            updateSuccessTask(task, environmentInstance);
            log.info("Application " + applicationRelease.getName() + '-' + applicationRelease.getVersion()
                    + " installed successfully " + " on Environment " + environmentInstanceName);
        } catch (EntityNotFoundException e) {
            String errorMsg = e.getMessage();
            log.error(errorMsg);
            updateErrorTask(environmentInstanceName, ENVIRONMENT_INSTANCE_PATH, task, errorMsg, e);
        } catch (ProductReleaseNotFoundException prNFE) {
            String errorMsg = prNFE.getMessage();
            log.error(errorMsg);
            updateErrorTask(prNFE.getProductRelease().getName(), PRODUCT_RELEASE_PATH, task, errorMsg, prNFE);
        } catch (InvalidEntityException iee) {
            String errorMsg = iee.getMessage();
            log.error(errorMsg);
            updateErrorTask(applicationRelease.getName(), APPLICATION_RELEASE_PATH, task, errorMsg, iee);
        } catch (AlreadyExistsEntityException e) {
        	String errorMsg = "Error installing an application. Description:" + e.getMessage();
            log.error(errorMsg);
        } catch (ProductInstallatorException e) {
            String errorMsg = "Error installing an application. Description:" + e.getMessage();
            log.error(errorMsg);
            updateErrorTask("Error installing an application", PRODUCT_RELEASE_PATH, task, errorMsg, e);
        } catch (TaskNotFoundException e) {
            String errorMsg = "Error installing an application. Description:" + e.getMessage();
            log.error(errorMsg);
            updateErrorTask("Error installing an application", PRODUCT_RELEASE_PATH, task, errorMsg, e);
        } finally {
            notifyTask(callback, task);
        }

    }

    public void uninstall(ClaudiaData data, String environmentInstanceName, String applicationName, Task task,
            String callback) {

        ApplicationInstance applicationInstance = null;
        try {
            applicationInstance = applicationInstanceManager.load(data.getVdc(),applicationName);
            EnvironmentInstance environmentInstance = environmentInstanceManager.load(data.getVdc(),
                    environmentInstanceName);
            applicationInstanceManager.uninstall(data, environmentInstance, applicationInstance);
            updateSuccessTask(task, environmentInstance);
            log.info("Application " + applicationInstance.getName() + '-' + " uninstalled successfully "
                    + " on Environment " + environmentInstanceName);
        } catch (EntityNotFoundException e) {
            String errorMsg = e.getMessage();
            log.error(errorMsg);
            updateErrorTask(environmentInstanceName, ENVIRONMENT_INSTANCE_PATH, task, errorMsg, e);
        } catch (ProductInstallatorException e) {
            String errorMsg = e.getMessage();
            log.error(errorMsg);
            updateErrorTask(environmentInstanceName, ENVIRONMENT_INSTANCE_PATH, task, errorMsg, e);
        } catch (TaskNotFoundException e) {
            String errorMsg = e.getMessage();
            log.error(errorMsg);
            updateErrorTask(environmentInstanceName, ENVIRONMENT_INSTANCE_PATH, task, errorMsg, e);
        }

    }

    private void updateSuccessTask(Task task, EnvironmentInstance environmentInstance) throws TaskNotFoundException {
        Task loadedTask;
        String path = MessageFormat.format(TASK_PATH, environmentInstance.getVdc(), environmentInstance.getName()); // the

        String psyh = propertiesProvider.getProperty(PAAS_MANAGER_URL) + path;
        // vdc

        try {
            loadedTask = taskManager.load(task.getId());
        } catch (EntityNotFoundException e) {
        	log.error(e.getMessage());
            throw new TaskNotFoundException(e.getMessage(), task);
        }

        loadedTask.setEndTime(new Date());
        loadedTask.setStatus(TaskStates.SUCCESS);
        taskManager.updateTask(loadedTask);
    }

    /**
     * Update the task with necessary information when the task is wrong because the environmentInstance does not exist
     * in the system.
     */
    private void updateErrorTask(String resourceName, String baseUrl, Task task, String message, Throwable t) {
        String piResource = MessageFormat.format(baseUrl, resourceName); // the name
        task.setResult(new TaskReference(piResource));
        updateErrorTask(task, message, t);
    }

    /**
     * Update the task with necessary information when the task is wrong.
     */
    private void updateErrorTask(Task task, String message, Throwable t) {
        TaskError error = new TaskError(message);
        error.setMajorErrorCode(t.getMessage());
        error.setMinorErrorCode(t.getClass().getSimpleName());
        task.setEndTime(new Date());
        task.setStatus(TaskStates.ERROR);
        task.setError(error);
        taskManager.updateTask(task);
        log.info("An error occurs while installing an application release on a" + " environment. See task "
                + task.getHref() + " for more information");
    }

    private void notifyTask(String url, Task task) {
        if (!StringUtils.isEmpty(url)) {
            taskNotificator.notify(url, task);
        }
    }

    // ////////// I.O.C ////////////

    /**
     * @param applicationInstanceManager
     *            the productInstanceManager to set
     */
    public void setApplicationInstanceManager(ApplicationInstanceManager applicationInstanceManager) {
        this.applicationInstanceManager = applicationInstanceManager;
    }

    /**
     * @param environmentInstanceManager
     *            the environmentInstanceManager to set
     */
    public void setEnvironmentInstanceManager(EnvironmentInstanceManager environmentInstanceManager) {
        this.environmentInstanceManager = environmentInstanceManager;
    }

    /**
     * @param taskManager
     *            the taskManager to set
     */
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
     * @param propertiesProvider
     *            the propertiesProvider to set
     */
    public void setPropertiesProvider(SystemPropertiesProvider propertiesProvider) {
        this.propertiesProvider = propertiesProvider;
    }

    /**
     * @param taskNotificator
     *            the taskNotificator to set
     */
    public void setTaskNotificator(TaskNotificator taskNotificator) {
        this.taskNotificator = taskNotificator;
    }

}
