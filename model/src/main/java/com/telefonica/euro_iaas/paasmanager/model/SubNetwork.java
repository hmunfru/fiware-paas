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
@Table(name = "SubNetwork")
public class SubNetwork {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    @XmlTransient
    private Long id;

    // the network name //
    private String name;

    private String region;

    private String vdc;

    private String cidr;


    /**
     * Constructor.
     */
    public SubNetwork() {
    }

    /**
     * @param networkName
     */
    public SubNetwork(String name, String vdc, String region) {
        this.name = name;
        this.vdc = vdc;
        this.region = region;
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

    public String getVdc() {
        return vdc;
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
        SubNetwork other = (SubNetwork) obj;
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

    /**
     * Get the hash code associated to the id, if the id is null return the number 31.
     * @return
     */
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * @param cidr
     */
    public void setCidr(String cidr) {
        this.cidr = cidr;
    }


    /**
     * To the subnetwork Dto.
     *
     * @return
     */
    public SubNetworkDto toDto() {
        SubNetworkDto subNetworkDto = new SubNetworkDto(this.getName());
        return subNetworkDto;

    }

    /**
     * To the subnetwork Dto.
     *
     * @return
     */
    public SubNetworkInstance toInstance(String vdc, String region) {
        SubNetworkInstance subNetworkInstance = new SubNetworkInstance(this.getName(), vdc, region);
        return subNetworkInstance;

    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[SubNetwork]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[region = ").append(this.region).append("]");
        sb.append("[vdc = ").append(this.vdc).append("]");
        sb.append("[cidr = ").append(this.cidr).append("]");
        sb.append("]");
        return sb.toString();
    }


}
