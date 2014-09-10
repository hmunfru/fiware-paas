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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;

/**
 * The dto for the Tier Instance.
 *
 * @author henar
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TierInstancePDto {

    private String tierInstanceName;
    private Status status;
    private String taskId;

    private List<ProductInstanceDto> productInstanceDtos;
    private VMDto vm;

    /**
     * Constructor.
     */
    public TierInstancePDto() {
    }

    /**
     * Constructor.
     *
     * @param tierInstanceName
     * @param productInstanceDtos
     * @param vm
     * @param status
     * @param taskId
     */
    public TierInstancePDto(String tierInstanceName, List<ProductInstanceDto> productInstanceDtos, VMDto vm,
                            Status status, String taskId) {

        this.tierInstanceName = tierInstanceName;
        this.productInstanceDtos = productInstanceDtos;
        this.vm = vm;
        this.status = status;
        this.taskId = taskId;
    }

    public List<ProductInstanceDto> getProductInstanceDtos() {
        return productInstanceDtos;
    }

    public Status getStatus() {
        return this.status;
    }

    public String getTierInstanceName() {
        return tierInstanceName;
    }

    public VMDto getVM() {
        return this.vm;
    }

    public void setProductInstanceDtos(List<ProductInstanceDto> productInstanceDtos) {
        this.productInstanceDtos = productInstanceDtos;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTierInstanceName(String tierInstanceName) {
        this.tierInstanceName = tierInstanceName;
    }

    public void setVM(VMDto vm) {
        this.vm = vm;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[TierInstancePDto]");
        sb.append("[tierInstanceName = ").append(this.tierInstanceName).append("]");
        sb.append("[status = ").append(this.status).append("]");
        sb.append("[taskId = ").append(this.taskId).append("]");
        sb.append("[productInstanceDtos = ").append(this.productInstanceDtos).append("]");
        sb.append("[vm = ").append(this.vm).append("]");
        sb.append("]");
        return sb.toString();
    }


}
