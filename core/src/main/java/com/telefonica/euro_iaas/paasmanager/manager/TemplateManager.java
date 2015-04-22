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

package com.telefonica.euro_iaas.paasmanager.manager;

import java.util.List;

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.model.Template;

/**
 * @author jesus.movilla
 */
public interface TemplateManager {

    /**
     * Find the Template using the name.
     * 
     * @param name
     *            the template name
     * @throws EntityNotFoundException
     *             if the template does not exists
     */
    Template load(String name) throws EntityNotFoundException;

    /**
     * Retrieve all Template created in the system.
     * 
     * @return the existent templates.
     */
    List<Template> findAll();

    /**
     * Insert the Template in database
     * 
     * @param the
     *            template to be inserted
     * @return the inserted template
     * @throws InvalidEntityException
     *             , AlreadyExistsEntityException
     */
    Template insert(Template template) throws InvalidEntityException, AlreadyExistsEntityException;

    /**
     * Update the template in database
     * 
     * @param template
     * @return the updated template
     * @throws InvalidEntityException
     */
    Template update(Template template) throws InvalidEntityException;

}
