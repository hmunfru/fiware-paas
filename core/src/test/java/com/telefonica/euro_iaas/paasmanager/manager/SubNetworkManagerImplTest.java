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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.fiware.commons.dao.AlreadyExistsEntityException;
import com.telefonica.fiware.commons.dao.EntityNotFoundException;
import com.telefonica.fiware.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.NetworkDao;
import com.telefonica.euro_iaas.paasmanager.dao.SubNetworkDao;
import com.telefonica.euro_iaas.paasmanager.manager.impl.NetworkManagerImpl;
import com.telefonica.euro_iaas.paasmanager.manager.impl.SubNetworkManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.SubNetwork;

/**
 * Network, SubNetwork and Router Manager.
 * 
 * @author henar
 */
public class SubNetworkManagerImplTest extends TestCase {

    private static String SUB_NETWORK_NAME = "subname";
    private static String CIDR = "10.100.1.0/24";
    private static String CIDR_ID = "1";

    private SubNetworkManagerImpl subNetworkManager;
    private SubNetworkDao subnetworkDao;


    @Override
    @Before
    public void setUp() throws Exception {

    	subNetworkManager = new SubNetworkManagerImpl();
    	subnetworkDao = mock(SubNetworkDao.class);
    	subNetworkManager.setSubNetworkDao(subnetworkDao);
    }

    /**
     * It tests the creation of a network.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateSubNetwork() throws Exception {
        // Given
        SubNetwork subNet = new SubNetwork(SUB_NETWORK_NAME,"vdc", "region");

        // When
        when(subnetworkDao.load(any(String.class), any(String.class), any(String.class))).thenThrow(new EntityNotFoundException(SubNetwork.class, "test", subNet));
        when (subnetworkDao.create(any(SubNetwork.class))).thenReturn(subNet);
        // Verity
        subNet = subNetworkManager.create(subNet);
        assertEquals(subNet.getName(), SUB_NETWORK_NAME);
        verify (subnetworkDao).create(any(SubNetwork.class));
        verify (subnetworkDao).load(any(String.class), any(String.class), any(String.class));


    }

  
    /**
     * It tests the destruction of a network.
     * 
     * @throws Exception
     */
    @Test
    public void testDestroyNetwork() throws Exception {
        // Given
     
        SubNetwork subNet = new SubNetwork(SUB_NETWORK_NAME, "vdc", "region");
        // When
    
        subNetworkManager.delete(subNet);
        
        verify (subnetworkDao).remove(subNet);
        

    }


}
