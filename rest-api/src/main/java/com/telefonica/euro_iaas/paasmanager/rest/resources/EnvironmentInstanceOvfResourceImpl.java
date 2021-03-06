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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.manager.EnvironmentInstanceManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.EnvironmentInstanceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
import com.telefonica.euro_iaas.paasmanager.manager.async.VirtualServiceAsyncManager;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.EnvironmentInstance;
import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Task.TaskStates;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentInstanceOvfDto;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.EnvironmentInstanceSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.rest.exception.APIException;
import com.telefonica.euro_iaas.paasmanager.rest.util.ExtendedOVFUtil;
import com.telefonica.euro_iaas.paasmanager.rest.util.OVFMacro;
import com.telefonica.euro_iaas.paasmanager.rest.validation.EnvironmentInstanceResourceValidator;

/**
 * @author jesus.movilla
 */
@Path("/ovf/org/{org}/vdc/{vdc}/environmentInstance")
@Component
@Scope("request")
public class EnvironmentInstanceOvfResourceImpl implements EnvironmentInstanceOvfResource {

    private static Logger log = Logger.getLogger(EnvironmentInstanceOvfResourceImpl.class);

    @InjectParam("environmentInstanceAsyncManager")
    private EnvironmentInstanceAsyncManager environmentInstanceAsyncManager;
    @InjectParam("environmentInstanceManager")
    private EnvironmentInstanceManager environmentInstanceManager;

    @InjectParam("taskManager")
    private TaskManager taskManager;
    @InjectParam("virtualServiceAsyncManager")
    private VirtualServiceAsyncManager virtualServiceAsyncManager;

    private EnvironmentInstanceResourceValidator validator;

    private ExtendedOVFUtil extendedOVFUtil;
    private OVFMacro ovfMacro;

    public Task create(String org, String vdc, String payload, String callback) throws APIException {

        Task task = null;
        List<String> virtualServices = new ArrayList<String>();

        log.debug("Deploy environmetn instace from a payload ");
        log.debug(payload);

        try {
            if (!extendedOVFUtil.isVirtualServicePayload(payload)) {

                // Validations
                validator.validateCreatePayload(payload);
                log.debug("Validating OVF payload : OK");
                String instantiateOvfParams = extendedOVFUtil.getEnvironmentName(payload);
                Set<Tier> tiers = extendedOVFUtil.getTiers(payload, vdc);

                Environment environment = new Environment();
                environment.setOvf(payload);
                environment = ovfMacro.resolveMacros(environment);
                environment.setVdc(vdc);
                environment.setOrg(org);
                environment.setName(instantiateOvfParams);
                environment.setDescription("Environment " + environment.getName());
                environment.setTiers(tiers);

                log.debug("Environment name " + environment.getName() + " " + environment.getVdc() + " "
                        + environment.getOrg() + " ");
                for (Tier tier : tiers) {
                    log.debug("Tier " + tier.getName() + " image " + tier.getImage());
                }

                ClaudiaData claudiaData = new ClaudiaData(org, vdc, instantiateOvfParams);
                claudiaData.setUser(extendedOVFUtil.getCredentials());

                task = createTask(MessageFormat.format("Create environment {0}", environment.getName()), vdc,
                        environment.getName());

                EnvironmentInstance environmentInstance = new EnvironmentInstance();
                environmentInstance.setVdc(vdc);
                environmentInstance.setStatus(Status.INIT);
                environmentInstance.setBlueprintName(instantiateOvfParams);
                environmentInstance.setDescription("description");
                environmentInstance.setEnvironment(environment);

                log.debug("EnvironmentInstance name " + environmentInstance.getBlueprintName() + " vdc "
                        + environmentInstance.getVdc() + "  description " + environmentInstance.getDescription()
                        + "  status " + environmentInstance.getStatus() + " environment  "
                        + environmentInstance.getEnvironment().getName());

                environmentInstanceAsyncManager.create(claudiaData, environmentInstance, task, callback);
            } else {
                virtualServices = extendedOVFUtil.getVirtualServices(payload);

                task = createTaskVS(
                        MessageFormat.format("Install Virtual Service environment {0}", "virtualServiceCollection"),
                        "VirtualService");

                for (int i = 0; i < virtualServices.size(); i++) {
                    String virtualServiceName = extendedOVFUtil.getVirtualServiceName(virtualServices.get(i));

                    virtualServiceAsyncManager.create(virtualServiceName, virtualServices.get(i), task, callback);
                }
            }
            return task;
        } catch (Exception ex) {
            throw new APIException(ex);
        }
    }

    public List<EnvironmentInstanceOvfDto> findAll(Integer page, Integer pageSize, String orderBy, String orderType,
            List<Status> status, String vdc) {

        EnvironmentInstanceSearchCriteria criteria = new EnvironmentInstanceSearchCriteria();

        criteria.setVdc(vdc);
        criteria.setStatus(status);

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

        List<EnvironmentInstance> envInstances = environmentInstanceManager.findByCriteria(criteria);

        List<EnvironmentInstanceOvfDto> environmentInstanceOvfDtos = new ArrayList<EnvironmentInstanceOvfDto>();
        for (int i = 0; i < envInstances.size(); i++) {

            environmentInstanceOvfDtos.add(new EnvironmentInstanceOvfDto(envInstances.get(i).getVapp()));
        }
        return environmentInstanceOvfDtos;
    }

    /**
     * 
     */
    public String load(String vdc, String name) {
        try {
            EnvironmentInstance envInstance = environmentInstanceManager.load(vdc, name);
            return envInstance.getVapp();
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(e, 404);
        }
    }

    public Task destroy(String org, String vdc, String name, String callback) {
        try {
            EnvironmentInstance environmentInstance = environmentInstanceManager.load(vdc, name);
            ClaudiaData claudiaData = new ClaudiaData(org, vdc, environmentInstance.getName());

            Task task = createTask(MessageFormat.format("Destroying EnvironmentInstance {0} ", name), vdc,
                    environmentInstance.getName());
            environmentInstanceAsyncManager.destroy(claudiaData, environmentInstance, task, callback);
            return task;
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(e, 404);
        }
    }

    /**
     * createTask
     * 
     * @param description
     * @param vdc
     * @return
     */
    private Task createTask(String description, String vdc, String env) {
        Task task = new Task(TaskStates.RUNNING);
        task.setDescription(description);
        task.setVdc(vdc);
        task.setEnvironment(env);
        return taskManager.createTask(task);
    }

    private Task createTaskVS(String description, String vdc) {
        Task task = new Task(TaskStates.RUNNING);
        task.setDescription(description);
        task.setVdc(vdc);
        return taskManager.createTask(task);
    }

    /**
     * @param validator
     *            the validator to set
     */
    public void setValidator(EnvironmentInstanceResourceValidator validator) {
        this.validator = validator;
    }

    /**
     * @param extendedOVFUtil
     *            the extendedOVFUtil to set
     */
    public void setExtendedOVFUtil(ExtendedOVFUtil extendedOVFUtil) {
        this.extendedOVFUtil = extendedOVFUtil;
    }

    /**
     * @param ovfMacro
     *            the ovfMacro to set
     */
    public void setOvfMacro(OVFMacro ovfMacro) {
        this.ovfMacro = ovfMacro;
    }

    public void setEnvironmentInstanceAsyncManager(EnvironmentInstanceAsyncManager environmentInstanceAsyncManager) {
        this.environmentInstanceAsyncManager = environmentInstanceAsyncManager;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

}
