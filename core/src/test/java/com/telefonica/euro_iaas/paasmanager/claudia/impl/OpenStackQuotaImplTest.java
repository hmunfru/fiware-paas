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

package com.telefonica.euro_iaas.paasmanager.claudia.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.OpenStackException;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Limits;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.util.OpenStackUtil;

public class OpenStackQuotaImplTest {

    @Test
    public void shouldGetLimitsFromExistingTenantId() throws OpenStackException, InfrastructureException {

        // given
        OpenStackQuotaClientImpl openStackQuotaImpl = new OpenStackQuotaClientImpl();

        ClaudiaData claudiaData = new ClaudiaData("org", "tenantId", "service");
        PaasManagerUser user = new PaasManagerUser("user", "pass");
        user.setToken("token");
        claudiaData.setUser(user);
        OpenStackUtil openStackUtil = mock(OpenStackUtil.class);
        openStackQuotaImpl.setOpenStackUtil(openStackUtil);
        // when
        String response = "{\"limits\": {\"rate\": [{\"regex\": \".*\", \"limit\": [{\"next-available\": \"2013-10-29T15:17:15Z\", \"unit\": \"MINUTE\", \"verb\": \"POST\", \"remaining\": 7, \"value\": 10}, {\"next-available\": \"2013-10-29T15:18:22Z\", \"unit\": \"MINUTE\", \"verb\": \"PUT\", \"remaining\": 10, \"value\": 10}, {\"next-available\": \"2013-10-29T15:15:55Z\", \"unit\": \"MINUTE\", \"verb\": \"DELETE\", \"remaining\": 98, \"value\": 100}], \"uri\": \"*\"}, {\"regex\": \"^/servers\", \"limit\": [{\"next-available\": \"2013-10-29T15:18:22Z\", \"unit\": \"DAY\", \"verb\": \"POST\", \"remaining\": 50, \"value\": 50}], \"uri\": \"*/servers\"}, {\"regex\": \".*changes-since.*\", \"limit\": [{\"next-available\": \"2013-10-29T15:18:22Z\", \"unit\": \"MINUTE\", \"verb\": \"GET\", \"remaining\": 3, \"value\": 3}], \"uri\": \"*changes-since*\"}, {\"regex\": \"^/os-fping\", \"limit\": [{\"next-available\": \"2013-10-29T15:18:22Z\", \"unit\": \"HOUR\", \"verb\": \"GET\", \"remaining\": 12, \"value\": 12}], \"uri\": \"*/os-fping\"}], \"absolute\": {\"maxServerMeta\": 128, \"maxPersonality\": 5, \"maxImageMeta\": 128, \"maxPersonalitySize\": 10240, \"maxSecurityGroupRules\": 20, \"maxTotalKeypairs\": 100, \"totalRAMUsed\": 4096, \"totalInstancesUsed\": 2, \"maxSecurityGroups\": 10, \"totalFloatingIpsUsed\": 0, \"maxTotalCores\": 20, \"totalSecurityGroupsUsed\": 0, \"maxTotalFloatingIps\": 10, \"maxTotalInstances\": 10, \"totalCoresUsed\": 2, \"maxTotalRAMSize\": 51200}}}";
        when(openStackUtil.getAbsoluteLimits(any(PaasManagerUser.class), anyString())).thenReturn(response);

        Limits limits = openStackQuotaImpl.getLimits(claudiaData, "region");

        // then
        assertNotNull(limits);
        verify(openStackUtil).getAbsoluteLimits(any(PaasManagerUser.class), anyString());
        assertEquals(new Integer(10), limits.getMaxTotalFloatingIps());
        assertEquals(new Integer(0), limits.getTotalFloatingIpsUsed());
        assertEquals(new Integer(10), limits.getMaxTotalInstances());
        assertEquals(new Integer(2), limits.getTotalInstancesUsed());
    }
}
