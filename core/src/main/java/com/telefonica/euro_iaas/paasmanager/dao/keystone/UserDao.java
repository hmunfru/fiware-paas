/**
 * (c) Copyright 2013 Telefonica, I+D. Printed in Spain (Europe). All Rights Reserved.<br>
 * The copyright to the software program(s) is property of Telefonica I+D. The program(s) may be used and or copied only
 * with the express written consent of Telefonica I+D or in accordance with the terms and conditions stipulated in the
 * agreement/contract under which the program(s) have been supplied.
 */

package com.telefonica.euro_iaas.paasmanager.dao.keystone;

import java.sql.Connection;
import java.util.List;

import com.telefonica.euro_iaas.paasmanager.model.keystone.User;

/**
 * @author jesus.movilla
 */
public interface UserDao {

    /**
     * List all users from keystone
     * 
     * @return
     */
    List<User> findAll(Connection connection);
}