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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.model.ApplicationRelease;

/**
 * @author jesus.movilla
 */
public interface ApplicationReleaseResource {

    /**
     * Retrieve all application releases.
     * @param artifactName  The name of the artifact.
     * @param page  The page in which show the data or extract the data.
     * @param pageSize  The page size of the information to show.
     * @param orderBy   The order type to apply to the data to show.
     * @param orderType The type of order to apply in order to show the data.
     * @return  The list of the applicaiton releases.
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    List<ApplicationRelease> findAll(@QueryParam("artifactName") String artifactName, @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize, @QueryParam("orderBy") String orderBy,
            @QueryParam("orderType") String orderType);

    /**
     * Retrieve the selected Application Releases.
     * 
     * @param name
     *            the application Release name
     * @return the loaded application.
     * @throws EntityNotFoundException
     *             if the osInstance does not exists
     */
    @GET
    @Path("/{appName}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    ApplicationRelease load(@PathParam("appName") String name) throws EntityNotFoundException;

}
