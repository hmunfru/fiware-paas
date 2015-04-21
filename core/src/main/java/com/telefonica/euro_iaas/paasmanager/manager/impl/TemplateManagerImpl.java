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

package com.telefonica.euro_iaas.paasmanager.manager.impl;

import java.util.List;

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.TemplateDao;
import com.telefonica.euro_iaas.paasmanager.manager.TemplateManager;
import com.telefonica.euro_iaas.paasmanager.model.Template;

/**
 * @author jesus.movilla
 */
public class TemplateManagerImpl implements TemplateManager {

    private TemplateDao templateDao;

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.manager.TemplateManager#load(java .lang.String)
     */
    public Template load(String name) throws EntityNotFoundException {
        return templateDao.load(name);
    }

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.manager.TemplateManager#findAll()
     */
    public List<Template> findAll() {
        return templateDao.findAll();
    }

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.manager.TemplateManager#insert(com
     * .telefonica.euro_iaas.paasmanager.model.Template)
     */
    public Template insert(Template template) throws InvalidEntityException, AlreadyExistsEntityException {
        return templateDao.create(template);
    }

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.manager.TemplateManager#update(com
     * .telefonica.euro_iaas.paasmanager.model.Template)
     */
    public Template update(Template template) throws InvalidEntityException {
        return templateDao.update(template);
    }

    /**
     * @param templateDao
     *            the templateDao to set
     */
    public void setTemplateDao(TemplateDao templateDao) {
        this.templateDao = templateDao;
    }

}
