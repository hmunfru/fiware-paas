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

package com.telefonica.euro_iaas.paasmanager.model.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Tier;

/**
 * Represents an artifact to be installed on a ProductRelease.
 *
 * @author Henar Mu�oz
 * @version $Id: $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TierDto {

    private String name;
    private String flavour;
    private String image;

    private Integer maximumNumberInstances;
    private Integer minimumNumberInstances;
    private Integer initialNumberInstances;

    private List<ProductReleaseDto> productReleaseDtos;

    private List<NetworkDto> networkDto;

    private String icono = "";
    private String securityGroup = "";
    private String keypair = "";
    private String floatingip = "";
    private String affinity = "None";

    private String region = "";

    /**
     * Default Constructor.
     */
    public TierDto() {
        this.networkDto = new ArrayList<NetworkDto>();
        this.productReleaseDtos = new ArrayList<ProductReleaseDto>();
    }

    /**
     * Constructor.
     *
     * @param name
     * @param maximumNumberInstances
     * @param minimumNumberInstances
     * @param initialNumberInstances
     * @param productReleaseDtos
     */

    public TierDto(String name, Integer maximumNumberInstances, Integer minimumNumberInstances,
                   Integer initialNumberInstances, List<ProductReleaseDto> productReleaseDtos) {

        this.name = name;
        this.maximumNumberInstances = maximumNumberInstances;
        this.minimumNumberInstances = minimumNumberInstances;
        this.initialNumberInstances = initialNumberInstances;
        this.productReleaseDtos = productReleaseDtos;

        this.networkDto = new ArrayList<NetworkDto>();
    }

    /**
     * Constructor with complete information.
     * @param name
     * @param maximumNumberInstances
     * @param minimumNumberInstances
     * @param initialNumberInstances
     * @param productReleaseDtos
     * @param flavour
     * @param image
     * @param icono
     * @param keypair
     * @param floatingip
     */
    public TierDto(String name, Integer maximumNumberInstances, Integer minimumNumberInstances,
                   Integer initialNumberInstances, List<ProductReleaseDto> productReleaseDtos,
                   String flavour, String image, String icono, String keypair, String floatingip) {

        this.name = name;
        this.maximumNumberInstances = maximumNumberInstances;
        this.minimumNumberInstances = minimumNumberInstances;
        this.initialNumberInstances = initialNumberInstances;
        this.productReleaseDtos = productReleaseDtos;
        this.flavour = flavour;
        this.image = image;
        this.icono = icono;
        this.keypair = keypair;
        this.floatingip = floatingip;
        this.affinity = "None";
        this.networkDto = new ArrayList<NetworkDto>();
    }

    /**
     * Constructor with complete information plus affinity one.
     * @param name
     * @param maximumNumberInstances
     * @param minimumNumberInstances
     * @param initialNumberInstances
     * @param productReleaseDtos
     * @param flavour
     * @param image
     * @param icono
     * @param keypair
     * @param floatingip
     * @param affinity
     */
    public TierDto(String name, Integer maximumNumberInstances, Integer minimumNumberInstances,
                   Integer initialNumberInstances, List<ProductReleaseDto> productReleaseDtos,
                   String flavour, String image, String icono, String keypair, String floatingip, String affinity) {

        this.name = name;
        this.maximumNumberInstances = maximumNumberInstances;
        this.minimumNumberInstances = minimumNumberInstances;
        this.initialNumberInstances = initialNumberInstances;
        this.productReleaseDtos = productReleaseDtos;
        this.flavour = flavour;
        this.image = image;
        this.icono = icono;
        this.keypair = keypair;
        this.floatingip = floatingip;
        this.affinity = affinity;
        this.networkDto = new ArrayList<NetworkDto>();
    }

    /**
     * Add network dto.
     *
     * @param networkDto the network do to be add to the array.
     */
    public void addNetworkDto(NetworkDto networkDto) {
        if (networkDto == null) {
            this.networkDto = new ArrayList<NetworkDto>();
        }
        this.networkDto.add(networkDto);
    }

    /**
     * Add product release.
     *
     * @param productReleaseDto the product release to be added.
     */
    public void addProductRelease(ProductReleaseDto productReleaseDto) {
        if (this.productReleaseDtos == null) {
            productReleaseDtos = new ArrayList<ProductReleaseDto>();
        }
        productReleaseDtos.add(productReleaseDto);
    }

    /**
     * It obtains the Tier object associated.
     *
     * @tier
     */
    public Tier fromDto(String vdc, String envName) {

        Tier tier = new Tier();
        tier.setName(getName());
        tier.setInitialNumberInstances(getInitialNumberInstances());
        tier.setMaximumNumberInstances(getMaximumNumberInstances());
        tier.setMinimumNumberInstances(getMinimumNumberInstances());
        tier.setIcono(getIcono());
        tier.setFlavour(getFlavour());
        tier.setImage(getImage());
        tier.setKeypair(getKeypair());
        tier.setFloatingip(getFloatingip());
        tier.setRegion(getRegion());
        tier.setVdc(vdc);
        tier.setEnviromentName(envName);
        tier.setAffinity(this.getAffinity());


        for (ProductReleaseDto pReleaseDto : getProductReleaseDtos()) {
            ProductRelease pRelease = new ProductRelease();
            pRelease.setProduct(pReleaseDto.getProductName());
            pRelease.setVersion(pReleaseDto.getVersion());

            if (pReleaseDto.getProductDescription() != null) {
                pRelease.setDescription(pReleaseDto.getProductDescription());
            }

            if (pReleaseDto.getPrivateAttributes() != null) {
                Set<Attribute> attrs = new HashSet<Attribute>();
                for (Attribute attr : pReleaseDto.getPrivateAttributes()) {
                    attrs.add(attr);
                }
                pRelease.setAttributes(attrs);
            }
            tier.addProductRelease(pRelease);
        }

        for (NetworkDto networkDto : this.getNetworksDto()) {
            Network network;
            if (vdc == null) {
                network = networkDto.fromDto("", this.getRegion());
            } else {
                network = networkDto.fromDto(vdc, this.getRegion());
            }
            tier.addNetwork(network);
        }
        return tier;
    }

    public String getFlavour() {
        return flavour;
    }

    public String getFloatingip() {
        return this.floatingip;
    }

    public String getIcono() {
        return this.icono;
    }

    public String getImage() {
        return this.image;
    }

    /**
     * @return the initialNumberInstances
     */
    public Integer getInitialNumberInstances() {
        return initialNumberInstances;
    }

    public String getKeypair() {
        return this.keypair;
    }

    /**
     * @return the maximum_number_instances
     */
    public Integer getMaximumNumberInstances() {
        return maximumNumberInstances;
    }

    /**
     * @return the minimumNumberInstances
     */
    public Integer getMinimumNumberInstances() {
        return minimumNumberInstances;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the list of networks associated
     */
    public List<NetworkDto> getNetworksDto() {
        return networkDto;
    }

    /**
     * @return the productReleases
     */
    public List<ProductReleaseDto> getProductReleaseDtos() {
        if (productReleaseDtos == null) {
            this.productReleaseDtos = new ArrayList<ProductReleaseDto>();
        }
        return productReleaseDtos;
    }

    /**
     * Get the security group.
     *
     * @return
     */
    public String getSecurityGroup() {
        return this.securityGroup;
    }

    /**
     * It removes the product release.
     *
     * @param productReleaseDto
     */
    public void removeProductRelease(ProductReleaseDto productReleaseDto) {

        productReleaseDtos.remove(productReleaseDto);
    }

    /**
     * @param flavour the flavour to set
     */
    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }

    public void setFloatingip(String floatingip) {
        this.floatingip = floatingip;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @param initialNumberInstances the initialNumberInstances to set
     */
    public void setInitialNumberInstances(Integer initialNumberInstances) {
        this.initialNumberInstances = initialNumberInstances;
    }

    public void setKeypair(String keypair) {
        this.keypair = keypair;
    }

    /**
     * @param maximumNumberInstances the maximumNumberInstances to set
     */
    public void setMaximumNumberInstances(Integer maximumNumberInstances) {
        this.maximumNumberInstances = maximumNumberInstances;
    }

    /**
     * @param minimumNumberInstances the minimumNumberInstances to set
     */
    public void setMinimumNumberInstances(Integer minimumNumberInstances) {
        this.minimumNumberInstances = minimumNumberInstances;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param networkDto the network do to be add to the array
     */
    public void setNetworksDto(List<NetworkDto> networkDto) {
        this.networkDto = networkDto;
    }

    /**
     * @param productReleaseDtos the productReleases to set
     */
    public void setProductReleaseDtos(List<ProductReleaseDto> productReleaseDtos) {
        this.productReleaseDtos = productReleaseDtos;
    }

    /**
     * @param securityGroup the securityGroup to set
     */
    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;

    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAffinity() {
        return affinity;
    }

    public void setAffinity(String affinity) {
        this.affinity = affinity;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[TierDto]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[flavour = ").append(this.flavour).append("]");
        sb.append("[image = ").append(this.image).append("]");
        sb.append("[maximumNumberInstances = ").append(this.maximumNumberInstances).append("]");
        sb.append("[minimumNumberInstances = ").append(this.minimumNumberInstances).append("]");
        sb.append("[initialNumberInstances = ").append(this.initialNumberInstances).append("]");
        sb.append("[productReleaseDtos = ").append(this.productReleaseDtos).append("]");
        sb.append("[networkDto = ").append(this.networkDto).append("]");
        sb.append("[icono = ").append(this.icono).append("]");
        sb.append("[securityGroup = ").append(this.securityGroup).append("]");
        sb.append("[keypair = ").append(this.keypair).append("]");
        sb.append("[floatingip = ").append(this.floatingip).append("]");
        sb.append("[affinity = ").append(this.affinity).append("]");
        sb.append("[region = ").append(this.region).append("]");
        sb.append("]");
        return sb.toString();
    }


}
