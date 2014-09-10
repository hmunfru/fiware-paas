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
import javax.persistence.OneToOne;

/**
 * @author jesus.movilla
 */
@Entity
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false, length = 256)
    private String name;

    @OneToOne
    private TierInstance tierInstance;

    /**
     * Constructor.
     */
    public Template() {
    }

    /**
     * @param name
     * @param tierInstance
     */
    public Template(String name, TierInstance tierInstance) {
        this.name = name;
        this.tierInstance = tierInstance;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the tierInstance
     */
    public TierInstance getTierInstance() {
        return tierInstance;
    }

    /**
     * @param tierInstance the tierInstance to set
     */
    public void setTierInstance(TierInstance tierInstance) {
        this.tierInstance = tierInstance;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[Template]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[tierInstance = ").append(this.tierInstance).append("]");
        sb.append("]");
        return sb.toString();
    }


}
