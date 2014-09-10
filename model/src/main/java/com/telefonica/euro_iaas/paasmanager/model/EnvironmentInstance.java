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
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentInstancePDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.ProductReleaseDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierInstancePDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierPDto;

// import org.hibernate.annotations.Cascade;

/**
 * Represents an instance of a environment.
 *
 * @author Jesus M. Movilla
 * @version $Id: $
 */
@SuppressWarnings("restriction")
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "EnvironmentInstance")
public class EnvironmentInstance extends InstallableInstance {

    public static final String VDC_FIELD = "vdc";
    public static final String ENVIRONMENTNAME_FIELD = "blueprintname";

    @ManyToOne()
    private Environment environment;
    @Column(length = 256)
    private String description;
    @Column(length = 256)
    private String blueprintName = "";
    private String taskId = "";


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "environmentInstance_has_tierInstances",
            joinColumns = {@JoinColumn(name = "environmentinstance_ID", nullable = false, updatable = false) },
            inverseJoinColumns = {@JoinColumn(name = "tierinstance_ID", nullable = false, updatable = false) })
    private List<TierInstance> tierInstances;

    @Column(length = 90000)
    private String vapp;

    /**
     * Constructor.
     */
    public EnvironmentInstance() {

    }

    /**
     * @param environment
     * @param tierInstances
     */
    public EnvironmentInstance(Environment environment, List<TierInstance> tierInstances) {
        super();
        this.environment = environment;
        this.tierInstances = tierInstances;

    }

    /**
     * @param blueprintName
     * @param description
     */
    public EnvironmentInstance(String blueprintName, String description) {
        this.blueprintName = blueprintName;
        this.description = description;
    }

    /**
     * @param blueprintName
     * @param description
     * @param env
     */
    public EnvironmentInstance(String blueprintName, String description, Environment env) {
        this.blueprintName = blueprintName;
        this.description = description;
        this.environment = env;
    }

    /**
     * @param tierInstance
     */
    public void addTierInstance(TierInstance tierInstance) {
        if (tierInstances == null) {
            tierInstances = new ArrayList<TierInstance>();
        }
        tierInstances.add(tierInstance);

    }

    private TierInstancePDto createInstancePDto(TierInstance tierInstance) {
        List<ProductInstanceDto> productInstanceDtos = new ArrayList<ProductInstanceDto>();
        if (tierInstance.getProductInstances() != null) {
            for (ProductInstance productInstance : tierInstance.getProductInstances()) {
                productInstanceDtos.add(productInstance.toDto());
            }
        }
        TierInstancePDto tierInstancePDto = null;
        if (tierInstance.getVM() == null) {
            tierInstancePDto = new TierInstancePDto(tierInstance.getName(), productInstanceDtos, null,
                    tierInstance.getStatus(), tierInstance.getTaskId());
        } else {
            tierInstancePDto = new TierInstancePDto(tierInstance.getName(), productInstanceDtos, tierInstance.getVM()
                    .toDto(), tierInstance.getStatus(), tierInstance.getTaskId());
        }
        return tierInstancePDto;
    }

    private TierPDto createTierPDto(Tier tier) {

        return new TierPDto(tier.getName());
    }

    private TierPDto createTierPDto(Tier tier, List<TierInstance> lTierInstance) {
        List<ProductReleaseDto> productReleasedto = new ArrayList<ProductReleaseDto>();

        for (ProductRelease productRelease : tier.getProductReleases()) {
            if (!productReleasedto.contains(productRelease.toDto())) {
                productReleasedto.add(productRelease.toDto());
            }
        }
        String securitygroup = "";
        if (tier.getSecurityGroup() != null) {
            securitygroup = tier.getSecurityGroup().getName();
        }
        TierPDto tierPDto = new TierPDto(tier.getName(), tier.getMaximumNumberInstances(),
                tier.getMinimumNumberInstances(), tier.getInitialNumberInstances(), productReleasedto,
                tier.getFlavour(), tier.getImage(), tier.getIcono(), securitygroup, tier.getKeypair(),
                tier.getFloatingip(), tier.getRegion());

        if (lTierInstance != null && lTierInstance.size() != 0) {
            for (TierInstance tierInstance : lTierInstance) {

                if (tierInstance.getTier().getName().equals(tier.getName())) {
                    tierPDto.addTierInstance(createInstancePDto(tierInstance));
                }
            }
        }
        return tierPDto;
    }

    public String getBlueprintName() {
        return this.blueprintName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * @return the environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    public String getTaskId() {
        return taskId;
    }

    /**
     * @return the tierInstances
     */
    public List<TierInstance> getTierInstances() {
        return tierInstances;
    }

    /**
     * @return the vapp
     */
    public String getVapp() {
        return vapp;
    }

    /**
     * It removes a tier instance in the environment.
     *
     * @param tierInstance
     */
    public void removeTierInstance(TierInstance tierInstance) {
        if (tierInstances.contains(tierInstance)) {
            tierInstances.remove(tierInstance);
        }

    }

    public void setBlueprintName(String blueprintName) {
        this.blueprintName = blueprintName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param environment the environment to set
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /*
     * setting the Name as function of environment and Tiers
     */

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @param tierInstances the tierInstances to set
     */
    public void setTierInstances(List<TierInstance> tierInstances) {
        this.tierInstances = tierInstances;
    }

    /**
     * @param vapp the vapp to set
     */
    public void setVapp(String vapp) {
        this.vapp = vapp;
    }

    /**
     * the dto specification.
     *
     * @return the dto
     */
    public EnvironmentInstanceDto toDto() {
        EnvironmentInstanceDto envInstanceDto = new EnvironmentInstanceDto();
        envInstanceDto.setEnvironmentInstanceName(getName());
        envInstanceDto.setDescription(this.getDescription());
        envInstanceDto.setBlueprintName(this.getBlueprintName());

        if (this.getStatus() != null) {
            envInstanceDto.setStatus(this.getStatus());
        }

        List<TierInstanceDto> lTierInstanceDto = new ArrayList<TierInstanceDto>();
        if (getTierInstances() != null) {
            for (TierInstance tierInstance : getTierInstances()) {
                lTierInstanceDto.add(tierInstance.toDto());
            }
            envInstanceDto.setTierInstances(lTierInstanceDto);
        }

        if (getVdc() != null) {
            envInstanceDto.setVdc(getVdc());
        }

        if (getPrivateAttributes() != null) {
            envInstanceDto.setAttributes(getPrivateAttributes());
        }

        return envInstanceDto;
    }

    /**
     * The dto entity for the portal.
     *
     * @return
     */
    public EnvironmentInstancePDto toPDto() {
        EnvironmentInstancePDto envInstanceDto = new EnvironmentInstancePDto();
        envInstanceDto.setEnvironmentInstanceName(getName());
        envInstanceDto.setDescription(this.getDescription());
        envInstanceDto.setBlueprintName(this.getBlueprintName());
        envInstanceDto.setTaskId(this.getTaskId());

        if (this.getStatus() != null) {
            envInstanceDto.setStatus(this.getStatus());
        }

        if (this.getEnvironment() != null) {
            if (this.getEnvironment().getTiers() != null) {
                for (Tier tier : getEnvironment().getTiers()) {
                    TierPDto tierPDto = createTierPDto(tier, this.getTierInstances());
                    envInstanceDto.addTiers(tierPDto);
                }
            }
        }

        List<TierInstanceDto> lTierInstanceDto = new ArrayList<TierInstanceDto>();
        if (getTierInstances() != null) {
            for (TierInstance tierInstance : getTierInstances()) {
                lTierInstanceDto.add(tierInstance.toDto());
            }

        }

        if (getVdc() != null) {
            envInstanceDto.setVdc(getVdc());
        }

        return envInstanceDto;
    }

    /**
     * The environment instance dto for the portal.
     *
     * @return
     */
    public EnvironmentInstancePDto toPDtos() {
        EnvironmentInstancePDto envInstanceDto = new EnvironmentInstancePDto();
        envInstanceDto.setEnvironmentInstanceName(getName());
        envInstanceDto.setDescription(this.getDescription());
        envInstanceDto.setBlueprintName(this.getBlueprintName());
        envInstanceDto.setTaskId(this.getTaskId());

        if (this.getStatus() != null) {
            envInstanceDto.setStatus(this.getStatus());
        }

        if (this.getEnvironment() != null) {
            if (this.getEnvironment().getTiers() != null) {
                for (Tier tier : getEnvironment().getTiers()) {
                    TierPDto tierPDto = createTierPDto(tier);
                    envInstanceDto.addTiers(tierPDto);
                }
            }
        }
        if (getVdc() != null) {
            envInstanceDto.setVdc(getVdc());
        }

        return envInstanceDto;
    }

    /**
     * Get the network instances from a network of a tier instance.
     * @param networkName
     * @param region
     * @return
     */
    public NetworkInstance getNetworkInstanceFromNetwork(String networkName, String region) {

        for (TierInstance tierInstance : this.getTierInstances()) {
            if (!tierInstance.getTier().getRegion().equals(region)) {
                continue;
            }
            for (NetworkInstance net2 : tierInstance.getNetworkInstances()) {
                net2.getNetworkName().equals(networkName);
                return net2;
            }

        }
        return null;

    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[EnvironmentInstance]");
        sb.append("[environment = ").append(this.environment).append("]");
        sb.append("[description = ").append(this.description).append("]");
        sb.append("[blueprintName = ").append(this.blueprintName).append("]");
        sb.append("[taskId = ").append(this.taskId).append("]");
        sb.append("[tierInstances = ").append(this.tierInstances).append("]");
        sb.append("[vapp = ").append(this.vapp).append("]");
        sb.append("]");
        return sb.toString();
    }


}
