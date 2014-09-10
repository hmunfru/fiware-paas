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
import javax.persistence.ManyToOne;

import com.telefonica.euro_iaas.paasmanager.model.dto.ProductInstanceDto;

/**
 * The Product Instance entity.
 *
 * @author henar
 */
@Entity
public class ProductInstance extends InstallableInstance implements Comparable<ProductInstance> {

    @ManyToOne
    private ProductRelease productRelease;
    private String taskId = "";

    /**
     * Default Constructor.
     */
    public ProductInstance() {
        super();
    }

    /**
     * Constructor.
     *
     * @param productRelease
     * @param status
     * @param vdc
     */
    public ProductInstance(ProductRelease productRelease, Status status, String vdc) {
        super(status);
        this.productRelease = productRelease;
        setVdc(vdc);
    }

    /**
     * It compares the product instance.
     */
    public int compareTo(ProductInstance arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @return the productRelease
     */
    public ProductRelease getProductRelease() {
        return productRelease;
    }

    /**
     * @param productRelease the productRelease to set
     */
    public void setProductRelease(ProductRelease productRelease) {
        this.productRelease = productRelease;
    }

    public void setTaskId(String id) {
        taskId = id;
    }

    /**
     * It returns the dto.
     *
     * @return ProductInstanceDto.class
     */
    public ProductInstanceDto toDto() {
        ProductInstanceDto pInstanceDto = new ProductInstanceDto();
        pInstanceDto.setName(getName());
        pInstanceDto.setTaskId(this.taskId);

        if (getProductRelease() != null) {
            pInstanceDto.setProductReleaseDto(getProductRelease().toDto());
        }
        return pInstanceDto;

    }

    /**
     * Constructs a <code>String</code> with all attributes
     * in name = value format.
     *
     * @return a <code>String</code> representation
     * of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("[[ProductInstance]");
        sb.append("[productRelease = ").append(this.productRelease).append("]");
        sb.append("[taskId = ").append(this.taskId).append("]");
        sb.append("]");
        return sb.toString();
    }


}
