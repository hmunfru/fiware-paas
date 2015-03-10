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
package com.telefonica.euro_iaas.paasmanager.rest.util;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;

import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extend LoggingFilter in order to disable stderr traces for jersey
 */
public class MyLoggingFilter extends LoggingFilter {

    private static Logger log = LoggerFactory.getLogger(MyLoggingFilter.class);

    public MyLoggingFilter() {
        super(null, true);
        System.out.println("MyLoggingFilter running");
    }

    @Override
    public void filter(ClientRequestContext context) throws IOException {
        // Do nothing.
        System.out.println("MyLoggingFilter: filter. do nothing.");
    }

}
