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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;
import com.telefonica.euro_iaas.paasmanager.model.dto.ApplicationInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ApplicationReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ArtifactDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for environmetn instances.
 *
 * @author henar
 */
public class ApplicationInstanceTest extends TestCase {

    private static final String ARTIFACT_NAME = "ARTIFACT_NAME";
    private static final String ARTIFACT_PATH = "ARTIFACT_PATH";
    private static final String APP_NAME = "APP_NAME";
    private static final String APP_VERSION = "APP_VERSION";
    private static final String PRODUCT_NAME = "PRODUCT_NAME";
    private static final String PRODUCT_VERSION = "PRODUCT_VERSION";

    private EnvironmentInstance envIns = null;
    private Environment envResult;


    @Override
    @Before
    public void setUp() throws Exception {

        ProductRelease productRelease = new ProductRelease("product", "2.0");
        List<ProductRelease> productReleases = new ArrayList<ProductRelease>();
        productReleases.add(productRelease);

        Tier tier = new Tier();
        tier.setInitialNumberInstances(new Integer(1));
        tier.setMaximumNumberInstances(new Integer(5));
        tier.setMinimumNumberInstances(new Integer(1));
        tier.setName("tierName");
        tier.setProductReleases(productReleases);
        tier.setFlavour("3");
        tier.setFloatingip("true");
        tier.setImage("image");

        Tier tier2 = new Tier("tierName2", new Integer(1), new Integer(1), new Integer(1), productReleases, "2",
                "image", "icone");

        Set<Tier> tiers = new HashSet<Tier>();
        tiers.add(tier);
        tiers.add(tier2);

        envResult = new Environment();
        envResult = new Environment();
        envResult.setName("environemntName");
        envResult.setTiers(tiers);

        envIns = new EnvironmentInstance("blue", "description");

        envIns.setEnvironment(envResult);

        VM vm = new VM("fqn", "ip", "hostname", "domain");
        ProductInstance productInstance = new ProductInstance();
        productInstance.setName("produInst");
        productInstance.setProductRelease(productRelease);
        TierInstance tierInstance = new TierInstance(tier, "ovf", "tierInstance", vm);
        List<ProductInstance> lProductInstance = new ArrayList<ProductInstance>();
        lProductInstance.add(productInstance);
        tierInstance.setProductInstances(lProductInstance);
        List<TierInstance> tieInstances = new ArrayList<TierInstance>();
        tieInstances.add(tierInstance);

        envIns.setTierInstances(tieInstances);
        envIns.setBlueprintName("blueprint");
    }

    @Test
    public void testCreateArtifact() throws Exception {
        ProductRelease productRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        Artifact artifact = new Artifact(ARTIFACT_NAME, ARTIFACT_PATH, productRelease);


        assertEquals(artifact.getName(), ARTIFACT_NAME);
        assertEquals(artifact.getPath(), ARTIFACT_PATH);
        assertEquals(artifact.getProductRelease().getProduct(), PRODUCT_NAME);
        assertEquals(artifact.getProductRelease().getVersion(), PRODUCT_VERSION);
    }

    @Test
    public void testEqualArtifact() throws Exception {
        ProductRelease productRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        Artifact artifact = new Artifact(ARTIFACT_NAME, ARTIFACT_PATH, productRelease);
        Artifact artifact2 = new Artifact(ARTIFACT_NAME, ARTIFACT_PATH, productRelease);
        Artifact artifact3 = new Artifact(ARTIFACT_NAME + 2, ARTIFACT_PATH, productRelease);
        assertTrue(artifact.equals(artifact2));
        assertFalse(artifact.equals(artifact3));

    }

    @Test
    public void testConvertArtifactFromDto() throws Exception {
        ProductRelease productRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        ArtifactDto artifactDto = new ArtifactDto(ARTIFACT_NAME, ARTIFACT_PATH, productRelease.toDto());
        Artifact artifact = artifactDto.fromDto();


        assertEquals(artifact.getName(), ARTIFACT_NAME);
        assertEquals(artifact.getPath(), ARTIFACT_PATH);
        assertEquals(artifact.getProductRelease().getProduct(), PRODUCT_NAME);
        assertEquals(artifact.getProductRelease().getVersion(), PRODUCT_VERSION);
    }

    @Test
    public void testConvertApplicationReleaseFromDto() throws Exception {
        ProductRelease productRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        ArtifactDto artifactDto = new ArtifactDto(ARTIFACT_NAME, ARTIFACT_PATH, productRelease.toDto());
        ArrayList<ArtifactDto> artifactsDto = new ArrayList<ArtifactDto>();
        artifactsDto.add(artifactDto);

        ApplicationReleaseDto applicationReleaseDto = new ApplicationReleaseDto(APP_NAME, APP_VERSION, artifactsDto);
        ApplicationRelease appRelease = applicationReleaseDto.fromDto();
        Artifact artifact = appRelease.getArtifacts().get(0);

        assertEquals(appRelease.getName(), APP_NAME);
        assertEquals(appRelease.getVersion(), APP_VERSION);
        assertEquals(appRelease.getArtifacts().size(), 1);
        assertEquals(artifact.getName(), ARTIFACT_NAME);
        assertEquals(artifact.getPath(), ARTIFACT_PATH);
        assertEquals(artifact.getProductRelease().getProduct(), PRODUCT_NAME);
        assertEquals(artifact.getProductRelease().getVersion(), PRODUCT_VERSION);
    }

    @Test
    public void testApplicationInstance() throws Exception {
        ProductRelease productRelease = new ProductRelease(PRODUCT_NAME, PRODUCT_VERSION);
        Artifact artifactDto = new Artifact(ARTIFACT_NAME, ARTIFACT_PATH, productRelease);
        ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
        artifacts.add(artifactDto);

        ApplicationRelease applicationRelease = new ApplicationRelease(APP_NAME, APP_VERSION);
        applicationRelease.setArtifacts(artifacts);


        EnvironmentInstance environmentInstance = new EnvironmentInstance();
        environmentInstance.setBlueprintName("blueprintName");

        ApplicationInstance applicationInstance = new ApplicationInstance(applicationRelease, environmentInstance);


        assertEquals(applicationInstance.getName(), APP_NAME + "-" + "blueprintName");
        assertEquals(applicationInstance.getApplicationRelease().getVersion(), APP_VERSION);


    }

    @Test
    public void testApplicationInstanceDto() {
        ApplicationInstanceDto applicationInstance = new ApplicationInstanceDto();
        applicationInstance.setApplicationName("applicationName");
        applicationInstance.setEnvironmentInstanceName("environmentInstanceName");
        applicationInstance.setVersion("version");

        assertEquals(applicationInstance.getApplicationName(), "applicationName");
        assertEquals(applicationInstance.getVersion(), "version");
        assertEquals(applicationInstance.getEnvironmentInstanceName(), "environmentInstanceName");
    }

    @Test
    public void testApplicationReleaseII() throws Exception {
        ApplicationRelease applRele = new ApplicationRelease(APP_NAME, APP_VERSION, "description", null, null);
        applRele.setId("id");
        ApplicationRelease applRele2 = new ApplicationRelease(APP_NAME + "2", APP_VERSION, "description", null, null);
        applRele2.setId("id2");

        assertEquals(applRele.getName(), APP_NAME);
        assertEquals(applRele.getDescription(), "description");
        assertEquals(applRele.getVersion(), APP_VERSION);
        assertNull(applRele.getTransitableReleases());
        assertNull(applRele.getArtifacts());
        assertEquals(applRele.getId(), "id");
        assertEquals(applRele.equals(applRele2), false);


    }

    @Test
    public void testApplicationInstanceII() throws Exception {
        ApplicationRelease applRele = new ApplicationRelease(APP_NAME, APP_VERSION, "description", null, null);
        ApplicationInstance applInst = new ApplicationInstance(applRele, envIns, "vdc");

        assertEquals(applInst.getName(), APP_NAME + "-blueprint");
        assertEquals(applInst.getVdc(), "vdc");
        assertEquals(applInst.getApplicationRelease(), applRele);
        assertEquals(applInst.getEnvironmentInstance(), envIns);

    }

    @Test
    public void testApplicationInstanceIII() throws Exception {
        ApplicationRelease applRele = new ApplicationRelease(APP_NAME, APP_VERSION, "description", null, null);
        ApplicationInstance applInst = new ApplicationInstance();
        applInst.setApplicationRelease(applRele);
        applInst.setEnvironmentInstance(envIns);
        applInst.setName(APP_NAME + "-blueprint");

        assertEquals(applInst.getName(), APP_NAME + "-blueprint");
        assertEquals(applInst.getApplicationRelease(), applRele);
        assertEquals(applInst.getEnvironmentInstance(), envIns);

    }

    @Test
    public void testApplicationInstanceIV() throws Exception {
        ApplicationRelease applRele = new ApplicationRelease(APP_NAME, APP_VERSION, "description", null, null);
        ApplicationInstance applInst = new ApplicationInstance(applRele, envIns, "vdc", Status.INSTALLING);

        assertEquals(applInst.getName(), APP_NAME + "-blueprint");
        assertEquals(applInst.getApplicationRelease(), applRele);
        assertEquals(applInst.getEnvironmentInstance(), envIns);

    }

    @Test
    public void testArtifactAtt() throws Exception {
        Artifact artifact = new Artifact();
        artifact.addAttribute(new Attribute("key", "value"));
        assertEquals(artifact.getAttributes().get(0).getKey(), "key");

        List<Attribute> atts = new ArrayList<Attribute>();
        atts.add(new Attribute("key", "value"));
        artifact.setAttributes(atts);
        assertEquals(artifact.getAttributes().get(0).getKey(), "key");

        assertNotNull(artifact.getMapAttributes());

    }


}
