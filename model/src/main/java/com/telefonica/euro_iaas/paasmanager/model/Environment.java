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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;


import com.telefonica.euro_iaas.paasmanager.model.dto.EnvironmentDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierDto;

/**
 * Environment entity.
 *
 * @author henar
 */
@Entity
public class Environment {

    public static final String ORG_FIELD = "org";
    public static final String VDC_FIELD = "vdc";
    public static final String ENVIRONMENT_NAME_FIELD = "name";
    public static final int COLUMN_LENGHT = 256;
    public static final int LONG_COLUMN_LENGHT = 90000;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = COLUMN_LENGHT)
    private String name;

    @Column(nullable = false, length = COLUMN_LENGHT)
    private String org;

    @Column(length = COLUMN_LENGHT)
    private String description;

    @Column(length = COLUMN_LENGHT)
    private String vdc;

    @Column(length = LONG_COLUMN_LENGHT)
    private String ovf;

    @ManyToMany
    @JoinTable(name = "environment_has_tiers",
            joinColumns = {@JoinColumn(name = "environment_ID", nullable = false, updatable = false) },
            inverseJoinColumns = {@JoinColumn(name = "tier_ID", nullable = false, updatable = false) })

    private Set<Tier> tiers = new HashSet<Tier>();

    /**
     * Default constructor.
     */
    public Environment() {
    }

    /**
     * Constructor.
     *
     * @param name
     * @param tiers
     */
    public Environment(String name, Set<Tier> tiers) {
        this.name = name;
        this.tiers = tiers;
    }

    /**
     * Constructor.
     *
     * @param name
     * @param tiers
     * @param description
     * @param org
     * @param vdc
     */
    public Environment(String name, Set<Tier> tiers, String description, String org, String vdc) {
        this.name = name;
        this.description = description;
        this.org = org;
        this.tiers = tiers;
        this.vdc = vdc;
    }

    /**
     * <p>
     * Constructor for Service.
     * </p>
     *
     * @param name        a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param tiers
     */
    public Environment(String name, String description, Set<Tier> tiers) {

        this.name = name;
        this.description = description;
        this.tiers = tiers;

    }

    /**
     * Add a tier to the environment.
     *
     * @param tier
     */
    public void addTier(Tier tier) {
        if (this.tiers == null) {
            tiers = new HashSet<Tier>();
        }
        tiers.add(tier);
    }

    /**
     * Delete a tier in the environment.
     *
     * @param tier
     */
    public void deleteTier(Tier tier) {
        if (tiers.contains(tier)) {
            tiers.remove(tier);
        }
    }

    /**
     * Update tier.
     *
     * @param tierOld
     * @param tierNew
     */
    public void updateTier(Tier tierOld, Tier tierNew) {
        deleteTier(tierOld);
        addTier(tierNew);
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
        Environment other = (Environment) obj;
        if (this.getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!this.getName().equals(other.getName())) {
            return false;
        }
        return true;
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrg() {
        return this.org;
    }

    public String getOvf() {
        return ovf;
    }

    public Set<Tier> getTiers() {
        return tiers;
    }

    public String getVdc() {
        return this.vdc;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        return result;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public void setOvf(String ovf) {
        this.ovf = ovf;
    }

    public void setTiers(Set<Tier> tiers) {
        this.tiers = tiers;
    }

    public void setVdc(String vdc) {
        this.vdc = vdc;
    }

    /**
     * It returns the dto specification.
     *
     * @return EnvironmentDto.class
     */
    public EnvironmentDto toDto() {
        EnvironmentDto envDto = new EnvironmentDto();
        envDto.setName(getName());
        envDto.setDescription(getDescription());

        Set<TierDto> lTierDto = new HashSet<TierDto>();
        if (getTiers() != null) {
            for (Tier tier : getTiers()) {
                lTierDto.add(tier.toDto());
            }
            envDto.setTierDtos(lTierDto);
        }

        return envDto;
    }

    /**
     * Get the list of available networks per region.
     * @return
     */
    public Map<String, Set<String>> getNetworksRegion() {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        Set<String> regions;
        for (Tier tier : this.getTiers()) {
            for (Network net : tier.getNetworks()) {
                if (map.get(net.getNetworkName()) != null) {
                    map.get(net.getNetworkName()).add(tier.getRegion());
                } else {
                    regions = new HashSet<String>();
                    regions.add(tier.getRegion());
                    map.put(net.getNetworkName(), regions);
                }
            }

        }
        return map;
    }

    /**
     * Get the list of federated networks per regions.
     * @return
     */
    public Set<String> getFederatedNetworks() {
        Set<String> nets = new HashSet<String>();
        Map<String, Set<String>> map = getNetworksRegion();
        Iterator<Entry<String, Set<String>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Set<String>> d = iterator.next();
            if (d.getValue().size() > 1) {
                nets.add(d.getKey());
            }
        }

        return nets;
    }


    private int getNumberRegions() {
        Set<String> map = new HashSet<String>();
        for (Tier tier : this.getTiers()) {
            map.add(tier.getRegion());
        }
        return map.size();
    }

    /**
     * Check if the number of regios is gt 1.
     * @return
     */
    public boolean isDifferentRegions() {

        if (getNumberRegions() > 1) {
            return true;
        }
        return false;

    }

    /**
     * Check if the number of regios is gt 1 and the size is gt 0.
     * @return
     */
    public boolean isNetworkFederated() {
        if (!isDifferentRegions()) {
            return false;
        } else {
            if (getFederatedNetworks().size() > 0) {
                return true;
            }

        }
        return false;
    }

    /**
     * Get the network associated to a region and identified by its name.
     * @param region
     * @param networkName
     * @return
     */
    public Network getNetworkWithRegionAndName(String region, String networkName) {

        for (Tier tier : this.getTiers()) {
            if (tier.getRegion().equals(region)) {
                for (Network net : tier.getNetworks()) {
                    if (net.getNetworkName().equals(networkName)) {
                        return net;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[Environment]");
        sb.append("[id = ").append(this.id).append("]");
        sb.append("[name = ").append(this.name).append("]");
        sb.append("[org = ").append(this.org).append("]");
        sb.append("[description = ").append(this.description).append("]");
        sb.append("[vdc = ").append(this.vdc).append("]");
        sb.append("[ovf = ").append(this.ovf).append("]");
        sb.append("[tiers = ").append(this.tiers).append("]");
        sb.append("]");
        return sb.toString();
    }


}
