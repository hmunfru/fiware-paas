/**
 * (c) Copyright 2013 Telefonica, I+D. Printed in Spain (Europe). All Rights Reserved.<br>
 * The copyright to the software program(s) is property of Telefonica I+D. The program(s) may be used and or copied only
 * with the express written consent of Telefonica I+D or in accordance with the terms and conditions stipulated in the
 * agreement/contract under which the program(s) have been supplied.
 */

package com.telefonica.euro_iaas.paasmanager.rest.resources;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Path;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.Task.TaskStates;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TaskSearchCriteria;

/**
 * Default TaskResource implementation.
 * 
 * @author Jesus M. Movilla
 */
@Path("/vdc/{vdc}/task")
@Component
@Scope("request")
public class TaskResourceImpl implements TaskResource {

    @InjectParam("taskManager")
    private TaskManager taskManager;

    // Errores con el Override

    public Task load(Long id) throws EntityNotFoundException {
        return taskManager.load(id);
    }

    public List<Task> findAll(Integer page, Integer pageSize, String orderBy, String orderType,
            List<TaskStates> states, String resource, String owner, Date fromDate, Date toDate, String vdc) {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setVdc(vdc);
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
        criteria.setStates(states);
        criteria.setResource(resource);
        criteria.setOwner(owner);
        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);
        return taskManager.findByCriteria(criteria);
    }

    public List<Task> findTierInstance(Integer page, Integer pageSize, String orderBy, String orderType,
            List<TaskStates> states, String resource, String owner, Date fromDate, Date toDate, String vdc,
            String environment, String tierInstance) {

        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setVdc(vdc);
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
        criteria.setStates(states);
        criteria.setResource(resource);
        criteria.setOwner(owner);
        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);
        criteria.setEnvironment(environment);
        criteria.setTier(tierInstance);
        return taskManager.findByCriteria(criteria);

    }

    public List<Task> findEnvironment(Integer page, Integer pageSize, String orderBy, String orderType,
            List<TaskStates> states, String resource, String owner, Date fromDate, Date toDate, String vdc,
            String environment) {

        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setVdc(vdc);
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
        criteria.setStates(states);
        criteria.setResource(resource);
        criteria.setOwner(owner);
        criteria.setFromDate(fromDate);
        criteria.setToDate(toDate);
        criteria.setEnvironment(environment);
        return taskManager.findByCriteria(criteria);
    }

}