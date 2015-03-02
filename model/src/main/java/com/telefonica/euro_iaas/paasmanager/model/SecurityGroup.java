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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
//import java.util.Set;


import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Security Group entity for iptables.
 *
 * @author henar
 */
@Entity
public class SecurityGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String idSecurityGroup = "";

    @Column(nullable = false, length = 256)
    private String name;

    private String description = "descripcion";

    @OneToMany
    private List<Rule> rules;
    
    /**
     * Constructor.
     */
    public SecurityGroup() {

    }

    /**
     * Constructor.
     *
     * @param name
     * @param description
     */
    public SecurityGroup(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Add rule in the security group.
     *
     * @param rule
     */
    public void addRule(Rule rule) {
        if (rules == null) {
            rules = new ArrayList<Rule>();
        }
        rules.add(rule);
    }
    
    /**
     * Delete a rule in the security group.
     *
     * @param rule
     */
    public void deleteRule(Rule rule) {

        if (rules.contains(rule)) {
            rules.remove(rule);
        }
    }

    /**
     * From json.
     *
     * @param jsonNode
     */
    @SuppressWarnings("unchecked")
    public void fromJson(JSONObject jsonNode) {
        name = jsonNode.getString("name");
        description = jsonNode.getString("description");
        // tenant_id = jsonNode.getJSONObject("tenant_id");
        idSecurityGroup = jsonNode.getString("id");

        // Rules
        JSONArray rulesjsonArray = jsonNode.getJSONArray("rules");
        List<Rule> rules = new ArrayList<Rule>();
        for (int i = 0; i < rulesjsonArray.size(); i++) {
            JSONObject object = rulesjsonArray.getJSONObject(i);
            Rule rule = new Rule();
            rule.fromJson(object);
            rules.add(rule);
        }
        setRules(rules);
    }

    public String getDescription() {
        return description;
    }

    public String getIdSecurityGroup() {
        return idSecurityGroup;
    }

    public String getName() {
        return name;
    }

    public List<Rule> getRules() {
        return rules;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdSecurityGroup(String idSecurityGroup) {
        this.idSecurityGroup = idSecurityGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public Long getId() {
        return id;
    }

    /**
     * Clone the list of rules associated to a security group and return it.
     * @return
     */
    public List<Rule> cloneRules() {
        List<Rule> rules = new ArrayList<Rule>();
        for (Rule rule : this.getRules()) {
            rules.add(rule);
        }
        return rules;
    }
    
    /**
     * Json.
     *
     * @return
     */
    public String toJSON() {
        return "{\"security_group\": \n" + "{" + "\"name\": \"" + name + "\", " + "\"description\":  \"" + description
                + "\" " + "}" + "}";
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[SecurityGroup]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[idSecurityGroup = ").append(this.idSecurityGroup).append("]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[description = ").append(this.description).append("]");
        sb.append("[rules = ").append(this.rules).append("]");
        sb.append("]");
        return sb.toString();
    }


}
