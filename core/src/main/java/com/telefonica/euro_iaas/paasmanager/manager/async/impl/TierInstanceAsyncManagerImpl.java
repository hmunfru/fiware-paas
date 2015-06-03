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

import static com.telefonica.euro_iaas.paasmanager.util.Configuration.ENVIRONMENT_PATH;
import static com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider.PAAS_MANAGER_URL;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.TaskNotFoundException;
import com.telefonica.euro_iaas.paasmanager.manager.TierInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.TierInstanceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Task.TaskStates;
import com.telefonica.euro_iaas.paasmanager.model.TaskError;
import com.telefonica.euro_iaas.paasmanager.model.TaskReference;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;
import com.telefonica.euro_iaas.paasmanager.util.TaskNotificator;

/**
 * Default implementation.
 * 
 * @author bmmanso
 */

public class TierInstanceAsyncManagerImpl implements TierInstanceAsyncManager {

    private static Logger log = LoggerFactory.getLogger(TierInstanceAsyncManagerImpl.class.getName());

    private TierInstanceManager tierInstanceManager;
    private TaskManager taskManager;
    private SystemPropertiesProvider propertiesProvider;
    private TaskNotificator taskNotificator;

    @Async
    public void create(ClaudiaData claudiaData, TierInstance tierInstance, EnvironmentInstance envInstance, Task task,
            String callback) {
        try {

            tierInstanceManager.create(claudiaData, tierInstance, envInstance, propertiesProvider);
            updateSuccessTask(task, tierInstance);
            log.info("The Tier Instance " + tierInstance.getName() + " has been CORRECTLY provisioned");
        } catch (InfrastructureException e) {
            String errorMsg = " An error has ocurred in the process of creating VMs ";
            updateErrorTaskOnInstall(tierInstance, claudiaData.getVdc(), task, errorMsg, e);
        } catch (TaskNotFoundException e) {
            String errorMsg = "Unable to update task: " + e.getTask().getHref() + ". Description: " + e.getMessage();
            updateErrorTaskOnInstall(tierInstance, claudiaData.getVdc(), task, errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error creating productInstance. Description:" + e.getMessage();
            updateErrorTaskOnInstall(tierInstance, claudiaData.getVdc(), task, errorMsg, e);
        } finally {
            notifyTask(callback, task);
        }
    }

    public void update(ClaudiaData claudiaData, TierInstance tierInstance, EnvironmentInstance envInstance, Task task,
            String callback) {
        try {

            tierInstanceManager.update(claudiaData, tierInstance, envInstance);
            updateSuccessTask(task, tierInstance);
            log.info("The Tier Instance " + tierInstance.getName() + " has been CORRECTLY updated");
        } catch (TaskNotFoundException e) {
            String errorMsg = "Unable to update task: " + e.getTask().getHref() + ". Description: " + e.getMessage();
            updateErrorTaskOnInstall(tierInstance, claudiaData.getVdc(), task, errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error updating productInstance. Description:" + e.getMessage();
            updateErrorTaskOnInstall(tierInstance, claudiaData.getVdc(), task, errorMsg, e);
        } finally {
            notifyTask(callback, task);
        }

    }

    public void delete(ClaudiaData claudiaData, TierInstance tierInstance, EnvironmentInstance envInstance, Task task,
            String callback) {
        try {
            tierInstanceManager.delete(claudiaData, tierInstance, envInstance);
            updateSuccessTask(task, tierInstance);
            log.info("The Tier Instance " + tierInstance.getName() + " has been CORRECTLY deleted");
        } catch (InfrastructureException e) {
            String errorMsg = " An error has ocurred in the process of destroying VM ";
            updateErrorTaskOnInstall(tierInstance, claudiaData.getVdc(), task, errorMsg, e);
        } catch (TaskNotFoundException e) {
            String errorMsg = "Unable to update task: " + e.getTask().getHref() + ". Description: " + e.getMessage();
            updateErrorTaskOnInstall(tierInstance, claudiaData.getVdc(), task, errorMsg, e);
        } catch (InvalidEntityException e) {
            String errorMsg = "Unable to update the tier Instance. Description: " + e.getMessage();
            updateErrorTaskOnInstall(tierInstance, claudiaData.getVdc(), task, errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error removing VM: " + tierInstance + ". Description:" + e.getMessage();
            updateErrorTaskOnInstall(tierInstance, claudiaData.getVdc(), task, errorMsg, e);
        } finally {
            notifyTask(callback, task);
        }
    }

    /**
     * Update the task with necessary information when the task is success.
     * 
     * @param task
     * @param tierInstance
     * @throws TaskNotFoundException
     */
    private void updateSuccessTask(Task task, TierInstance tierInstance) throws TaskNotFoundException {
        Task loadedTask;
        try {
            loadedTask = taskManager.load(task.getId());
        } catch (EntityNotFoundException e) {
            throw new TaskNotFoundException(e.getMessage(), task);
        }
        // Uncommented - Could not commit JPA transaction; nested exception is
        // javax.persistence.RollbackException: Transaction marked as
        // rollbackOnly
        // loadedTask.setResult(new TaskReference(piResource));
        loadedTask.setEndTime(new Date());
        loadedTask.setStatus(TaskStates.SUCCESS);
        taskManager.updateTask(loadedTask);
    }

    /*
     * Update the error task in install operation. The installation is a bit different to the other operations because
     * the instance could exist or not so this method shall do this check.
     */
    private void updateErrorTaskOnInstall(TierInstance tierInstance, String vdc, Task task, String errorMsg, Throwable e) {
        updateErrorTask(tierInstance, vdc, task, errorMsg, e);
    }

    /*
     * Update the task with necessary information when the task is wrong and the product instance exists in the system.
     */
    private void updateErrorTask(TierInstance tierInstance, String vdc, Task task, String message, Throwable t) {

        String aiResource = getUrl(tierInstance);
        task.setResult(new TaskReference(aiResource));
        updateErrorTask(task, message, t);
    }

    private String getUrl(TierInstance tierInstance) {
        String path = MessageFormat.format(ENVIRONMENT_PATH, tierInstance.getName());

        return propertiesProvider.getProperty(PAAS_MANAGER_URL) + path;
    }

    /*
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
        log.info("An error occurs while executing an Tier Instance action. See task " + task.getHref()
                + " for more information " + message);
    }

    private void notifyTask(String url, Task task) {
        if (!StringUtils.isEmpty(url)) {
            taskNotificator.notify(url, task);
        }
    }

    // ////////// I.O.C ////////////
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

    /**
     * @param tierInstanceManager
     *            the TierInstanceManager to set
     */
    public void setTierInstanceManager(TierInstanceManager tierInstanceManager) {
        this.tierInstanceManager = tierInstanceManager;
    }
}
