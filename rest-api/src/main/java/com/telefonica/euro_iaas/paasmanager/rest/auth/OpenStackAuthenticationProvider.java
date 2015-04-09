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

package com.telefonica.euro_iaas.paasmanager.rest.auth;

import java.util.HashSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import net.sf.ehcache.Cache;

import org.apache.http.conn.HttpClientConnectionManager;
import org.openstack.docs.identity.api.v2.AuthenticateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.telefonica.euro_iaas.paasmanager.bean.OpenStackAccess;
import com.telefonica.euro_iaas.paasmanager.bean.PaasManagerUser;
import com.telefonica.euro_iaas.paasmanager.model.ClaudiaData;
import com.telefonica.euro_iaas.paasmanager.util.PoolHttpClient;
import com.telefonica.euro_iaas.paasmanager.util.SystemPropertiesProvider;
import com.telefonica.euro_iaas.paasmanager.util.TokenCache;
import com.telefonica.euro_iaas.paasmanager.util.auth.OpenStackAuthenticationToken;

/**
 * The Class OpenStackAuthenticationProvider.
 * 
 * @author fernandolopezaguilar
 */
public class OpenStackAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    /**
     * The system properties provider.
     */
    private SystemPropertiesProvider systemPropertiesProvider;

    /**
     * The Constant CODE_200. HTTP 200 ok
     */
    public static final int CODE_200 = 200;
    /**
     * The Constant CODE_401.
     */
    public static final int CODE_401 = 401;
    /**
     * The log.
     */
    private static Logger log = LoggerFactory.getLogger(OpenStackAuthenticationProvider.class);

    /**
     * Thread to recover a valid X-Auth-Token.
     */
    private OpenStackAuthenticationToken oSAuthToken;

    /**
     * Cache for tokens.
     */
    private TokenCache tokenCache;

    /**
     * Jersey client used to validates token to OpenStack.
     */
    private Client client;

    /**
     * connection manager.
     */
    private HttpClientConnectionManager httpConnectionManager;

    /**
     * Default constructor.
     */
    public OpenStackAuthenticationProvider() {
        oSAuthToken = null;
        tokenCache = new TokenCache();
    }

    /*
     * (non-Javadoc) @seeorg.springframework.security.authentication.dao. AbstractUserDetailsAuthenticationProvider
     * #additionalAuthenticationChecks( org.springframework.security.core.userdetails.UserDetails, org.springframework
     * .security.authentication.UsernamePasswordAuthenticationToken)
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) {
    }

    /**
     * Authentication fiware.
     * 
     * @param token
     *            the token
     * @param tenantId
     *            the tenantId
     * @return the open stack user
     */

    private PaasManagerUser authenticationFiware(String token, String tenantId) {

        OpenStackAccess openStackAccess = generateOpenStackAuthenticationToken();

        log.debug("Keystone URL : " + oSAuthToken.getKeystoneURL());
        log.debug("adminToken : " + openStackAccess.getToken());

        PaasManagerUser paasManagerUser = tokenCache.getPaasManagerUser(token, tenantId);
        Response response = null;
        try {
            if (paasManagerUser == null) {

                WebTarget webResource = getClient().target(oSAuthToken.getKeystoneURL());
                WebTarget tokens = webResource.path(token);
                Invocation.Builder builder = tokens.request();
                response = builder.accept(MediaType.APPLICATION_XML).header("X-Auth-Token", openStackAccess.getToken())
                        .get();

                if (response.getStatus() == CODE_200) {
                    // Validate user's token
                    AuthenticateResponse authenticateResponse = response.readEntity(AuthenticateResponse.class);
                    PaasManagerUser userValidated = createPaasManagerUser(token, tenantId, authenticateResponse);
                    log.info("generated new token for tenantId:" + tenantId + ": " + token);
                    tokenCache.put(token + "-" + tenantId, userValidated);

                    return userValidated;
                } else {
                    log.warn("response status:" + response.getStatus());

                    if (response.getStatus() == CODE_401) {
                        throw new BadCredentialsException("Invalid token");
                    }

                    throw new AuthenticationServiceException("Invalid token");
                }
            } else {
                return paasManagerUser;
            }
        } catch (Exception e) {
            log.warn("Exception in authentication: " + e);
            throw new AuthenticationServiceException("Unknown problem", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * Creates paasManagerUser.
     * 
     * @param token
     * @param tenantId
     * @param authenticateResponse
     * @return
     */
    private PaasManagerUser createPaasManagerUser(String token, String tenantId,
            AuthenticateResponse authenticateResponse) {

        if (!tenantId.equals(authenticateResponse.getToken().getTenant().getId())) {
            throw new AuthenticationServiceException("Token " + authenticateResponse.getToken().getTenant().getId()
                    + " not valid for the tenantId provided:" + tenantId);
        }

        PaasManagerUser user = new PaasManagerUser(authenticateResponse.getUser().getOtherAttributes()
                .get(new QName("username")), token);

        user.setTenantId(tenantId);
        user.setTenantName(authenticateResponse.getToken().getTenant().getName());
        user.setToken(token);
        return user;
    }

    private OpenStackAccess generateOpenStackAuthenticationToken() {
        OpenStackAccess openStackAccess;

        openStackAccess = tokenCache.getAdmin();

        if (openStackAccess == null) {
            Client client = PoolHttpClient.getInstance(httpConnectionManager).getClient();
            if (oSAuthToken == null) {
                oSAuthToken = new OpenStackAuthenticationToken(systemPropertiesProvider);
            }

            openStackAccess = oSAuthToken.getAdminCredentials(client);
            tokenCache.putAdmin(openStackAccess);
        }
        return openStackAccess;

    }

    /**
     * Gets the system properties provider.
     * 
     * @return the systemPropertiesProvider
     */
    public final SystemPropertiesProvider getSystemPropertiesProvider() {
        return systemPropertiesProvider;
    }

    /*
     * (non-Javadoc) @seeorg.springframework.security.authentication.dao. AbstractUserDetailsAuthenticationProvider
     * #retrieveUser(java.lang.String, org .springframework.security.authentication.UsernamePasswordAuthenticationToken
     * )
     */
    @Override
    protected final UserDetails retrieveUser(final String username,
            final UsernamePasswordAuthenticationToken authentication) {

        if (null != authentication.getCredentials()) {
            String tenantId = authentication.getCredentials().toString();

            PaasManagerUser paasManagerUser = authenticationFiware(username, tenantId);

            UserDetails userDetails = new User(paasManagerUser.getUserName(), paasManagerUser.getToken(),
                    new HashSet<GrantedAuthority>());
            return userDetails;
        } else {
            String str = "Missing tenantId header";
            log.info(str);
            throw new BadCredentialsException(str);
        }

    }

    /**
     * Sets the system properties provider.
     * 
     * @param pSystemPropertiesProvider
     *            the systemPropertiesProvider to set
     */
    public void setSystemPropertiesProvider(SystemPropertiesProvider pSystemPropertiesProvider) {
        this.systemPropertiesProvider = pSystemPropertiesProvider;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {

        if (this.client == null) {
            this.client = PoolHttpClient.getInstance(httpConnectionManager).getClient();
        }
        return this.client;
    }

    /**
     * Setter the oSAuthToken.
     */
    public void setoSAuthToken(OpenStackAuthenticationToken oSAuthToken) {
        this.oSAuthToken = oSAuthToken;
    }

    public HttpClientConnectionManager getHttpConnectionManager() {
        return httpConnectionManager;
    }

    public void setHttpConnectionManager(HttpClientConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
    }

    /**
     * reset cache
     */
    public Cache getTokenCache() {
        return tokenCache.getCache();
    }

    /**
     * Add PaasManagerUser to claudiaData.
     * 
     * @param claudiaData
     */
    public static void addCredentialsToClaudiaData(ClaudiaData claudiaData) {

        PaasManagerUser paasManagerUser = new PaasManagerUser("unknown", "unknown");
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            if (usernamePasswordAuthenticationToken != null) {
                paasManagerUser.setToken(usernamePasswordAuthenticationToken.getPrincipal().toString());
                paasManagerUser.setTenantId(usernamePasswordAuthenticationToken.getCredentials().toString());

            }
        }
        claudiaData.setUser(paasManagerUser);

    }

}
