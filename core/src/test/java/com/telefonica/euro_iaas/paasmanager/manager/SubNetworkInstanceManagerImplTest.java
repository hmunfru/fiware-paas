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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.paasmanager.claudia.NetworkClient;
import com.telefonica.euro_iaas.paasmanager.dao.SubNetworkInstanceDao;
import com.telefonica.euro_iaas.paasmanager.manager.impl.SubNetworkInstanceManagerImpl;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.SubNetwork;
import com.telefonica.euro_iaas.paasmanager.model.SubNetworkInstance;


/**
 * Network, SubNetwork and Router Manager.
 * 
 * @author henar
 */
public class SubNetworkInstanceManagerImplTest {

    private static String SUB_NETWORK_NAME = "subname";
    private static String CIDR = "10.100.1.0/24";

    private SubNetworkInstanceManagerImpl subNetworkInstanceManager;
    private SubNetworkInstanceDao subNetworkInstanceDao;
    private NetworkClient networkClient = null;


    @Before
    public void setUp() throws Exception {

    	subNetworkInstanceManager = new SubNetworkInstanceManagerImpl();
    	subNetworkInstanceDao = mock(SubNetworkInstanceDao.class);
    	subNetworkInstanceManager.setSubNetworkInstanceDao(subNetworkInstanceDao);
        networkClient = mock(NetworkClient.class);
        subNetworkInstanceManager.setNetworkClient(networkClient);


    }

    /**
     * It tests the creation of a network.
     * 
     * @throws Exception
     */
    @Test
    public void testCreateSubNetwork() throws Exception {
        // Given
        SubNetwork net = new SubNetwork(SUB_NETWORK_NAME, "vdc", "region");
        SubNetworkInstance subnetInst = net.toInstance( "vdc", "region");

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        when(subNetworkInstanceDao.load(any(String.class),any(String.class),any(String.class))).thenThrow(
                new EntityNotFoundException(SubNetwork.class, "test", net));
        Mockito.doNothing().when(networkClient)
                .deploySubNetwork(any(ClaudiaData.class), any(SubNetworkInstance.class), anyString());
        when(subNetworkInstanceDao.create(any(SubNetworkInstance.class))).thenReturn(subnetInst);

        // Verify
        SubNetworkInstance netInstOut = subNetworkInstanceManager.create(claudiaData, subnetInst, "region");
        assertEquals(netInstOut.getName(), SUB_NETWORK_NAME);

    }
    
    @Test(expected=AlreadyExistsEntityException.class)
    public void testCreateSubNetworkError() throws Exception {
        // Given
        SubNetwork net = new SubNetwork(SUB_NETWORK_NAME, "vdc", "region");
        SubNetworkInstance subnetInst = net.toInstance( "vdc", "region");

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        when(subNetworkInstanceDao.exists(any(String.class),any(String.class),any(String.class))).thenReturn(true);
       
      
        // Verify
        subNetworkInstanceManager.create(claudiaData, subnetInst, "region");
        verify (subNetworkInstanceDao.exists(any(String.class),any(String.class),any(String.class)));
        verify (subNetworkInstanceDao.create(any(SubNetworkInstance.class)));
        
    }
    
    @Test
    public void testDeleteSubNet() throws Exception {
        // Given
        SubNetwork net = new SubNetwork(SUB_NETWORK_NAME, "vdc", "region");
        SubNetworkInstance subnetInst = net.toInstance( "vdc", "region");

        ClaudiaData claudiaData = new ClaudiaData("dd", "dd", "service");

        // When
        Mockito.doNothing().when(networkClient).destroySubNetwork(any(ClaudiaData.class), any(SubNetworkInstance.class), anyString());
        Mockito.doNothing().when(subNetworkInstanceDao).remove(any(SubNetworkInstance.class));
        
        // Verify
        subNetworkInstanceManager.delete(claudiaData, subnetInst, "region");
        verify(networkClient).destroySubNetwork(any(ClaudiaData.class), any(SubNetworkInstance.class), anyString());
       
     
    }
    

}
