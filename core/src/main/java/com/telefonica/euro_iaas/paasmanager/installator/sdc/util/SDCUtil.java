/**
 * (c) Copyright 2013 Telefonica, I+D. Printed in Spain (Europe). All Rights Reserved.<br>
 * The copyright to the software program(s) is property of Telefonica I+D. The program(s) may be used and or copied only
 * with the express written consent of Telefonica I+D or in accordance with the terms and conditions stipulated in the
 * agreement/contract under which the program(s) have been supplied.
 */

package com.telefonica.euro_iaas.paasmanager.installator.sdc.util;

import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.sdc.model.Task;

/**
 * @author jesus.movilla
 */
public interface SDCUtil {

    /**
     * check the status of the productInstance install task
     * 
     * @param task
     * @throws ProductInstallatorException
     */
    public void checkTaskStatus(Task task, String vdc) throws ProductInstallatorException;

}