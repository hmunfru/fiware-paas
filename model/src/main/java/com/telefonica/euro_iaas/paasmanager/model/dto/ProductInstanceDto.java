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

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.telefonica.euro_iaas.paasmanager.model.Attribute;

/**
 * DTO to receive the complete information when a product release is going to be installed.
 *
 * @author Jesus M. Movilla
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductInstanceDto {

    private ProductReleaseDto productReleaseDto;

    private Set<Attribute> attributes;
    private String vdc;
    private String name;
    private String taskId;

    /**
     * Constructor.
     */
    public ProductInstanceDto() {
    }

    /**
     * Constructor with a product release dto information.
     * @param productReleaseDto
     */
    public ProductInstanceDto(ProductReleaseDto productReleaseDto) {
        this.productReleaseDto = productReleaseDto;

    }

    /**
     * @return the product
     */
    public ProductReleaseDto getProductReleaseDto() {
        return productReleaseDto;
    }

    public void setProductReleaseDto(ProductReleaseDto productReleaseDto) {
        this.productReleaseDto = productReleaseDto;
    }

    /**
     * @return the attributes
     */
    public Set<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the vdc
     */
    public String getVdc() {
        return vdc;
    }

    /**
     * @param vdc the vdc to set
     */
    public void setVdc(String vdc) {
        this.vdc = vdc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[ProductInstanceDto]");
        sb.append("[productReleaseDto = ").append(this.productReleaseDto).append("]");
        sb.append("[attributes = ").append(this.attributes).append("]");
        sb.append("[vdc = ").append(this.vdc).append("]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[taskId = ").append(this.taskId).append("]");
        sb.append("]");
        return sb.toString();
    }


}
