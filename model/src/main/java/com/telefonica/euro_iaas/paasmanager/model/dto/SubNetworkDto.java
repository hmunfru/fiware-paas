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

import com.telefonica.euro_iaas.paasmanager.model.SubNetwork;

/**
 * Represents the data about the network to be invoke in the request.
 *
 * @author Henar Munoz
 * @version $Id: $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SubNetworkDto {

    /* The subred name */
    private String subnetName;
    private String cidr;


    /**
     * Default Constructor.
     */
    public SubNetworkDto() {
    }

    /**
     * @param networkName
     */
    public SubNetworkDto(String subnetName) {
        this.subnetName = subnetName;
    }

    /**
     * @param subnetName
     * @param cidr
     */
    public SubNetworkDto(String subnetName, String cidr) {
        this.cidr = cidr;
        this.subnetName = subnetName;
    }

    /**
     * It returns the network object associated to the dto.
     *
     * @return the network
     */
    public SubNetwork fromDto(String vdc, String region) {

        SubNetwork subNet = new SubNetwork(this.getSubNetName(), vdc, region);
        subNet.setCidr(this.getCidr());
        return subNet;
    }

    /**
     * @return the cidr
     */
    public String getCidr() {
        return cidr;
    }

    /**
     * @return the networkName
     */
    public String getSubNetName() {
        return subnetName;
    }


    /**
     * @param subnetName
     */
    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    /**
     * @param subnetName
     */
    public void setSubNetName(String subnetName) {
        this.subnetName = subnetName;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[SubNetworkDto]");
        sb.append("[subnetName = ").append(this.subnetName).append("]");
        sb.append("[cidr = ").append(this.cidr).append("]");
        sb.append("]");
        return sb.toString();
    }


}
