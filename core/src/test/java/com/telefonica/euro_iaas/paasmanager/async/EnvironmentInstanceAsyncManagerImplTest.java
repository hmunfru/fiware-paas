/**
 * (c) Copyright 2013 Telefonica, I+D. Printed in Spain (Europe). All Rights Reserved.<br>
 * The copyright to the software program(s) is property of Telefonica I+D. The program(s) may be used and or copied only
 * with the express written consent of Telefonica I+D or in accordance with the terms and conditions stipulated in the
 * agreement/contract under which the program(s) have been supplied.
 */

package com.telefonica.euro_iaas.paasmanager.async;

/**
 * Unit test suite for EnvironmentInstanceAsyncManagerImpl
 * 
 * @author Jesus M. Movilla
 */
public class EnvironmentInstanceAsyncManagerImplTest {

    /*
     * private EnvironmentManager environmentManager; private EnvironmentInstanceManager environmentInstanceManager;
     * private TierInstanceManager tierInstanceManager; private ProductInstanceManager productInstanceManager; private
     * InfrastructureManager infrastructureManager; private TaskNotificator taskNotificator; private TaskManager
     * taskManager; private SystemPropertiesProvider propertiesProvider; private Environment expectedEnvironment;
     * private VM host = new VM("fqn","ip","hostname", "domain"); private VM host2 = new VM ("fqn2","ip2","hostname2",
     * "domain2"); private String vdc = "VDC"; private String extendedOVF; private String callback = "callback"; private
     * Task task; private OS os; private List<VM> vmtestings;
     * @Before public void setUp() throws Exception { InputStream is = ClassLoader.getSystemClassLoader()
     * .getResourceAsStream("4caastovfexample_attributes.xml"); BufferedReader reader = new BufferedReader(new
     * InputStreamReader(is)); StringBuffer ruleFile = new StringBuffer(); String actualString; while ((actualString =
     * reader.readLine()) != null) { ruleFile.append(actualString).append("\n"); } extendedOVF = ruleFile.toString();
     * task= mock(Task.class); taskNotificator = mock (TaskNotificator.class); taskManager = mock (TaskManager.class);
     * when(taskManager.updateTask(any(Task.class))).thenReturn(task); vmtestings = new ArrayList<VM>();
     * vmtestings.add(host); List<VM> vmproductions = new ArrayList<VM>(); vmproductions.add(host);
     * vmproductions.add(host2); infrastructureManager = mock(InfrastructureManager.class);
     * //when(infrastructureManager.getVMs(vdc, new Integer(1))).thenReturn(vmtestings);
     * //when(infrastructureManager.getVMs(vdc, new Integer(2))).thenReturn(vmproductions); os = new OS("os1", "1",
     * "os1 description", "v1"); List<OS> supportedOOSS = new ArrayList<OS>(); List<Tier> tiers = new ArrayList<Tier>();
     * List<ProductRelease> productReleases = new ArrayList<ProductRelease> (); ProductRelease productRelease = new
     * ProductRelease(); productRelease.setDescription("desc"); productRelease.setProductType(new
     * ProductType("name","desc")); productRelease.setVersion("1.0"); productRelease.setName("name");
     * productRelease.setSupportedOOSS(supportedOOSS); productReleases.add(productRelease); Tier tier = new Tier();
     * tier.setName("tiername"); tier.setInitial_number_instances(1); tier.setMaximum_number_instances(1);
     * tier.setMinimum_number_instances(4); tier.setProductReleases(productReleases); tiers.add(tier);
     * expectedEnvironment = new Environment(); expectedEnvironment.setName("name");
     * expectedEnvironment.setEnvironmentType(new EnvironmentType("name", "description"));
     * expectedEnvironment.setTiers(tiers); List<Attribute> attributes = new ArrayList<Attribute>(); ProductInstance
     * expectProductInstance = new ProductInstance(); //expectProductInstance.setName("name");
     * expectProductInstance.setProductRelease(productRelease); expectProductInstance.setStatus(Status.INSTALLED);
     * expectProductInstance.setVm(host); List<ProductInstance> productInstances = new ArrayList<ProductInstance>();
     * productInstances.add(expectProductInstance); TierInstance expectedTierInstance = new TierInstance (tier,
     * productInstances); productInstanceManager = mock (ProductInstanceManager.class);
     * when(productInstanceManager.install(any(VM.class), any(String.class), any(ProductRelease.class),
     * (List<Attribute>) any(Object.class))).thenReturn(expectProductInstance); environmentManager =
     * mock(EnvironmentManager.class); when(environmentManager
     * .load(any(String.class))).thenReturn(expectedEnvironment); tierInstanceManager = mock
     * (TierInstanceManager.class); when(tierInstanceManager.update(any(TierInstance.class)))
     * .thenReturn(expectedTierInstance); List<TierInstance> tierInstances = new ArrayList<TierInstance>();
     * tierInstances.add(expectedTierInstance); environmentInstanceManager = mock(EnvironmentInstanceManager.class);
     * when(environmentInstanceManager.create(any(String.class), any(String.class),any(String.class),
     * any(Environment.class))).thenReturn(new EnvironmentInstance(expectedEnvironment, tierInstances));
     * propertiesProvider = mock(SystemPropertiesProvider.class); when(propertiesProvider
     * .getProperty(any(String.class))).thenReturn("blablablablba"); }
     * @Test public void CreateWhenEverithingIsOk() throws Exception { EnvironmentInstanceAsyncManagerImpl manager = new
     * EnvironmentInstanceAsyncManagerImpl(); manager.setEnvironmentInstanceManager(environmentInstanceManager);
     * manager.setTaskNotificator(taskNotificator); manager.setPropertiesProvider(propertiesProvider);
     * manager.setTaskManager(taskManager); manager.create("org", vdc, "vmtestings", expectedEnvironment, task,
     * callback); // make verifications }
     */
}