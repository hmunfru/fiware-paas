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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import net.sf.json.JSONObject;

/**
 * Rule entity for security groups.
 *
 * @author henar
 */
@Entity
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ipProtocol;
    private String fromPort;
    private String toPort;
    private String sourceGroup;
    private String cidr;

    private String idparent;
    private String idrule;

    /**
     * The default constructor.
     */
    public Rule() {
    }

    /**
     * Constructor.
     *
     * @param ipProtocol
     * @param fromPort
     * @param toPort
     * @param sourceGroup
     * @param cidr
     */
    public Rule(String ipProtocol, String fromPort, String toPort, String sourceGroup, String cidr) {
        this.ipProtocol = ipProtocol;
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.sourceGroup = sourceGroup;
        this.cidr = cidr;

    }

    /**
     * From json to entity.
     *
     * @param jsonNode
     */
    @SuppressWarnings("unchecked")
    public void fromJson(JSONObject jsonNode) {
        ipProtocol = jsonNode.getString("ip_protocol");
        fromPort = jsonNode.getString("from_port");
        toPort = jsonNode.getString("to_port");
        sourceGroup = jsonNode.getString("group");
        idparent = jsonNode.getString("parent_group_id");
        cidr = (jsonNode.getJSONObject("ip_range")).getString("cidr");
    }

    public String getCidr() {
        return cidr;
    }

    public String getFromPort() {
        return fromPort;
    }

    public String getIdParent() {
        return idparent;
    }

    public String getIdRule() {
        return idrule;
    }

    public String getIpProtocol() {
        return ipProtocol;
    }

    public String getSourceGroup() {
        return sourceGroup;
    }

    public String getToPort() {
        return toPort;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public void setFromPort(String fromPort) {
        this.fromPort = fromPort;
    }

    public void setIdParent(String idparent) {
        this.idparent = idparent;
    }

    public void setIdRule(String idrule) {
        this.idrule = idrule;
    }

    public void setIpProtocol(String ipProtocol) {
        this.ipProtocol = ipProtocol;
    }

    public void setSourceGroup(String sourceGroup) {
        this.sourceGroup = sourceGroup;
    }

    public void setToPort(String toPort) {
        this.toPort = toPort;
    }

    /**
     * to json for request Openstack.
     *
     * @return
     */
    public String toJSON() {
        String payload = "{\"security_group_rule\": \n" + "{" + "\"ip_protocol\": \"" + getIpProtocol() + "\", \n"
                + "\"from_port\": \"" + getFromPort() + "\", \n" + "\"to_port\": \"" + getToPort() + "\", \n"
                + "\"cidr\": \"" + getCidr() + "\", \n";

        if (!getSourceGroup().equals("")) {
            payload = payload + "\"group_id\": \"" + getSourceGroup() + "\", \n";
        }
        payload = payload + "\"parent_group_id\": \"" + idparent + "\" }}";
        return payload;

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
        Rule other = (Rule) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        }
        if (!this.fromPort.equals(other.fromPort)) {
            return false;
        }
        if (!this.toPort.equals(other.toPort)) {
            return false;
        }
        if (!this.cidr.equals(other.cidr)) {
            return false;
        }
        if (this.idparent != null && !this.idparent.equals(other.idparent)) {
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
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[Rule]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[ipProtocol = ").append(this.ipProtocol).append("]");
        sb.append("[fromPort = ").append(this.fromPort).append("]");
        sb.append("[toPort = ").append(this.toPort).append("]");
        sb.append("[sourceGroup = ").append(this.sourceGroup).append("]");
        sb.append("[cidr = ").append(this.cidr).append("]");
        sb.append("[idparent = ").append(this.idparent).append("]");
        sb.append("[idrule = ").append(this.idrule).append("]");
        sb.append("]");
        return sb.toString();
    }


}
