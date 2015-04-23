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

import static com.telefonica.euro_iaas.paasmanager.util.Configuration.PRODUCT_INSTANCE_PATH;
import static com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider.PAAS_MANAGER_URL;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidProductInstanceRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.NotUniqueResultException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.ProductInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.ProductInstanceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductInstance;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Task.TaskStates;
import com.telefonica.euro_iaas.paasmanager.model.TaskError;
import com.telefonica.euro_iaas.paasmanager.model.TaskReference;
import com.telefonica.euro_iaas.paasmanager.model.TierInstance;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ProductInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;
import com.telefonica.euro_iaas.paasmanager.util.TaskNotificator;

public class ProductInstanceAsyncManagerImpl implements ProductInstanceAsyncManager {

    private static Logger log = LoggerFactory.getLogger(ProductInstanceAsyncManagerImpl.class.getName());
    private ProductInstanceManager productInstanceManager;
    private TaskManager taskManager;
    private SystemPropertiesProvider propertiesProvider;
    private TaskNotificator taskNotificator;
    private EnvironmentInstanceManager environmentInstanceManager;

    public void install(TierInstance tierInstance, ClaudiaData claudiaData, String envName, String vdc,
            ProductRelease productRelease, Set<Attribute> attributes, Task task, String callback)
            throws EntityNotFoundException {

        EnvironmentInstance environmentInstance = environmentInstanceManager.load(vdc, envName);

        try {
            ProductInstance productInstance = productInstanceManager.install(tierInstance, claudiaData,
                    environmentInstance, productRelease);
            log.info("Product " + productRelease.getProduct() + '-' + productRelease.getVersion()
                    + " installed successfully");
        } catch (InvalidProductInstanceRequestException e) {
            String errorMsg = e.getMessage();
            ProductInstance instance = getInstalledProduct(productRelease, tierInstance.getVM());
            if (instance != null) {
                updateErrorTask(instance, task, errorMsg, e);
            } else {
                updateErrorTask(task, errorMsg, e);
            }
        } catch (Exception e) {
            String errorMsg = "The product " + productRelease.getProduct() + "-" + productRelease.getVersion()
                    + " can not be installed in" + tierInstance.getVM();
            ProductInstance instance = getInstalledProduct(productRelease, tierInstance.getVM());
            if (instance != null) {
                updateErrorTask(instance, task, errorMsg, e);
            } else {
                updateErrorTask(task, errorMsg, e);
            }
        } finally {
            notifyTask(callback, task);
        }
    }

    public void uninstall(ClaudiaData data, ProductInstance productInstance, Task task, String callback) {

        try {
            productInstanceManager.uninstall(data, productInstance);
            updateSuccessTask(task, productInstance);
            log.info("Product Release " + productInstance.getProductRelease().getProduct() + "-"
                    + productInstance.getProductRelease().getVersion() + " uninstalled successfully");
        } catch (ProductInstallatorException e) {
            String errorMsg = "Error Unistalling ProductInstance " + e.getMessage();
            log.info(errorMsg);

        } catch (Exception e) {
            String errorMsg = "The product " + productInstance.getProductRelease().getProduct() + "-"
                    + productInstance.getProductRelease().getVersion() + " can not be uninstalled";
        } finally {
            notifyTask(callback, task);
        }

        /*
         * try { productInstanceManager.uninstall(productInstance);
         * updateSuccessTask(task, productInstance); log.info("Product " +
         * productInstance.getProduct().getProduct().getName() + "-" +
         * productInstance.getProduct().getVersion() +
         * " uninstalled successfully"); } catch (FSMViolationException e) {
         * updateErrorTask(productInstance, task, "The product " +
         * productInstance.getId() +
         * " can not be uninstalled due to previous status", e); } catch
         * (ApplicationInstalledException e) { updateErrorTask(productInstance,
         * task, "The product " + productInstance.getId() +
         * " can not be uninstalled due to some applications are" +
         * " installed on it.", e); } catch (NodeExecutionException e) {
         * updateErrorTask(productInstance, task, "The product " +
         * productInstance.getId() +
         * " can not be uninstalled due to an error executing in node.", e); }
         * catch (Throwable e) { updateErrorTask(productInstance, task,
         * "The product " + productInstance.getId() +
         * " can not be uninstalled due to unexpected error.", e); } finally {
         * notifyTask(callback, task); }
         */

    }

    public ProductInstance load(String vdc, String name) throws EntityNotFoundException {
        return productInstanceManager.load(vdc, name);
    }

    // //////// PRIVATE METHODS ///////////

    /*
     * Update the task with necessary information when the task is success.
     */
    private void updateSuccessTask(Task task, ProductInstance productInstance) {
        String piResource = getUrl(productInstance);
        task.setResult(new TaskReference(piResource));
        task.setEndTime(new Date());
        task.setStatus(TaskStates.SUCCESS);
        taskManager.updateTask(task);
    }

    private String getUrl(ProductInstance productInstance) {
        String path = MessageFormat.format(PRODUCT_INSTANCE_PATH, productInstance.getId(), // the
                                                                                           // id

                productInstance.getProductRelease().getProduct(), productInstance.getVdc());

        return propertiesProvider.getProperty(PAAS_MANAGER_URL) + path;
    }

    /*
     * Update the task with necessary information when the task is wrong and the
     * product instance exists in the system.
     */
    private void updateErrorTask(ProductInstance productInstance, Task task, String message, Throwable t) {
        String piResource = getUrl(productInstance);
        task.setResult(new TaskReference(piResource));
        updateErrorTask(task, message, t);
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
        log.info("An error occurs while executing a product action. See task " + task.getHref()
                + "for more information");
    }

    private ProductInstance getInstalledProduct(ProductRelease productRelease, VM vm) {
        ProductInstanceSearchCriteria criteria = new ProductInstanceSearchCriteria();
        // criteria.setVm(vm);
        criteria.setProductRelease(productRelease);
        try {
            return productInstanceManager.loadByCriteria(criteria);
        } catch (EntityNotFoundException e) {
            return null;
        } catch (NotUniqueResultException e) {
            return null;
        }
    }

    private void notifyTask(String url, Task task) {
        if (!StringUtils.isEmpty(url)) {
            taskNotificator.notify(url, task);
        }
    }

    // ////////// I.O.C ////////////

    /**
     * @param productInstanceManager
     *            the productInstanceManager to set
     */
    public void setProductInstanceManager(ProductInstanceManager productInstanceManager) {
        this.productInstanceManager = productInstanceManager;
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

    public void setEnvironmentInstanceManager(EnvironmentInstanceManager environmentInstanceManager) {
        this.environmentInstanceManager = environmentInstanceManager;
    }

}
