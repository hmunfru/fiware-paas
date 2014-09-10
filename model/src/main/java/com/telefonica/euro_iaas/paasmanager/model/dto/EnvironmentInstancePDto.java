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

import com.telefonica.euro_iaas.paasmanager.model.InstallableInstance.Status;

/**
 * Class environment instance pdto.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EnvironmentInstancePDto {

    private String environmentInstanceName;
    private String vdc;
    private String description;
    private Status status;
    private String blueprintName;
    private String taskId;

    private List<TierPDto> tierDto;

    /**
     * @param environmentInstanceName
     * @param vdc
     * @param environment
     * @param tierInstances
     */
    public EnvironmentInstancePDto(String environmentInstanceName, List<TierPDto> tierPDtos, String vdc) {
        this.environmentInstanceName = environmentInstanceName;

        this.tierDto = tierPDtos;
        this.vdc = vdc;
    }

    /**
     *
     */
    public EnvironmentInstancePDto() {
    }

    /**
     * @return the environmentInstanceName
     */
    public String getEnvironmentInstanceName() {
        return environmentInstanceName;
    }

    /**
     * @param environmentInstanceName the environmentInstanceName to set
     */
    public void setEnvironmentInstanceName(String environmentInstanceName) {
        this.environmentInstanceName = environmentInstanceName;
    }

    public void setBlueprintName(String blueprintName) {
        this.blueprintName = blueprintName;
    }

    public String getBlueprintName() {
        return blueprintName;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    /**
     * @return the vdc
     */
    public String getVdc() {
        return vdc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @param vdc the vdc to set
     */
    public void setVdc(String vdc) {
        this.vdc = vdc;
    }

    /**
     * @param tierPDtos the tierInstances to set
     */
    public void setTiers(List<TierPDto> tierPDtos) {
        this.tierDto = tierPDtos;
    }

    /**
     * Add a new tier to the tierpdto.
     * @param tierPDto
     */
    public void addTiers(TierPDto tierPDto) {
        if (tierDto == null) {
            tierDto = new ArrayList();
        }
        tierDto.add(tierPDto);
    }

    public List<TierPDto> getTiers() {
        return this.tierDto;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[EnvironmentInstancePDto]");
        sb.append("[environmentInstanceName = ").append(this.environmentInstanceName).append("]");
        sb.append("[vdc = ").append(this.vdc).append("]");
        sb.append("[description = ").append(this.description).append("]");
        sb.append("[status = ").append(this.status).append("]");
        sb.append("[blueprintName = ").append(this.blueprintName).append("]");
        sb.append("[taskId = ").append(this.taskId).append("]");
        sb.append("[tierDto = ").append(this.tierDto).append("]");
        sb.append("]");
        return sb.toString();
    }


}
