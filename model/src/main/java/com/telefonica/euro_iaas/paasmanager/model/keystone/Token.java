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

package com.telefonica.euro_iaas.paasmanager.model.keystone;

import javax.persistence.Entity;
import javax.persistence.Id;

import net.sf.json.JSONObject;

/**
 * @author jesus.movilla
 */
@Entity
public class Token {

    /*
     * @Id
     * @GeneratedValue(strategy = GenerationType.SEQUENCE) private Long tokenId;
     */

    /** The tokenId. */
    @Id
    private String id = "";

    /** The date when the token expires. */
    private String expires = "";

    /** extra Info. */
    private String extra = "";

    /** tenantId. **/
    private String tenantId = "";

    /**
	 * 
	 */
    public Token() {
        super();
    }

    /**
     * @param id
     */
    public Token(String id) {
        super();
        this.id = id;
    }

    /**
     * @param id
     * @param expires
     * @param extra
     */
    public Token(String id, String expires, String extra) {
        super();
        this.id = id;
        this.expires = expires;
        this.extra = extra;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the expires
     */
    public String getExpires() {
        return expires;
    }

    /**
     * @param expires
     *            the expires to set
     */
    public void setExpires(String expires) {
        this.expires = expires;
    }

    /**
     * @return the extra
     */
    public String getExtra() {
        return extra;
    }

    /**
     * @param extra
     *            the extra to set
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId
     *            the tenantId to set
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public void setTenantId(JSONObject object) {
        this.tenantId = object.getString("tenantId");
    }
}
