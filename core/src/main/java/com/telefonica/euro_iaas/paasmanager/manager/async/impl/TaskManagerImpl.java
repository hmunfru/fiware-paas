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

import static com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider.PAAS_MANAGER_URL;
import static com.telefonica.euro_iaas.paasmanager.util.Configuration.TASK_PATH;

import java.text.MessageFormat;
import java.util.List;

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.dao.TaskDao;
import com.telefonica.euro_iaas.paasmanager.exception.PaasManagerServerRuntimeException;
import com.telefonica.euro_iaas.paasmanager.manager.async.TaskManager;
import com.telefonica.euro_iaas.paasmanager.model.Task;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TaskSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * Default TaskManager implementation.
 * 
 * @author Jesus M.Movilla
 */
public class TaskManagerImpl implements TaskManager {

    private TaskDao taskDao;
    private SystemPropertiesProvider propertiesProvider;

    /**
     * {@inheritDoc}
     */
    public Task createTask(Task task) {
        try {
            task = taskDao.create(task);            
            task.setHref(getTaskUrl (task));
            return task;
        } catch (AlreadyExistsEntityException e) {
            throw new PaasManagerServerRuntimeException(e);
        }
    }
    
    private String getTaskUrl (Task task) {
        String path = MessageFormat.format(TASK_PATH,
                Long.valueOf(task.getId())
                .toString(), task.getVdc()); // th
        
        return propertiesProvider.getProperty(PAAS_MANAGER_URL) + path;
    }

    /**
     * {@inheritDoc}
     */
    public Task updateTask(Task task) {
        try {
            task = taskDao.update(task);
            task.setHref(getTaskUrl (task));
            return task;
        } catch (Exception e) {
            throw new PaasManagerServerRuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Task load(Long id) throws EntityNotFoundException {
        Task task = taskDao.load(id);
        task.setHref(getTaskUrl (task));
        return task;
    }

    /**
     * {@inheritDoc}
     */
    public List<Task> findByCriteria(TaskSearchCriteria criteria) {
        List<Task> tasks = taskDao.findByCriteria(criteria);
        for (Task task : tasks) {
            task.setHref(getTaskUrl (task));
        }
        return tasks;
    }

    // //////// I.O.C ////////

    /**
     * @param taskDao
     *            the taskDao to set
     */
    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    /**
     * @param propertiesProvider
     *            the propertiesProvider to set
     */
    public void setPropertiesProvider(SystemPropertiesProvider propertiesProvider) {
        this.propertiesProvider = propertiesProvider;
    }

}
