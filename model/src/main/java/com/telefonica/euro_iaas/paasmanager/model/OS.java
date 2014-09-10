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
import javax.persistence.Version;

import net.sf.json.JSONObject;

/**
 * Represents an available SO in the system. This entity does not provides any information about a concrete
 * installation.
 *
 * @author Jesus M. Movilla
 * @version $Id: $
 */
@Entity
public class OS {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @SuppressWarnings("unused")
    @Version
    private Long v;

    @Column(unique = true, nullable = false, length = 3)
    private String osType;
    // @Column(unique=true, nullable=false, length=256)
    @Column(length = 128)
    private String name;
    @Column(length = 2048)
    private String description;
    @Column(length = 128)
    private String version;

    /**
     * Default constructor.
     */
    public OS() {
    }

    /**
     * @param osType
     */
    public OS(String osType) {
        this.osType = osType;
    }

    /**
     * <p>
     * Constructor for SO.
     * </p>
     *
     * @param osType      a {@link java.lang.String} object.
     * @param name        a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param version     a {@link java.lang.String} object.
     */
    public OS(String osType, String name, String description, String version) {
        this.osType = osType;
        this.name = name;
        this.description = description;
        this.version = version;
    }

    /**
     * <p>
     * Getter for the field <code>name</code>.
     * </p>
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Setter for the field <code>name</code>.
     * </p>
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     * Getter for the field <code>OSType</code>.
     * </p>
     *
     * @return the OSType
     */
    public String getOsType() {
        return osType;
    }

    /**
     * <p>
     * Setter for the field <code>OsType</code>.
     * </p>
     *
     * @param osType the OsType to set
     */
    public void setOsType(String osType) {
        this.osType = osType;
    }

    /**
     * <p>
     * Getter for the field <code>description</code>.
     * </p>
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>
     * Setter for the field <code>description</code>.
     * </p>
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>
     * Getter for the field <code>version</code>.
     * </p>
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * <p>
     * Setter for the field <code>version</code>.
     * </p>
     *
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * <p>
     * Getter for the field <code>id</code>.
     * </p>
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((osType == null) ? 0 : osType.hashCode());
        return result;
    }

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

        OS other = (OS) obj;
        if (osType == null) {
            if (other.osType != null) {
                return false;
            }
        } else if (!osType.equals(other.osType)) {
            return false;
        } else if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

    /**
     * Get information (name, version and osType) from the json message.
     * @param jsonNode
     */
    @SuppressWarnings("unchecked")
    public void fromJson(JSONObject jsonNode) {
        name = jsonNode.getString("name");
        version = jsonNode.getString("version");
        osType = jsonNode.getString("osType");

        if (jsonNode.containsKey("description")) {
            description = jsonNode.getString("description");
        }

    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[OS]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[v = ").append(this.v).append("]");
        sb.append("[osType = ").append(this.osType).append("]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[description = ").append(this.description).append("]");
        sb.append("[version = ").append(this.version).append("]");
        sb.append("]");
        return sb.toString();
    }


}
