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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.telefonica.euro_iaas.paasmanager.model.dto.SubNetworkDto;

/**
 * A sub network.
 *
 * @author Henar Munoz
 */

@SuppressWarnings("serial")
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Table(name = "SubNetworkInstance")
public class SubNetworkInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    @XmlTransient
    private Long id;

    // the network name //
    private String name;

    private String idSubNet;

    private String idNetwork;

    private String region;

    private String vdc;

    private String cidr;

    private String allocationPoolsStart;

    private String allocationPoolsEnd;

    /**
     * Constructor.
     */
    public SubNetworkInstance() {
    }

    /**
     * @param networkName
     */
    public SubNetworkInstance(String name, String vdc, String region) {
        this.name = name;
        this.cidr = "10.100." + 1 + ".0/24";
        this.region = region;
        this.vdc = vdc;
    }

    /**
     * @param networkName
     */
    public SubNetworkInstance(String name, String vdc, String region, String id) {
        this.name = name;
        this.cidr = "10.100." + id + ".0/24";
        this.region = region;
        this.vdc = vdc;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the cidr
     */
    public String getCidr() {
        return cidr;
    }

    /**
     * @return the id
     */
    public String getIdNetwork() {
        return idNetwork;
    }

    /**
     * @return the id
     */
    public String getIdSubNet() {
        return idSubNet;
    }


    /**
     * @return the networkName
     */
    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    /**
     * @param cidr
     */
    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    /**
     * @param id
     */
    public void setIdNetwork(String id) {
        this.idNetwork = id;
    }

    /**
     * @param id
     */
    public void setIdSubNet(String id) {
        this.idSubNet = id;
    }

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
        SubNetworkInstance other = (SubNetworkInstance) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * To the subnetwork Dto.
     *
     * @return
     */
    public SubNetworkDto toDto() {
        SubNetworkDto subNetworkDto = new SubNetworkDto(this.getName());
        subNetworkDto.setCidr(this.getCidr());
        return subNetworkDto;

    }

    /**
     * the json for the OPenstack request.
     *
     * @return
     */
    public String toJson() {

        return "{\"subnet\":{"
                + "      \"name\":\"" + name + "\","
                + "      \"network_id\":\"" + this.idNetwork
                + "\","
                + "      \"ip_version\":4,    \"dns_nameservers\": [\"8.8.8.8\"],       \"cidr\":\""
                + this.cidr + "\"   }"
                + "}";
    }

    /**
     * It obtains the json for adding this subnet into a router.
     *
     * @return
     */
    public String toJsonAddInterface() {
        return "{\"subnet_id\": \"" + getIdSubNet() + "\" }";
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[SubNetworkInstance]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[idSubNet = ").append(this.idSubNet).append("]");
        sb.append("[idNetwork = ").append(this.idNetwork).append("]");
        sb.append("[region = ").append(this.region).append("]");
        sb.append("[vdc = ").append(this.vdc).append("]");
        sb.append("[cidr = ").append(this.cidr).append("]");
        sb.append("[allocationPoolsStart = ").append(this.allocationPoolsStart).append("]");
        sb.append("[allocationPoolsEnd = ").append(this.allocationPoolsEnd).append("]");
        sb.append("]");
        return sb.toString();
    }


}
