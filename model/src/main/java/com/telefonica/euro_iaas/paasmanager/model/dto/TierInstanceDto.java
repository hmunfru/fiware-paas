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
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.telefonica.euro_iaas.paasmanager.model.Attribute;

/**
 * The Dto for the Tier Instance.
 *
 * @author henar
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TierInstanceDto {

    private String tierInstanceName;
    private TierDto tierDto;
    private Set<Attribute> attributes;
    private int replicaNumber;
    private String securityGroup = "";
    private List<ProductInstanceDto> productInstanceDtos;
    private VMDto vm;

    /**
     * the default constructor.
     */
    public TierInstanceDto() {
        this.productInstanceDtos = new ArrayList<ProductInstanceDto>();
    }

    /**
     * @param tierInstanceName
     * @param replicaNumber
     * @param productInstanceDtos
     * @param fqn
     */
    public TierInstanceDto(String tierInstanceName, int replicaNumber, List<ProductInstanceDto> productInstanceDtos) {

        this.tierInstanceName = tierInstanceName;
        this.productInstanceDtos = productInstanceDtos;
        this.replicaNumber = replicaNumber;
    }


    /**
     * Add the product instance dto object.
     *
     * @param productInstanceDto
     */
    public void addProductInstanceDto(ProductInstanceDto productInstanceDto) {
        if (this.productInstanceDtos == null) {
            this.productInstanceDtos = new ArrayList<ProductInstanceDto>();
        }
        this.productInstanceDtos.add(productInstanceDto);
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public List<ProductInstanceDto> getProductInstanceDtos() {
        return productInstanceDtos;
    }

    public int getReplicaNumber() {
        return replicaNumber;

    }

    public TierDto getTierDto() {
        return tierDto;
    }

    public String getTierInstanceName() {
        return tierInstanceName;
    }

    /**
     * Get the security group.
     *
     * @return
     */
    public String getSecurityGroup() {
        return this.securityGroup;
    }
    
    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void setProductInstanceDtos(List<ProductInstanceDto> productInstanceDtos) {
        this.productInstanceDtos = productInstanceDtos;
    }

    public void setReplicaNumber(int replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public void setTierDto(TierDto tierDto) {
        this.tierDto = tierDto;
    }

    public void setTierInstanceName(String tierInstanceName) {
        this.tierInstanceName = tierInstanceName;
    }

    public void setVM(VMDto vm) {
        this.vm = vm;
    }

    /**
     * @param securityGroup the securityGroup to set
     */
    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;

    }
    
    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[TierInstanceDto]");
        sb.append("[tierInstanceName = ").append(this.tierInstanceName).append("]");
        sb.append("[tierDto = ").append(this.tierDto).append("]");
        sb.append("[attributes = ").append(this.attributes).append("]");
        sb.append("[replicaNumber = ").append(this.replicaNumber).append("]");
        sb.append("[securityGroup = ").append(this.securityGroup).append("]");
        sb.append("[productInstanceDtos = ").append(this.productInstanceDtos).append("]");
        sb.append("[vm = ").append(this.vm).append("]");
        sb.append("]");
        return sb.toString();
    }


}
