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

package com.telefonica.euro_iaas.paasmanager.rest.util;

import java.io.InputStream;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.openstack.docs.identity.api.v2.AuthenticateResponse;

public class AdminTokenCache {

    public static final String CACHE_NAME = "adminToken";

    private Cache cache;

    public AdminTokenCache() {

        CacheManager singletonManager;
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/ehcache.xml");
            singletonManager = CacheManager.newInstance(inputStream);
        } catch (Exception e) {
            singletonManager = CacheManager.create();
            singletonManager.addCache(CACHE_NAME);
            cache.getCacheConfiguration();
            cache = singletonManager.getCache(CACHE_NAME);
            CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
            cacheConfiguration.setTimeToIdleSeconds(1200);
            cacheConfiguration.setTimeToLiveSeconds(1200);

        }
        cache = singletonManager.getCache(CACHE_NAME);

    }

    public CacheConfiguration getConfiguration() {
        CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
        return cacheConfiguration;
    }

    public void put(String key, AuthenticateResponse authenticateResponse) {
        cache.put(new Element(key, authenticateResponse));
    }

    public AuthenticateResponse getAuthenticateResponse(String key) {

        if (cache.isKeyInCache(key) && (cache.get(key) != null)) {

            return (AuthenticateResponse) cache.get(key).getObjectValue();
        } else {
            return null;
        }
    }
}
