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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.telefonica.euro_iaas.paasmanager.model.dto.ProductInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.TierInstanceDto;
import com.telefonica.euro_iaas.paasmanager.model.dto.VM;

/**
 * Represents an instance of a tier.
 * 
 * @author Jesus M. Movilla
 * @version $Id: $
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TierInstance extends InstallableInstance {

    @ManyToOne
    private Tier tier;

    /**
     * the vmOVF. **
     */
    @Column(length = 100000)
    private String ovf = "";

    /**
     * the VAPP. **
     */
    @Column(length = 10000)
    private String vapp = "";
    private String taskId = "";

    private int numberReplica = 0;

    @Embedded
    private VM vm;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tierinstance_has_productinstances", 
    joinColumns = { @JoinColumn(name = "tierinstance_ID", nullable = false, updatable = false) }, 
    inverseJoinColumns = { @JoinColumn(name = "productinstance_ID", nullable = false, updatable = false) })
    private List<ProductInstance> productInstances;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tierinstance_has_networkinstance", 
    joinColumns = { @JoinColumn(name = "tierinstance_ID", nullable = false, updatable = false) }, 
    inverseJoinColumns = { @JoinColumn(name = "networkinstance_ID", nullable = false, updatable = false) })
    private Set<NetworkInstance> networkInstances;

    @ManyToOne(fetch = FetchType.EAGER)
    private SecurityGroup securityGroup;
    
    /**
     * Constructor.
     */
    public TierInstance() {

    }

    /**
     * @param tier
     * @param productInstances
     * @param name
     * @param vm
     * @param ovf
     */

    public TierInstance(Tier tier, List<ProductInstance> productInstances, String name, VM vm, String ovf) {
        super();
        this.tier = tier;
        this.productInstances = productInstances;
        this.ovf = ovf;
        this.name = name;
        this.vm = vm;
    }

    /**
     * @param tier2
     * @param productInstances2
     * @param vm
     */
    public TierInstance(Tier tier2, List<ProductInstance> productInstances2, VM vm) {
        this.tier = tier2;
        this.productInstances = productInstances2;
        this.vm = vm;
    }

    /**
     * @param tier
     * @param ovf
     * @param name
     * @param vm
     */
    public TierInstance(Tier tier, String ovf, String name, VM vm) {
        super();
        this.tier = tier;
        this.ovf = ovf;
        this.name = name;
        this.vm = vm;
    }

    /**
     * @param productInstance
     */
    public void addProductInstance(ProductInstance productInstance) {
        if (productInstances == null) {
            productInstances = new ArrayList<ProductInstance>();
        }
        this.productInstances.add(productInstance);
    }

    /**
     * @param networkInstance
     */
    public void addNetworkInstance(NetworkInstance networkInstance) {
        if (networkInstances == null) {
            networkInstances = new HashSet<NetworkInstance>();
        }
        this.networkInstances.add(networkInstance);
    }

    /**
     * @param productInstance
     */
    public void deleteProductInstance(ProductInstance productInstance) {
        if (productInstances.contains(productInstance)) {
            this.productInstances.remove(productInstance);
        }
    }

    /**
     * Clone the network instances.
     * 
     * @return
     */
    public Set<NetworkInstance> cloneNetworkInt() {
        Set<NetworkInstance> netInts = new HashSet<NetworkInstance>();
        for (NetworkInstance netInst : this.getNetworkInstances()) {
            netInts.add(netInst);
        }
        return netInts;
    }

    /**
     * @param networkInstance
     */
    public void deleteNetworkInstance(NetworkInstance networkInstance) {
        if (networkInstances.contains(networkInstance)) {
            this.networkInstances.remove(networkInstance);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public int getNumberReplica() {
        return numberReplica;
    }

    /**
     * @return the tier
     */
    public String getOvf() {
        return ovf;
    }

    /**
     * @return the productInstances
     */
    public List<ProductInstance> getProductInstances() {
        return productInstances;
    }

    /**
     * @return the productInstances
     */
    public Set<NetworkInstance> getNetworkInstances() {
        if (networkInstances == null) {
            networkInstances = new HashSet<NetworkInstance>();
        }
        return networkInstances;
    }

    public String getTaskId() {
        return taskId;

    }

    public SecurityGroup getSecurityGroup() {
        return this.securityGroup;
    }
    
    /**
     * Check if there is a public network (Internet) in the list of network instances.
     * 
     * @return
     */
    public boolean isTherePublicNet() {
        for (NetworkInstance net : networkInstances) {
            if (net.getNetworkName().equals("Internet")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the number of no public networks (with name different to Internet).
     * 
     * @return
     */
    public int getNetworkNumberNoPublic() {
        int i = 0;
        if (networkInstances == null) {
            return 0;
        }
        for (NetworkInstance net : networkInstances) {
            if (!net.getNetworkName().equals("Internet")) {
                i++;
            }
        }
        return i;
    }

    /**
     * @return the tier
     */
    public Tier getTier() {
        return tier;
    }

    public String getVApp() {
        return vapp;
    }

    public VM getVM() {
        return vm;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;

    }

    public void setNumberReplica(int numberReplica) {
        this.numberReplica = numberReplica;
    }

    /**
     * @param ovf
     */
    public void setOvf(String ovf) {
        this.ovf = ovf;
    }

    /**
     * @param productInstances
     *            the productInstances to set
     */
    public void setProductInstances(List<ProductInstance> productInstances) {
        this.productInstances = productInstances;
    }

    /**
     * @param networkInstances
     *            the networkInstances to set
     */
    public void setNetworkInstance(Set<NetworkInstance> networkInstances) {
        this.networkInstances = networkInstances;
    }

    public void setTaskId(String id) {
        taskId = id;

    }

    public void setSecurityGroup(SecurityGroup securityGroup) {
        this.securityGroup = securityGroup;
    }

    /**
     * @param tier
     *            the tier to set
     */
    public void setTier(Tier tier) {
        this.tier = tier;
    }

    /**
     * @param vapp
     */
    public void setVapp(String vapp) {
        this.vapp = vapp;
    }

    public void setVM(VM vm) {
        this.vm = vm;
    }

    /**
     * The Dto specification.
     * 
     * @return
     */
    public TierInstanceDto toDto() {
        TierInstanceDto tierInstanceDto = new TierInstanceDto();
        tierInstanceDto.setTierInstanceName(getName());
        tierInstanceDto.setTierDto(getTier().toDto());
        tierInstanceDto.setReplicaNumber(getNumberReplica());
        if (getSecurityGroup() != null) {
        	tierInstanceDto.setSecurityGroup(this.getSecurityGroup().getName());
        }
        if (this.getVM() != null) {
            tierInstanceDto.setVM(getVM().toDto());
        }

        List<ProductInstanceDto> lProductInstanceDto = new ArrayList<ProductInstanceDto>();
        if (getProductInstances() != null) {
            for (ProductInstance productInstance : getProductInstances()) {
                ProductInstanceDto productInstanceDto = productInstance.toDto();
                lProductInstanceDto.add(productInstanceDto);
            }
        }

        tierInstanceDto.setProductInstanceDtos(lProductInstanceDto);

        if (getPrivateAttributes() != null) {
            tierInstanceDto.setAttributes(getPrivateAttributes());
        }

        return tierInstanceDto;
    }

    /**
     * to json.
     * 
     * @return
     */
    public String toJson(String userData) {
        String payload = "{\"server\": " + "{\"key_name\": \"" + getTier().getKeypair() + "\", ";
        if (getSecurityGroup() != null) {
            payload = payload + "\"security_groups\": [{ \"name\": \"" + getSecurityGroup().getName()
                    + "\"}], ";
        }
        if (!this.getNetworkInstances().isEmpty()) {
            payload = payload + "\"networks\": [";
            int count = 0;
            for (NetworkInstance net : this.getNetworkInstances()) {

                if (count == this.getNetworkInstances().size() - 1) {
                    payload = payload + " {\"uuid\": \"" + net.getIdNetwork() + "\"} ";
                } else {
                    payload = payload + " {\"uuid\": \"" + net.getIdNetwork() + "\"} ,";
                }
                count++;

            }
            payload = payload + "], ";

        }

        if (this.getTier().getAffinity() != "None") {
            String id = this.getVM().getFqn().substring(0, this.getVM().getFqn().indexOf(".vee"));
            String group = null;
            if (this.getTier().getAffinity().equals("anti-affinity")) {

                group = "\"group\": \"" + id + "\"";

            } else {
                group = "\"group\": \"affinity:" + id + "\"";
            }
            payload = payload + "\"os:scheduler_hints\": { " + group + "},";
        }

        payload = createMetadata(payload);

        if (userData != null) {
            payload += "\"user_data\": \"" + userData + "\",";
        }
        payload += "\"flavorRef\": \"" + getTier().getFlavour() + "\", " + "\"imageRef\": \"" + getTier().getImage()
                + "\", " + "\"name\": \"" + name + "\"}}";

        return payload;

    }

    private String createMetadata(String payload) {

        payload += "\"metadata\": {";

        if (this.getTier().getRegion() != null) {

            payload += "\"region\": \"" + this.getTier().getRegion() + "\"";
        }

        if (this.getTier().getProductReleases() != null) {
            List<ProductRelease> list = this.getTier().getProductReleases();

            for (ProductRelease productRelease : list) {

                Metadata nid = productRelease.getMetadata("nid");
                if (nid != null) {
                    payload += ",\"nid\": \"" + nid.getValue() + "\"";

                }

            }

        }

        payload += "},";

        return payload;
    }

    /**
     * Update the Application instance with a new tier.
     * 
     * @param tier2
     */
    public void update(Tier tier2) {
        tier = tier2;

    }

    /**
     * Constructs a <code>String</code> with all attributes in name = value format.
     * 
     * @return a <code>String</code> representation of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[TierInstance]");
        sb.append("[tier = ").append(this.tier).append("]");
        sb.append("[ovf = ").append(this.ovf).append("]");
        sb.append("[vapp = ").append(this.vapp).append("]");
        sb.append("[taskId = ").append(this.taskId).append("]");
        sb.append("[numberReplica = ").append(this.numberReplica).append("]");
        sb.append("[vm = ").append(this.vm).append("]");
        sb.append("[productInstances = ").append(this.productInstances).append("]");
        sb.append("[networkInstances = ").append(this.networkInstances).append("]");
        sb.append("[securityGroup = ").append(this.securityGroup).append("]");
        sb.append("]");
        return sb.toString();
    }

}
