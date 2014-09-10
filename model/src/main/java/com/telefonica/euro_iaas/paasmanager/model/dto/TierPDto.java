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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents an artifact to be installed on a ProductRelease.
 *
 * @author Henar Mu�oz
 * @version $Id: $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TierPDto {

    private String name;
    private String flavour;
    private String image;

    private Integer maximumNumberInstances;
    private Integer minimumNumberInstances;
    private Integer initialNumberInstances;

    private List<ProductReleaseDto> productReleaseDtos;

    private String icono;
    private String securityGroup;
    private String keypair;
    private String floatingip;
    private String region;

    private List<TierInstancePDto> tierInstancePDto;

    /**
     * Default Constructor.
     */
    public TierPDto() {
    }

    /**
     * Constructor.
     * @param name
     */
    public TierPDto(String name) {
        this.name = name;
    }

    /**
     * Constructor with complete information about the product.
     * @param name
     * @param maximumNumberInstances
     * @param minimumNumberInstances
     * @param initialNumberInstances
     * @param productReleaseDtos
     * @param flavour
     * @param image
     * @param icono
     * @param securityGroup
     * @param keypair
     * @param floatingip
     * @param region
     */
    public TierPDto(String name, Integer maximumNumberInstances, Integer minimumNumberInstances,
                    Integer initialNumberInstances, List<ProductReleaseDto> productReleaseDtos,
                    String flavour, String image, String icono, String securityGroup, String keypair,
                    String floatingip, String region) {
        this.name = name;
        this.maximumNumberInstances = maximumNumberInstances;
        this.minimumNumberInstances = minimumNumberInstances;
        this.initialNumberInstances = initialNumberInstances;
        this.productReleaseDtos = productReleaseDtos;
        this.flavour = flavour;
        this.image = image;
        this.icono = icono;
        this.securityGroup = securityGroup;
        this.keypair = keypair;
        this.floatingip = floatingip;
        this.region = region;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
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
     * @return the initialNumberInstances
     */
    public Integer getInitialNumberInstances() {
        return initialNumberInstances;
    }

    /**
     * @return the productReleases
     */
    public List<ProductReleaseDto> getProductReleaseDtos() {
        return productReleaseDtos;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @param initialNumberInstances the initialNumberInstances to set
     */
    public void setInitialNumberInstances(Integer initialNumberInstances) {
        this.initialNumberInstances = initialNumberInstances;
    }

    /**
     * @param flavour the name to set
     */
    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }

    public String getFlavour() {
        return flavour;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    /**
     * @param productReleaseDtos the productReleases to set
     */
    public void setProductReleaseDtos(List<ProductReleaseDto> productReleaseDtos) {
        this.productReleaseDtos = productReleaseDtos;
    }

    /**
     * Add a new product release.
     * @param productReleaseDto
     */
    public void addProductRelease(ProductReleaseDto productReleaseDto) {
        if (this.productReleaseDtos == null) {
            productReleaseDtos = new ArrayList();
        }

        productReleaseDtos.add(productReleaseDto);
    }

    /**
     * Delete a Product Release.
     * @param productReleaseDto
     */
    public void removeProductRelease(ProductReleaseDto productReleaseDto) {

        productReleaseDtos.remove(productReleaseDto);
    }

    /**
     * Set a new set of Tier instances.
     * @param tierInstancePDto
     */
    public void setTierInstances(List<TierInstancePDto> tierInstancePDto) {
        this.tierInstancePDto = tierInstancePDto;
    }

    public List<TierInstancePDto> getTierInstances() {
        return this.tierInstancePDto;
    }

    /**
     * Add a new Tier instance.
     * @param tierInstacesDto
     */
    public void addTierInstance(TierInstancePDto tierInstacesDto) {
        if (this.tierInstancePDto == null) {
            tierInstancePDto = new ArrayList();
        }

        tierInstancePDto.add(tierInstacesDto);
    }

    /**
     * Remove a Tier instance.
     * @param tierInstacesDto
     */
    public void removeTierInstances(TierInstancePDto tierInstacesDto) {

        tierInstancePDto.remove(tierInstacesDto);
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getIcono() {
        return this.icono;
    }

    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;
    }

    public String getSecurityGroup() {
        return this.securityGroup;
    }

    public void setKeypair(String keypair) {
        this.keypair = keypair;
    }

    public String getKeypair() {
        return this.keypair;
    }

    public void setFloatingip(String floatingip) {
        this.floatingip = floatingip;
    }

    public String getFloatingip() {
        return this.floatingip;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[TierPDto]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[flavour = ").append(this.flavour).append("]");
        sb.append("[image = ").append(this.image).append("]");
        sb.append("[maximumNumberInstances = ").append(this.maximumNumberInstances).append("]");
        sb.append("[minimumNumberInstances = ").append(this.minimumNumberInstances).append("]");
        sb.append("[initialNumberInstances = ").append(this.initialNumberInstances).append("]");
        sb.append("[productReleaseDtos = ").append(this.productReleaseDtos).append("]");
        sb.append("[icono = ").append(this.icono).append("]");
        sb.append("[security_group = ").append(this.securityGroup).append("]");
        sb.append("[keypair = ").append(this.keypair).append("]");
        sb.append("[floatingip = ").append(this.floatingip).append("]");
        sb.append("[region = ").append(this.region).append("]");
        sb.append("[tierInstancePDto = ").append(this.tierInstancePDto).append("]");
        sb.append("]");
        return sb.toString();
    }


}
