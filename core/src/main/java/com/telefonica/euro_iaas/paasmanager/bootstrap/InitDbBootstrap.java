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

package com.telefonica.euro_iaas.paasmanager.bootstrap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Populates data base with synthetic data to emulate the preconditions of paas manager.
 * 
 * @author Jesus M. Movilla
 * @version $Id: $
 */
// TODO delete this class when the preconditions are done.
public class InitDbBootstrap implements ServletContextListener {

    private static Logger log = LoggerFactory.getLogger(InitDbBootstrap.class);

    /** {@inheritDoc} */
    public void contextDestroyed(ServletContextEvent event) {

    }

    /** {@inheritDoc} */
    public void contextInitialized(ServletContextEvent event) {
        log.debug("InitDbBootstrap. START");
        log.debug("InitDbBootstrap. END");
    }
}
