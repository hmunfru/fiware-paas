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

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Represents a host where a OS is running. It is formed by
 *
 * @author jesus M. Movilla
 */

@Embeddable
public class VM {

    /**
     * The ip where the host is located.
     */
    @Column(length = 128)
    private String ip = "";

    @Column(length = 128)
    private String floatingip = "";

    /**
     * The computer's hostname.
     */
    @Column(length = 128)
    private String hostname = "";

    /**
     * The domain.
     */
    @Column(length = 128)
    private String domain = "";

    /**
     * the fqn. **
     */
    @Column(length = 512)
    private String fqn = "";

    /**
     * the OSType. **
     */
    @Column(length = 8)
    private String osType = "";

    /**
     * the VAPP. **
     */
    @Column(length = 512)
    private String vmid = "";

    /**
     * Constructor.
     */
    public VM() {

    }

    /**
     * @param fqn
     */
    public VM(String fqn) {
        this.fqn = fqn;
    }

    /**
     * @param fqn
     * @param ip
     */
    public VM(String fqn, String ip) {
        this.fqn = fqn;
        this.ip = ip;
    }

    /**
     * @param fqn
     * @param hostname
     * @param domain
     */
    public VM(String fqn, String hostname, String domain) {

        this.fqn = fqn;
        this.hostname = hostname;
        this.domain = domain;
    }

    /**
     * @param ip
     * @param fqn
     * @param hostname
     * @param domain
     */
    public VM(String fqn, String ip, String hostname, String domain) {
        this.fqn = fqn;
        this.hostname = hostname;
        this.domain = domain;
        this.ip = ip;
    }

    /**
     * @param ip
     * @param hostname
     * @param domain
     * @param fqn
     * @param osType
     */
    public VM(String ip, String hostname, String domain, String fqn, String osType) {
        this.fqn = fqn;
        this.hostname = hostname;
        this.domain = domain;
        this.ip = ip;
        this.osType = osType;
    }

    // // ACCESSORS ////

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VM other = (VM) obj;
        if (domain == null) {
            if (other.domain != null) {
                return false;
            }
        } else if (!domain.equals(other.domain)) {
            return false;
        }
        if (fqn == null) {
            if (other.fqn != null) {
                return false;
            }
        } else if (!fqn.equals(other.fqn)) {

            return false;
        }
        if (hostname == null) {
            if (other.hostname != null) {
                return false;
            }
        } else if (!hostname.equals(other.hostname)) {
            return false;
        }
        if (ip == null) {
            if (other.ip != null) {
                return false;
            }

        } else if (!ip.equals(other.ip)) {
            return false;
        }
        return true;
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @return the fqn
     */
    public String getFqn() {
        return fqn;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @return the ip
     */
    public String getFloatingIp() {
        return this.floatingip;
    }

    /**
     * @return the osType
     */
    public String getOsType() {
        return osType;
    }

    /**
     * @return the vmid
     */
    public String getVmid() {
        return vmid;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((fqn == null) ? 0 : fqn.hashCode());
        result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        return result;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @param fqn the fqn to set
     */
    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @param floatingip the ip to set
     */
    public void setFloatingIp(String floatingip) {
        this.floatingip = floatingip;
    }

    /**
     * @param osType the osType to set
     */
    public void setOsType(String osType) {
        this.osType = osType;
    }

    /**
     * @param vmid the vmid to set
     */
    public void setVmid(String vmid) {
        this.vmid = vmid;
    }

    /**
     * a Dto entity.
     *
     * @return
     */
    public VMDto toDto() {

        return new VMDto(domain, fqn, hostname, ip, this.vmid);

    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[VM]");
        sb.append("[ip = ").append(this.ip).append("]");
        sb.append("[floatingip = ").append(this.floatingip).append("]");
        sb.append("[hostname = ").append(this.hostname).append("]");
        sb.append("[domain = ").append(this.domain).append("]");
        sb.append("[fqn = ").append(this.fqn).append("]");
        sb.append("[osType = ").append(this.osType).append("]");
        sb.append("[vmid = ").append(this.vmid).append("]");
        sb.append("]");
        return sb.toString();
    }


}
