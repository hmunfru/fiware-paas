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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

/**
 * Test Case related to ErrorHandler class.
 */
public class ErrorHandlerTest {

    /**
     * Test the response with a valid error without message in json format.
     * @throws IOException
     */
    @Test
    public void shouldReturnResponseWithValidErrorJSONWithoutMessage() throws IOException {
        // given

        ErrorHandler errorHandler = new ErrorHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getAttribute("javax.servlet.error.message")).thenReturn("");
        when(request.getAttribute("javax.servlet.error.status_code")).thenReturn(200);

        // when

        String message = errorHandler.createResponseMessage(request, response);

        // then
        assertNotNull(message);
        assertEquals("{\"errors\":[ {\"message\":\"Internal PaasManager Server error\",\"code\":500}]}", message);
        verify(response).setStatus(500);
    }
}
