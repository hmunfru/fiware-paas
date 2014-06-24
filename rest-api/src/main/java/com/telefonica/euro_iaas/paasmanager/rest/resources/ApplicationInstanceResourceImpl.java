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

package com.telefonica.euro_iaas.paasmanager.rest.resources;

import java.text.MessageFormat;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.manager.ApplicationInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.ApplicationReleaseManager;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.ApplicationInstanceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.EnvironmentInstanceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationInstance;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Task.TaskStates;
import com.telefonica.euro_iaas.paasmanager.model.dto.ApplicationReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.ApplicationInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;
import com.telefonica.euro_iaas.paasmanager.rest.validation.ApplicationInstanceResourceValidator;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Default ApplicationInstanceResource implementation.
 * 
 * @author Jesus M. Movilla
 */
@Path("/envInst/org/{org}/vdc/{vdc}/environmentInstance/{environmentInstance}/applicationInstance")
@Component
@Scope("request")
public class ApplicationInstanceResourceImpl implements ApplicationInstanceResource {

    private static Logger log = LoggerFactory.getLogger(ApplicationInstanceResourceImpl.class);

    private ApplicationInstanceAsyncManager applicationInstanceAsyncManager;

    private ApplicationInstanceManager applicationInstanceManager;

    private ApplicationReleaseManager applicationReleaseManager;

    private EnvironmentInstanceAsyncManager environmentInstanceAsyncManager;

    private TaskManager taskManager;

    private EnvironmentInstanceManager environmentInstanceManager;

    private ApplicationInstanceResourceValidator validator;
    
    @Autowired
    private SystemPropertiesProvider systemPropertiesProvider;

    /**
     * 
     */
    public Task install(String org, String vdc, String environmentInstance,

    ApplicationReleaseDto applicationReleaseDto, String callback) throws APIException {
        log.debug("Install aplication " + applicationReleaseDto.getApplicationName() + " "
                + applicationReleaseDto.getVersion() + " on " + " enviornment " + environmentInstance
                + " with artificats " + applicationReleaseDto.getArtifactsDto().size());

        ClaudiaData claudiaData = new ClaudiaData(org, vdc, environmentInstance);
        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
            claudiaData.setUser(getCredentials());
        }
        
        Task task = null;
        try {
        	log.debug("Validating install "+  applicationReleaseDto.getApplicationName() + " "
                    + applicationReleaseDto.getVersion());
        	log.debug(validator.toString());
            validator.validateInstall(vdc, environmentInstance, applicationReleaseDto);
            log.debug("Application validated");
        } catch (Exception ex) {
            throw new APIException(ex);
        }

        ApplicationRelease applicationRelease = applicationReleaseDto.fromDto();

        task = createTask(
                MessageFormat.format("Deploying application {0} in environment instance {1}",
                        applicationRelease.getName(), environmentInstance), vdc);

        log.debug (MessageFormat.format("Deploying application {0} in environment instance {1}",
                applicationRelease.getName(), environmentInstance), vdc);
        applicationInstanceAsyncManager.install(claudiaData, environmentInstance, applicationRelease, task, callback);

        return task;

    }

    public List<ApplicationInstance> findAll(Integer page, Integer pageSize, String orderBy, String orderType,
            List<Status> status, String vdc, String environmentInstance) {
    	log.debug ("Find applications for " + environmentInstance + " page " + page + " pageSize " + pageSize + " orderby" + orderBy
    			+ " orderType " + orderType);
        ApplicationInstanceSearchCriteria criteria = new ApplicationInstanceSearchCriteria();

        criteria.setVdc(vdc);
        criteria.setEnvironmentInstance(environmentInstance);


        if (page != null && pageSize != null) {
            criteria.setPage(page);
            criteria.setPageSize(pageSize);
        }
        if (!StringUtils.isEmpty(orderBy)) {
            criteria.setOrderBy(orderBy);
        }
        if (!StringUtils.isEmpty(orderType)) {
            criteria.setOrderBy(orderType);
        }
        log.debug("Find criteria for application instance");
        List<ApplicationInstance> appInstances = applicationInstanceManager.findByCriteria(criteria);

        return appInstances;

    }

    public Task uninstall(String org, String vdc, String environmentName, String applicationName, String callback) {

        ClaudiaData claudiaData = new ClaudiaData(org, vdc, environmentName);
        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
            claudiaData.setUser(getCredentials());
        }
            
        try {
             validator.validateUnInstall(vdc, environmentName, applicationName);
             log.debug("Application validated");
        } catch (Exception ex) {
             throw new APIException(ex);
       }

       Task task = createTask(MessageFormat.format("Uninstalling application Instance {0} ", applicationName), vdc);
       applicationInstanceAsyncManager.uninstall(claudiaData, environmentName, applicationName, task, callback);
       return task;

    }

    /**
     * Find an applicationinstance by name and vdc
     * 
     * @param vdc
     *            , the vdc the app belongs to
     * @param name
     *            , the applicationInstanceName
     * @return the applicationInstance
     */
    public ApplicationInstance load(String vdc, String enviroment, String name) {
        try {
            ApplicationInstance appInstance = applicationInstanceManager.load(vdc, name);
            return appInstance;

        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(e, 404);
        }
    }

    private Task createTask(String description, String vdc) {
        Task task = new Task(TaskStates.RUNNING);
        task.setDescription(description);
        task.setVdc(vdc);
        return taskManager.createTask(task);
    }
    
    public void addCredentialsToClaudiaData(ClaudiaData claudiaData) {
    	log.debug ("add credentials");
        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
        	log.debug ("set users");
            claudiaData.setUser(getCredentials());
            claudiaData.getUser().setTenantId(claudiaData.getVdc());
        }

    }
    
    public PaasManagerUser getCredentials() {
        if (systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")) {
            return (PaasManagerUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {
            return null;
        }

    }

    /**
     * @param validator
     *            the validator to set
     */
    public void setValidator(ApplicationInstanceResourceValidator validator) {
        this.validator = validator;
    }

    public void setEnvironmentInstanceAsyncManager(EnvironmentInstanceAsyncManager environmentInstanceAsyncManager) {
        this.environmentInstanceAsyncManager = environmentInstanceAsyncManager;
    }

    public void setEnvironmentInstanceManager(EnvironmentInstanceManager environmentInstanceManager) {
        this.environmentInstanceManager = environmentInstanceManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void setApplicationInstanceAsyncManager(ApplicationInstanceAsyncManager applicationInstanceAsyncManager) {
        this.applicationInstanceAsyncManager = applicationInstanceAsyncManager;
    }

    public void setApplicationInstanceManager(ApplicationInstanceManager applicationInstanceManager) {
        this.applicationInstanceManager = applicationInstanceManager;
    }

    public void setApplicationReleaseManager(ApplicationReleaseManager applicationReleaseManager) {
        this.applicationReleaseManager = applicationReleaseManager;
    }
    
    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {
        this.systemPropertiesProvider = systemPropertiesProvider;
    }

}
