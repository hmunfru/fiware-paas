/**
 * (c) Copyright 2013 Telefonica, I+D. Printed in Spain (Europe). All Rights Reserved.<br>
 * The copyright to the software program(s) is property of Telefonica I+D. The program(s) may be used and or copied only
 * with the express written consent of Telefonica I+D or in accordance with the terms and conditions stipulated in the
 * agreement/contract under which the program(s) have been supplied.
 */

package com.telefonica.euro_iaas.paasmanager.client.services;

import com.telefonica.euro_iaas.paasmanager.model.Task;

/**
 * @author jesus.movilla
 */
public interface TaskService {

    /**
     * Find a task for a given id.
     * 
     * @param vdc
     *            the vdc
     * @param id
     *            the task's id
     * @return the task
     */
    Task load(String vdc, Long id);

    /**
     * Find a task for a given id.
     * 
     * @param url
     *            the url where the task is
     * @return the task
     */
    Task load(String url);

}