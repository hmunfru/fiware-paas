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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * VM Dto class.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VMDto {

    private String domain;
    private String fqn;
    private String hostname;
    private String ip;
    private String id;

    /**
     */
    public VMDto() {
    }

    /**
     * @param productName
     * @param version
     * @param releaseNotes
     * @param privateAttributes
     * @param supportedOS
     * @param transitableReleases
     */
    public VMDto(String domain, String fqn, String hostname, String ip, String id) {
        this.domain = domain;
        this.fqn = fqn;
        this.hostname = hostname;
        this.ip = ip;
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFqn() {
        return fqn;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[VMDto]");
        sb.append("[domain = ").append(this.domain).append("]");
        sb.append("[fqn = ").append(this.fqn).append("]");
        sb.append("[hostname = ").append(this.hostname).append("]");
        sb.append("[ip = ").append(this.ip).append("]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("]");
        return sb.toString();
    }


}
