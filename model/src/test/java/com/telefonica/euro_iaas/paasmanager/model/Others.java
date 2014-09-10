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

package com.telefonica.euro_iaas.paasmanager.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashSet;

import net.sf.json.JSONObject;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import com.telefonica.euro_iaas.paasmanager.model.Task.TaskStates;
import com.telefonica.euro_iaas.paasmanager.model.dto.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import com.telefonica.euro_iaas.paasmanager.model.keystone.Token;
import com.telefonica.euro_iaas.paasmanager.model.keystone.User;

/**
 * Test other model classes.
 *
 * @author henar
 */
public class Others {

    private static final String ORG = "org";
    private static final String VDC = "vdc";
    private static final String SERVICE = "service";

    @Test
    public void testClaudiaData() throws Exception {

        PaasManagerUser user = new PaasManagerUser("username", "myToken", new HashSet<GrantedAuthority>());
        user.setTenantName("FIWARE");
        ClaudiaData claudiaData = new ClaudiaData(ORG, VDC, SERVICE);
        claudiaData.setUser(user);

        assertEquals(claudiaData.getOrg(), ORG);
        assertEquals(claudiaData.getVdc(), VDC);
        assertEquals(claudiaData.getService(), SERVICE);
        assertEquals(claudiaData.getUser().getTenantName(), "FIWARE");
    }

    /**
     * Test the creation of a task.
     * @throws Exception
     */
    @Test
    public void testTasks() throws Exception {

        Task task = new Task(TaskStates.RUNNING);
        task.setHref("href");
        task.setDescription("description");
        task.setEndTime(new Date());
        task.setEnvironment("env");
        TaskError taskError = new TaskError();
        taskError.setMessage("error");
        task.setError(taskError);
        task.setVdc("vdc");


        assertEquals(task.getStatus(), TaskStates.RUNNING);
        assertEquals(task.getHref(), "href");
        assertEquals(task.getDescription(), "description");
        assertEquals(task.getEnvironment(), "env");
        assertEquals(task.getVdc(), "vdc");
        assertEquals(task.getError().getMessage(), "error");
        assertNotNull(task.getEndTime());
        assertNotNull(task.getStartTime());

    }

    /**
     * Test the creation of a Task with tier information.
     * @throws Exception
     */
    @Test
    public void testTasksII() throws Exception {

        Task task = new Task();
        task.setStatus(TaskStates.RUNNING);
        task.setTier("tier");
        task.setExpireTime(new Long(1000));
        task.setOwner(new TaskReference());

        assertEquals(task.getStatus(), TaskStates.RUNNING);
        assertEquals(task.getExpireTime().intValue(), 1000);
        assertNotNull(task.getOwner());


    }

    /**
     * Test the creation of a Task with an error message.
     * @throws Exception
     */
    @Test
    public void testTaskError() throws Exception {


        TaskError taskError = new TaskError();
        taskError.setMessage("error");
        taskError.setMajorErrorCode("majorErrorCode");
        taskError.setMinorErrorCode("minorErrorCode");
        taskError.setVenodrSpecificErrorCode("venodrSpecificErrorCode");

        assertEquals(taskError.getMessage(), "error");
        assertEquals(taskError.getMajorErrorCode(), "majorErrorCode");
        assertEquals(taskError.getMinorErrorCode(), "minorErrorCode");
        assertEquals(taskError.getVenodrSpecificErrorCode(), "venodrSpecificErrorCode");

    }

    /**
     * Test the creation of a error task with a reference.
     * @throws Exception
     */
    @Test
    public void testTaskReference() throws Exception {


        TaskReference taskError = new TaskReference();
        taskError.setName("error");
        taskError.setHref("http");
        taskError.setType("errortype");

        assertEquals(taskError.getName(), "error");
        assertEquals(taskError.getHref(), "http");
        assertEquals(taskError.getType(), "errortype");

    }

    /**
     * Test the creation of a error task with no reference.
     * @throws Exception
     */
    @Test
    public void testTaskReferenceII() throws Exception {


        TaskReference taskError = new TaskReference("http");
        taskError.setName("error");
        taskError.setType("errortype");

        assertEquals(taskError.getName(), "error");
        assertEquals(taskError.getHref(), "http");
        assertEquals(taskError.getType(), "errortype");

    }


    @Test
    public void testLimitFromJson() throws Exception {

        String limits = "  { maxServerMeta: 128, maxPersonality: 5, maxImageMeta: 128, maxPersonalitySize: 10240,"
                + " maxSecurityGroupRules: 20, maxTotalKeypairs: 100, totalRAMUsed: 2560, totalInstancesUsed: 2,"
                + " maxSecurityGroups: 10, totalFloatingIpsUsed: 0, maxTotalCores: 20, totalSecurityGroupsUsed: 0,"
                + " maxTotalFloatingIps: 10, maxTotalInstances: 10, totalCoresUsed: 2, maxTotalRAMSize: 51200 }  ";

        Limits limit = new Limits();
        JSONObject json = new JSONObject().fromObject(limits);
        limit.fromJson(json);

        assertEquals(limit.getTotalSecurityGroups(), new Integer(0));
        assertEquals(limit.getMaxTotalFloatingIps(), new Integer(10));
        assertEquals(limit.getMaxTotalInstances(), new Integer(10));
        assertEquals(limit.getTotalFloatingIpsUsed(), new Integer(0));
        assertEquals(limit.getTotalInstancesUsed(), new Integer(2));
        assertEquals(limit.getTotalSecurityGroups(), new Integer(0));

    }

    @Test
    public void testTemplate() throws Exception {
        TierInstance tierInstance = new TierInstance();
        tierInstance.setName("tiername");
        Template template = new Template();
        template.setName("name");
        template.setTierInstance(tierInstance);
        template.setId(new Long(5));

        assertEquals(template.getName(), "name");
        assertEquals(template.getTierInstance().getName(), "tiername");
        assertEquals(template.getId().intValue(), 5);
    }

    @Test
    public void testTemplateII() throws Exception {
        TierInstance tierInstance = new TierInstance();
        tierInstance.setName("tiername");
        Template template = new Template("name", tierInstance);

        assertEquals(template.getName(), "name");
        assertEquals(template.getTierInstance().getName(), "tiername");
    }

    @Test
    public void testUser() throws Exception {
        User user = new User();
        user.setExtra("extra");
        user.setId("id");
        user.setName("name");

        assertEquals(user.getName(), "name");
        assertEquals(user.getId(), "id");
        assertEquals(user.getExtra(), "extra");
    }

    @Test
    public void testUserII() throws Exception {
        User user = new User("id", "name", "extra");

        assertEquals(user.getName(), "name");
        assertEquals(user.getId(), "id");
        assertEquals(user.getExtra(), "extra");
    }

    @Test
    public void testUserIII() throws Exception {
        User user = new User("id");

        assertEquals(user.getId(), "id");
    }

    @Test
    public void testToken() throws Exception {
        Token token = new Token();
        token.setExpires("expires");
        token.setExtra("extra");
        token.setId("id");
        token.setTenantId("tenantId");

        assertEquals(token.getExpires(), "expires");
        assertEquals(token.getId(), "id");
        assertEquals(token.getExtra(), "extra");
        assertEquals(token.getTenantId(), "tenantId");
    }

    @Test
    public void testTokenII() throws Exception {
        Token token = new Token("id", "expires", "extra");
        assertEquals(token.getExpires(), "expires");
        assertEquals(token.getId(), "id");
        assertEquals(token.getExtra(), "extra");
    }

    @Test
    public void testTokenIII() throws Exception {
        Token token = new Token("id");
        JSONObject json = new JSONObject().fromObject("{\"tenantId\": \"tenantId\"}");

        token.setTenantId(json);
        assertEquals(token.getId(), "id");
        assertEquals(token.getTenantId(), "tenantId");
    }

    @Test
    public void testOS() throws Exception {
        OS os = new OS();
        os.setDescription("description");
        os.setName("name");
        os.setOsType("type");
        os.setVersion("version");


        OS os2 = new OS("type");
        os2.setDescription("description");
        os2.setName("name2");


        assertEquals(os.getName(), "name");
        assertEquals(os.getOsType(), "type");
        assertEquals(os.getVersion(), "version");
        assertEquals(os.getDescription(), "description");
        assertEquals(os.equals(os2), false);
    }

    @Test
    public void testVM() {
        VM vm = new VM();
        vm.setDomain("domain");
        vm.setFloatingIp("flaotinip");
        vm.setFqn("fqn");
        vm.setHostname("hostname");
        vm.setIp("ip");
        vm.setOsType("osType");
        vm.setVmid("vmid");

        assertEquals(vm.getDomain(), "domain");
        assertEquals(vm.getFloatingIp(), "flaotinip");
        assertEquals(vm.getFqn(), "fqn");
        assertEquals(vm.getHostname(), "hostname");
        assertEquals(vm.getIp(), "ip");
        assertEquals(vm.getOsType(), "osType");
        assertEquals(vm.getVmid(), "vmid");
        assertNotNull(vm.toString());

        VM vm2 = new VM("fqn2", "domain", "hostname");
        assertEquals(vm.equals(vm2), false);

    }


}
