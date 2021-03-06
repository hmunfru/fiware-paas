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

package com.telefonica.euro_iaas.paasmanager.installator.rec.services.impl;

import java.text.MessageFormat;

import com.telefonica.euro_iaas.paasmanager.exception.ProductInstallatorException;
import com.telefonica.euro_iaas.paasmanager.exception.ProductReconfigurationException;
import com.telefonica.euro_iaas.paasmanager.installator.rec.client.ClientConstants;
import com.telefonica.euro_iaas.paasmanager.installator.rec.services.RECVMService;
import com.telefonica.euro_iaas.paasmanager.installator.rec.util.VappUtils;
import org.apache.log4j.Logger;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.DomRepresentation;
import org.w3c.dom.Document;

/*
 * import com.sun.jersey.api.client.Client; import com.sun.jersey.api.client.WebResource; import
 * com.sun.jersey.api.client.WebResource.Builder;
 */

/*
 * import com.sun.jersey.api.client.Client; import com.sun.jersey.api.client.WebResource; import
 * com.sun.jersey.api.client.WebResource.Builder;
 */

/*
 * import com.sun.jersey.api.client.Client; import com.sun.jersey.api.client.WebResource; import
 * com.sun.jersey.api.client.WebResource.Builder;
 */

/*
 * import com.sun.jersey.api.client.Client; import com.sun.jersey.api.client.WebResource; import
 * com.sun.jersey.api.client.WebResource.Builder;
 */

/**
 * @author jesus.movilla
 */
public class RECVMServiceImpl extends AbstractBaseService implements RECVMService {

    private MediaType APPLICATION_OVF_XML;
    private VappUtils vappUtils;
    private static Logger log = Logger.getLogger(RECVMServiceImpl.class);

    public RECVMServiceImpl(Client client, String baseUrl, String mediaType) {
        APPLICATION_OVF_XML = MediaType.register(mediaType, "XML OVF document");
        setBaseHost(baseUrl);
        setType(APPLICATION_OVF_XML);
        setClient(client);
    }

    /*
     * (non-Javadoc)
     * @see com.telefonica.euro_iaas.paasmanager.util.rec.services.RECVMService#createVM (java.lang.String)
     */
    public void configureVM(String recVapp, String recVMname, String appId) throws ProductReconfigurationException {

        Reference urlGet = new Reference(getBaseHost() + MessageFormat.format(ClientConstants.BASE_VM_PATH, appId)
                + "/" + recVMname);

        Response getResponse = client.get(urlGet);

        if (!getResponse.getStatus().equals(Status.SUCCESS_OK)) {

            throw new ProductReconfigurationException("The VM is not register before the reconfiguration");
        }

    }

    public void createVM(String recVapp, String recVMname, String appId) throws ProductInstallatorException {

        DomRepresentation data = null;
        Response postResponse = null;

        Reference urlPost = new Reference(getBaseHost() + MessageFormat.format(ClientConstants.BASE_VM_PATH, appId));

        Reference urlGet = new Reference(getBaseHost() + MessageFormat.format(ClientConstants.BASE_VM_PATH, appId)
                + "/" + recVMname);

        Response getResponse = client.get(urlGet);

        if (!getResponse.getStatus().equals(Status.SUCCESS_OK)) {
            Document doc = getDocument(recVapp);
            data = new DomRepresentation(APPLICATION_OVF_XML, doc);
            postResponse = client.post(urlPost, data);

            checkRECTaskStatus(postResponse);
        }

    }

    /*
     * private Document getDocumentFromVapp(String vapp, String ip, String login, String password) throws
     * ProductInstallatorException { VirtualSystemType vs = new VirtualSystemType(); //vappUtils.getVEEName();
     * //vs.setId(vee.getVEEName()+"-"+ip.replace(".", "-")); //vee.setVEEName (vee.getVEEName()+"-"+ip.replace(".",
     * "-")); ProductSectionType productsection = new ProductSectionType(); MsgType category = new MsgType ();
     * category.setMsgid("org.fourcaast.instancecomponent"); category.setValue("Instance Component Metadata");
     * productsection.getCategoriesAndProperties().add(category); ProductSectionType.Property property = new
     * ProductSectionType.Property (); property.setKey("org.fourcaast.instancecomponent.type");
     * property.setValue("REC"); productsection.getCategoriesAndProperties().add(property);
     */

    // vapp.getRecipes()

    /*
     * if (vee.getRecipes()!= null && vee.getRecipes().size() >0) { for (String recipe: vee.getRecipes()){ property =
     * new ProductSectionType.Property (); property.setKey("org.fourcaast.instancecomponent.recipe");
     * property.setValue(recipe); productsection.getCategoriesAndProperties().add(property); } }
     */

    /*
     * category = new MsgType (); category.setMsgid("org.fourcaast.rec"); category.setValue("REC Attributes");
     * productsection.getCategoriesAndProperties().add(category); property = new ProductSectionType.Property ();
     * property.setKey("org.fourcaast.rec.address"); property.setValue(ip);
     * productsection.getCategoriesAndProperties().add(property); property = new ProductSectionType.Property ();
     * property.setKey("org.fourcaast.rec.username"); if (login != null) property.setValue(login);
     * productsection.getCategoriesAndProperties().add(property); property = new ProductSectionType.Property ();
     * property.setKey("org.fourcaast.rec.password"); if (password != null) property.setValue(password);
     * productsection.getCategoriesAndProperties().add(property); try { OVFEnvelopeUtils.addSection(vs, productsection);
     * } catch (SectionAlreadyPresentException e) { String msg = "Esta seccion ya existe. Desc: " + e.getMessage();
     * throw new ProductInstallatorException(msg); } catch (InvalidSectionException e) { String msg =
     * "Esta seccion NO es v�lida. Desc: " + e.getMessage(); throw new ProductInstallatorException(msg); } OVFSerializer
     * ovfSerializer = OVFSerializer.getInstance(); //String vsstring = ovfSerializer.toString(); //String vsstring =
     * ovfSerializer.writeXML(vs); OutputStream os = new OutputStream(); //ovfSerializer.writeXML(vs, os);
     * ovfSerializer.writeXML(virtualSystem, os) ByteArrayOutputStream byte1=new ByteArrayOutputStream();
     * os.write(byte1.toByteArray()); String s=byte1.toString(); return getDocument(s); }
     */

    /*
     * private Document getDocumentFromVapp(String vapp, String ip, String login, String password) throws
     * ProductInstallatorException { Document doc = getDocument(vapp); //setIp, setLogin, setPassword return doc; }
     */

    /**
     * @param vappUtils
     *            the vappUtils to set
     */
    public void setVappUtils(VappUtils vappUtils) {
        this.vappUtils = vappUtils;
    }

}
