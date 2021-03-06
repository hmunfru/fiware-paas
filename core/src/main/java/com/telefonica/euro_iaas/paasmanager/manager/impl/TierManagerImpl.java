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

package com.telefonica.euro_iaas.paasmanager.manager.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.telefonica.euro_iaas.commons.dao.AlreadyExistsEntityException;
import com.telefonica.euro_iaas.commons.dao.EntityNotFoundException;
import com.telefonica.euro_iaas.commons.dao.InvalidEntityException;
import com.telefonica.euro_iaas.paasmanager.dao.TierDao;
import com.telefonica.euro_iaas.paasmanager.exception.InfrastructureException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidEnvironmentRequestException;
import com.telefonica.euro_iaas.paasmanager.exception.InvalidSecurityGroupRequestException;
import com.telefonica.euro_iaas.paasmanager.manager.NetworkManager;
import com.telefonica.euro_iaas.paasmanager.manager.ProductReleaseManager;
import com.telefonica.euro_iaas.paasmanager.manager.SecurityGroupManager;
import com.telefonica.euro_iaas.paasmanager.manager.TierManager;
import com.telefonica.euro_iaas.paasmanager.model.Attribute;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.model.Environment;
import com.telefonica.euro_iaas.paasmanager.model.Metadata;
import com.telefonica.euro_iaas.paasmanager.model.Network;
import com.telefonica.euro_iaas.paasmanager.model.ProductRelease;
import com.telefonica.euro_iaas.paasmanager.model.Rule;
import com.telefonica.euro_iaas.paasmanager.model.SecurityGroup;
import com.telefonica.euro_iaas.paasmanager.model.Tier;
import com.telefonica.euro_iaas.paasmanager.model.searchcriteria.TierSearchCriteria;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;

/**
 * It is the manager for the Tier.
 * 
 * @author henar
 */
public class TierManagerImpl implements TierManager {

    private TierDao tierDao;

    private ProductReleaseManager productReleaseManager;
    private SecurityGroupManager securityGroupManager;

    private NetworkManager networkManager;

    private SystemPropertiesProvider systemPropertiesProvider;

    private static Logger log = Logger.getLogger(TierManagerImpl.class);

    /**
     * It add teh security groups related the products.
     */
    public void addSecurityGroupToProductRelease(ClaudiaData claudiaData, Tier tier, ProductRelease productRelease)
            throws InvalidEntityException, AlreadyExistsEntityException, InfrastructureException {
        Attribute openPortsAttribute = productRelease.getAttribute("openports");
        if (openPortsAttribute != null) {
            StringTokenizer st = new StringTokenizer(openPortsAttribute.getValue());
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                Rule rule = createRulePort(token);

                securityGroupManager.addRule(tier.getRegion(), token, tier.getVdc(), tier.getSecurityGroup(), rule);

            }
        }
    }

    /**
     * It creates a tier.
     * 
     * @throws EntityNotFoundException
     * @throws AlreadyExistsEntityException
     */

    public Tier create(ClaudiaData claudiaData, String envName, Tier tier) throws InvalidEntityException,
            InvalidSecurityGroupRequestException, InfrastructureException, EntityNotFoundException,
            AlreadyExistsEntityException {
        log.debug("Create tier name " + tier.getName() + " image " + tier.getImage() + " flavour " + tier.getFlavour()
                + " initial_number_instances " + tier.getInitialNumberInstances() + " maximum_number_instances "
                + tier.getMaximumNumberInstances() + " minimum_number_instances " + tier.getMinimumNumberInstances()
                + " floatingip " + tier.getFloatingip() + " keypair " + tier.getKeypair() + " icono " + tier.getIcono()
                + " product releases " + tier.getProductReleases() + "  vdc " + claudiaData.getVdc() + " networks "
                + tier.getNetworks());

        if (exists(tier.getName(), tier.getVdc(), envName)) {
            return load(tier.getName(), claudiaData.getVdc(), envName);
        } else {

            // check if exist product or need sync with SDC
            existProductOrSyncWithSDC(tier);

            createSecurityGroups(claudiaData, tier);

            createNetworks(tier);

            return tierInsertBD(tier, claudiaData);

        }
    }

    private void existProductOrSyncWithSDC(Tier tier) throws InvalidEntityException {

        if (tier.getProductReleases() != null && tier.getProductReleases().size() != 0) {
            for (ProductRelease prod : tier.getProductReleases()) {
                try {
                    log.debug("Sync product release " + prod.getProduct() + "-" + prod.getVersion());
                    prod = productReleaseManager.load(prod.getProduct() + "-" + prod.getVersion());
                } catch (Exception e2) {
                    String errorMessage = "The ProductRelease Object " + prod.getProduct() + "-" + prod.getVersion()
                            + " not exist in database";
                    log.error(errorMessage);
                    throw new InvalidEntityException(e2);
                }
            }
        }
    }

    /**
     * It creates the rule port for ssh.
     * 
     * @param port
     * @return
     */

    public Rule createRulePort(String port) {
        log.debug("Generate security rule " + port);
        Rule rule = new Rule("TCP", port, port, "", "0.0.0.0/0");
        return rule;
    }

    /**
     * It creates the specified security groups.
     * 
     * @param claudiaData
     * @param tier
     * @return
     * @throws InvalidSecurityGroupRequestException
     * @throws EntityNotFoundException
     */

    private void createSecurityGroups(ClaudiaData claudiaData, Tier tier) throws InvalidSecurityGroupRequestException,
            EntityNotFoundException {
        if ((systemPropertiesProvider.getProperty(SystemPropertiesProvider.CLOUD_SYSTEM).equals("FIWARE")
                && claudiaData.getVdc() != null && claudiaData.getVdc().length() > 0)) {

            SecurityGroup securityGroup = generateSecurityGroup(claudiaData, tier);
            try {
                securityGroup = securityGroupManager.create(tier.getRegion(), claudiaData.getUser().getToken(),
                        claudiaData.getVdc(), securityGroup);
            } catch (InvalidEntityException e) {
                log.error("It is not posssible to create the security group " + securityGroup.getName() + " "
                        + e.getMessage());
                throw new InvalidSecurityGroupRequestException("It is not posssible to create the security group "
                        + securityGroup.getName() + " " + e.getMessage(), e);
            } catch (InvalidEnvironmentRequestException e) {

                log.error("It is not posssible to create the security group " + securityGroup.getName() + " "
                        + e.getMessage());
                throw new InvalidSecurityGroupRequestException("It is not posssible to create the security group "
                        + securityGroup.getName() + " " + e.getMessage(), e);
            } catch (AlreadyExistsEntityException e) {
                log.error("It is not posssible to create the security group " + securityGroup.getName() + " "
                        + e.getMessage());
                throw new InvalidSecurityGroupRequestException("It is not posssible to create the security group "
                        + securityGroup.getName() + " " + e.getMessage(), e);
            } catch (InfrastructureException e) {
                log.error("It is not posssible to create the security group " + securityGroup.getName() + " "
                        + e.getMessage());
                throw new InvalidSecurityGroupRequestException("It is not posssible to create the security group "
                        + securityGroup.getName() + " " + e.getMessage(), e);

            }
            tier.setSecurityGroup(securityGroup);
        }

    }

    public void createNetworks(Tier tier) throws EntityNotFoundException, InvalidEntityException,
            AlreadyExistsEntityException {
        List<Network> networkToBeDeployed = new ArrayList<Network>();
        for (Network network : tier.getNetworks()) {
            networkToBeDeployed.add(network);
        }

        for (Network network : networkToBeDeployed) {
            if (networkManager.exists(network.getNetworkName(), network.getVdc())) {
                log.debug("the network " + network.getNetworkName() + " already exists");
                network = networkManager.load(network.getNetworkName(), network.getVdc());

            } else {
                network = networkManager.create(network);
            }
            tier.addNetwork(network);
        }
    }

    /**
     * It deletes the tier.
     * 
     * @param claudiaData
     * @param tier
     */
    public void delete(ClaudiaData claudiaData, Tier tier) throws EntityNotFoundException, InvalidEntityException,
            InfrastructureException {

        try {
            tier = loadTierWithNetworks(tier.getName(), tier.getVdc(), tier.getEnviromentName());
            log.debug("Deleting tier " + tier.getName() + " from vdc " + tier.getVdc() + "  env  "
                    + tier.getEnviromentName() + " " + tier.getNetworks());
        } catch (EntityNotFoundException e) {

            String mens = "It is not possible to delete the tier " + tier.getName() + " since it is not exist";
            log.error(mens);
            throw new EntityNotFoundException(Tier.class, mens, tier);
        }

        if (tier.getSecurityGroup() != null && !tier.getVdc().isEmpty()) {
            SecurityGroup sec = tier.getSecurityGroup();
            log.debug("Deleting security group " + sec.getName() + " in tier " + tier.getName());
            tier.setSecurityGroup(null);
            tierDao.update(tier);
            securityGroupManager.destroy(tier.getRegion(), claudiaData.getUser().getToken(), tier.getVdc(), sec);

        }

        log.debug("Deleting the networks " + tier.getNetworks());

        List<Network> netsAux = new ArrayList<Network>();
        for (Network netNet : tier.getNetworks()) {
            netsAux.add(netNet);
        }

        tier.setNetworks(null);
        tierDao.update(tier);

        for (Network net : netsAux) {

            if (isAvailableToBeDeleted(net)) {
                log.debug("Deleting network " + net.getNetworkName());
                try {
                    networkManager.delete(net);
                } catch (Exception e) {
                    log.debug("There is an error to delete the network");
                }
            }

        }
        log.debug("Networks deleted");

        try {
            tierDao.remove(tier);
        } catch (Exception e) {
            String mens = "It is not possible to delete the tier since it is not exist " + e.getMessage();
            log.error(mens);
            throw new InvalidEntityException(tier, e);
        }

    }

    private boolean isAvailableToBeDeleted(Network net) {

        List<Tier> tiers = tierDao.findAllWithNetwork(net.getNetworkName());
        if (tiers.isEmpty()) {
            log.debug("The network " + net + " can be deleted");
            return true;
        } else {
            log.debug("The network " + net.getNetworkName() + " cannot be deleted. The following tiers are using it");
            for (Tier tier : tiers) {
                log.debug(tier.getName());
            }
            return false;
        }

    }

    public List<Tier> findAll() {

        return tierDao.findAll();
    }

    public List<Tier> findByCriteria(TierSearchCriteria criteria) throws EntityNotFoundException {
        return tierDao.findByCriteria(criteria);
    }

    public List<Tier> findByEnvironment(Environment environment) {
        return null;
    }

    public SecurityGroup generateSecurityGroup(ClaudiaData claudiaData, Tier tier) throws EntityNotFoundException {

        SecurityGroup securityGroup = new SecurityGroup();
        securityGroup.setName("sg_" + claudiaData.getService() + "_" + claudiaData.getVdc() + "_" + tier.getName());

        log.debug("Generate security group " + "sg_" + claudiaData.getService() + "_" + claudiaData.getVdc() + "_"
                + tier.getName());

        List<Rule> rules = getDefaultRules();

        if (tier.getProductReleases() != null) {

            for (ProductRelease productRelease : tier.getProductReleases()) {
                getRulesFromProduct(productRelease, rules);
            }

        }
        securityGroup.setRules(rules);
        return securityGroup;
    }

    public List<Rule> getDefaultRules() {
        List<Rule> rules = new ArrayList<Rule>();
        // 9990
        log.debug("Generate security rule " + 9990);

        Rule rule = new Rule("TCP", "9990", "9990", "", systemPropertiesProvider.getProperty("sdcIp") + "/32");

        rules.add(rule);
        Rule rule2 = new Rule("TCP", "22", "22", "", "0.0.0.0/0");
        rules.add(rule2);
        return rules;

    }

    private void getRulesFromProduct(ProductRelease productRelease, List<Rule> rules) throws EntityNotFoundException {

        productRelease = productReleaseManager.loadWithMetadata(productRelease.getProduct() + "-"
                + productRelease.getVersion());
        Metadata openPortsAttribute = productRelease.getMetadata("open_ports");
        if (openPortsAttribute != null) {
            log.debug("Adding product rule " + openPortsAttribute.getValue());
            StringTokenizer st = new StringTokenizer(openPortsAttribute.getValue());
            while (st.hasMoreTokens()) {
                Rule rule = createRulePort(st.nextToken());
                if (!rules.contains(rule)) {
                    log.debug("New rule ");
                    rules.add(rule);
                }
            }
        }
    }

    public Tier load(String name, String vdc, String environmentName) throws EntityNotFoundException {
        try {
            return tierDao.load(name, vdc, environmentName);
        } catch (Exception e) {
            throw new EntityNotFoundException(Tier.class, "error", e.getMessage());
        }
    }

    public boolean exists(String name, String vdc, String environmentName) {
        try {
            tierDao.load(name, vdc, environmentName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Tier loadTierWithProductReleaseAndMetadata(String tierName, String environmentName, String vdc)
            throws EntityNotFoundException {
        try {
            return tierDao.loadTierWithProductReleaseAndMetadata(tierName, vdc, environmentName);
        } catch (Exception e) {
            String message = "Tier " + tierName + " not found";
            throw new EntityNotFoundException(Tier.class, message, e.getMessage());
        }
    }

    public Tier loadTierWithNetworks(String tierName, String vdc, String environmentName)
            throws EntityNotFoundException {
        try {
            return tierDao.loadTierWithNetworks(tierName, vdc, environmentName);

        } catch (Exception e) {
            String message = "Tier " + tierName + " not found";
            throw new EntityNotFoundException(Tier.class, message, e.getMessage());
        }
    }

    private void restore(ClaudiaData claudiaData, Tier tier) throws InvalidEntityException, InfrastructureException {
        if (tier.getSecurityGroup() != null) {
            securityGroupManager.destroy(tier.getRegion(), tier.getVdc(), claudiaData.getUser().getToken(),
                    tier.getSecurityGroup());

        }
    }

    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public void setProductReleaseManager(ProductReleaseManager productReleaseManager) {
        this.productReleaseManager = productReleaseManager;

    }

    public void setSecurityGroupManager(SecurityGroupManager securityGroupManager) {
        this.securityGroupManager = securityGroupManager;
    }

    public void setSystemPropertiesProvider(SystemPropertiesProvider systemPropertiesProvider) {

        this.systemPropertiesProvider = systemPropertiesProvider;
    }

    /**
     * @param tierDao
     *            the tierDao to set
     */
    public void setTierDao(TierDao tierDao) {
        this.tierDao = tierDao;
    }

    /**
     * It insert the tier in the database.
     * 
     * @param tier
     * @param data
     * @return
     * @throws InvalidEntityException
     * @throws InfrastructureException
     */
    public Tier tierInsertBD(Tier tier, ClaudiaData data) throws InvalidEntityException, InfrastructureException {
        List<Network> networskout = new ArrayList();
        try {
            return load(tier.getName(), tier.getVdc(), tier.getEnviromentName());
        } catch (EntityNotFoundException e) {

            // tier.setVdc(data.getVdc());
            // tier.setEnviromentName(data.getService());

            List<ProductRelease> productReleases = new ArrayList();
            if (tier.getProductReleases() != null && tier.getProductReleases().size() != 0) {
                for (ProductRelease p : tier.getProductReleases()) {
                    productReleases.add(p);
                }
            } else {

                log.warn("There is not any product release associated to the tier " + tier.getName());
            }

            for (Network p : tier.getNetworks()) {
                networskout.add(p);
            }

            try {
                tier.setProductReleases(null);
                tier.setNetworks(null);
                tier = tierDao.create(tier);
            } catch (Exception e2) {
                String errorMessage = "The Tier  " + tier.getName() + "  cannot be created " + e2.getMessage();
                log.error(errorMessage);
                restore(data, tier);
                throw new InvalidEntityException(errorMessage);
            }

            if (productReleases != null && productReleases.size() != 0) {
                for (ProductRelease product : productReleases) {

                    try {
                        ProductRelease templateProduct = productReleaseManager.load(product.getProduct() + "-"
                                + product.getVersion());
                        log.debug("Adding product release " + templateProduct.getProduct() + "-"
                                + templateProduct.getVersion() + " to tier " + templateProduct.getName());

                        mergeAttributes(product, templateProduct);
                        mergeMetadatas(product, templateProduct);

                        tier.addProductRelease(templateProduct);

                        tier = update(tier);
                    } catch (Exception e2) {
                        String errorMessage = "The ProductRelease Object " + product.getProduct() + "-"
                                + product.getVersion() + " is " + "NOT present in Database";
                        log.error(errorMessage);
                        throw new InvalidEntityException(e2);
                    }
                }
            }

        }

        for (Network net : networskout) {

            try {
                net = networkManager.load(net.getNetworkName(), net.getVdc());
                log.debug("Adding network " + net.getNetworkName() + "-" + " to tier " + tier.getName());
                tier.addNetwork(net);
                update(tier);
            } catch (Exception e2) {
                String errorMessage = "The Network Object " + net.getNetworkName() + " is " + "NOT present in Database";
                log.error(errorMessage);
                throw new InvalidEntityException(e2);
            }
        }

        return tier;
    }

    public void mergeAttributes(ProductRelease product, ProductRelease templateProduct) {

        if (product.getAttributes() != null) {
            if (!templateProduct.getAttributes().isEmpty()) {

                for (Attribute attribute : product.getAttributes()) {

                    String key = attribute.getKey();
                    boolean notExistInTemplate = templateProduct.getAttribute(key) == null;
                    if (notExistInTemplate) {
                        templateProduct.addAttribute(attribute);
                    } else {
                        templateProduct.getAttribute(key).setValue(attribute.getValue());
                    }
                }

            } else {
                templateProduct.setAttributes(product.getAttributes());
            }
        }
    }

    public void mergeMetadatas(ProductRelease productRelease, ProductRelease newProductRelease) {
        if (productRelease.getMetadatas() != null) {
            if (!newProductRelease.getMetadatas().isEmpty()) {

                for (Metadata metadata : productRelease.getMetadatas()) {

                    String key = metadata.getKey();
                    boolean notExistInTemplate = newProductRelease.getMetadata(key) == null;
                    if (notExistInTemplate) {
                        newProductRelease.addMetadata(metadata);
                    } else {
                        newProductRelease.getMetadata(key).setValue(metadata.getValue());
                    }
                }

            } else {
                newProductRelease.setMetadatas(productRelease.getMetadatas());
            }
        }
    }

    public Tier update(Tier tier) throws InvalidEntityException {
        log.debug("Update tier " + tier.getName());
        try {
            return tierDao.update(tier);
        } catch (Exception e) {
            log.error("It is not possible to update the tier " + tier.getName() + " : " + e.getMessage(), e);
            throw new InvalidEntityException("It is not possible to update the tier " + tier.getName() + " : "
                    + e.getMessage());
        }

    }

    public void updateTier(Tier tierold, Tier tiernew) throws InvalidEntityException, EntityNotFoundException,
            AlreadyExistsEntityException {

        tierold.setFlavour(tiernew.getFlavour());
        tierold.setFloatingip(tiernew.getFloatingip());
        tierold.setIcono(tiernew.getIcono());
        tierold.setImage(tiernew.getImage());
        tierold.setInitialNumberInstances(tiernew.getInitialNumberInstances());
        tierold.setKeypair(tiernew.getKeypair());
        tierold.setMaximumNumberInstances(tiernew.getMaximumNumberInstances());
        tierold.setMinimumNumberInstances(tiernew.getMinimumNumberInstances());

        update(tierold);

        // Get networks to be delete
        Set<Network> nets = new HashSet<Network>();
        for (Network net : tierold.getNetworks()) {
            nets.add(net);
        }

        // delete networks
        tierold.setNetworks(null);
        update(tierold);

        // adding networks
        for (Network net : tiernew.getNetworks()) {
            log.debug("Creating new network " + net.getNetworkName());
            try {
                net = networkManager.create(net);
            } catch (AlreadyExistsEntityException e) {
                net = networkManager.load(net.getNetworkName(), net.getVdc());
            }
            tierold.addNetwork(net);
            update(tierold);
        }

        for (Network net : nets) {
            networkManager.delete(net);
        }

        tierold.setProductReleases(null);
        update(tierold);

        if (tiernew.getProductReleases() == null)
            return;

        for (ProductRelease productRelease : tiernew.getProductReleases()) {
            try {
                productRelease = productReleaseManager.load(productRelease.getProduct() + "-"
                        + productRelease.getVersion());
            } catch (EntityNotFoundException e) {
                log.error("The new software " + productRelease.getProduct() + "-" + productRelease.getVersion()
                        + " is not found");

            }
            tierold.addProductRelease(productRelease);
            update(tierold);
        }

    }

}
