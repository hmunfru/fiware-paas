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

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Defines the object return in an asynchronous request to the system. Provides some information to know the result of
 * the task and how to find the created resource.
 *
 * @author Jesus M. Movilla
 */
@SuppressWarnings("serial")
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Task implements Serializable {
    /**
     * List of task states.
     */
    public enum TaskStates {
        QUEUED, PENDING, RUNNING, SUCCESS, ERROR, CANCELLED
    };

    @Id
    @GeneratedValue
    @XmlTransient
    private long id;

    @XmlAttribute
    @Transient
    private String href;

    @XmlElement(required = false)
    @Embedded
    private TaskError error;
    @XmlElement(required = false)
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "href", column = @Column(name = "owner_href")),
            @AttributeOverride(name = "name", column = @Column(name = "owner_name")),
            @AttributeOverride(name = "type", column = @Column(name = "owner_type")) })

    private TaskReference owner;
    @XmlElement(required = false)
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "href", column = @Column(name = "result_href")),
            @AttributeOverride(name = "name", column = @Column(name = "result_name")),
            @AttributeOverride(name = "type", column = @Column(name = "result_type")) })

    private TaskReference result;

    @XmlAttribute(required = true)
    private Date startTime;
    @XmlAttribute(required = false)
    private Date endTime;
    @XmlAttribute(required = false)
    private Long expireTime;

    @XmlAttribute(required = true)
    private TaskStates status;

    @XmlElement(required = false)
    @Column(length = 1024)
    private String description;

    @XmlElement(required = false)
    @Column(length = 1024)
    private String vdc;

    @XmlElement(required = false)
    @Column(length = 1024)
    private String environment;

    @XmlElement(required = false)
    @Column(length = 1024)
    private String tier;

    /**
     * @param href
     */
    public Task() {
        this.startTime = new Date();
    }

    /**
     * @param href
     */
    public Task(TaskStates status) {
        this.status = status;
        this.startTime = new Date();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * @return the error
     */
    public TaskError getError() {
        return error;
    }

    /**
     * @return the expireTime
     */
    public Long getExpireTime() {
        return expireTime;
    }

    /**
     * @return the href
     */
    public String getHref() {
        return href;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @return the owner
     */
    public TaskReference getOwner() {
        return owner;
    }

    /**
     * @return the result
     */
    public TaskReference getResult() {
        return result;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @return the status
     */
    public TaskStates getStatus() {
        return status;
    }

    /**
     * @return the tier
     */
    public String getTier() {
        return tier;
    }

    /**
     * @return the vdc
     */
    public String getVdc() {
        return vdc;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @param environment the environment to set
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * @param error the error to set
     */
    public void setError(TaskError error) {
        this.error = error;
    }

    /**
     * @param expireTime the expireTime to set
     */
    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(TaskReference owner) {
        this.owner = owner;
    }

    /**
     * @param result the result to set
     */
    public void setResult(TaskReference result) {
        this.result = result;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(TaskStates status) {
        this.status = status;
    }

    /**
     * @param tier the tier to set
     */
    public void setTier(String tier) {
        this.tier = tier;
    }

    /**
     * @param vdc the vdc to set
     */
    public void setVdc(String vdc) {
        this.vdc = vdc;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[Task]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[href = ").append(this.href).append("]");
        sb.append("[error = ").append(this.error).append("]");
        sb.append("[owner = ").append(this.owner).append("]");
        sb.append("[result = ").append(this.result).append("]");
        sb.append("[startTime = ").append(this.startTime).append("]");
        sb.append("[endTime = ").append(this.endTime).append("]");
        sb.append("[expireTime = ").append(this.expireTime).append("]");
        sb.append("[status = ").append(this.status).append("]");
        sb.append("[description = ").append(this.description).append("]");
        sb.append("[vdc = ").append(this.vdc).append("]");
        sb.append("[environment = ").append(this.environment).append("]");
        sb.append("[tier = ").append(this.tier).append("]");
        sb.append("]");
        return sb.toString();
    }


}
