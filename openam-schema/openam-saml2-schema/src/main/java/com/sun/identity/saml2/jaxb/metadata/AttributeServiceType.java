//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.6-b27-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.11 at 10:34:07 AM PDT 
//


package com.sun.identity.saml2.jaxb.metadata;


/**
 * Java content class for AttributeServiceType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/Users/allan/A-SVN/trunk/opensso/products/federation/library/xsd/saml2/saml-schema-metadata-2.0.xsd line 372)
 * <p>
 * <pre>
 * &lt;complexType name="AttributeServiceType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:SAML:2.0:metadata}EndpointType">
 *       &lt;attribute ref="{urn:oasis:names:tc:SAML:metadata:X509:query}supportsX509Query"/>
 *       &lt;attribute ref="{urn:oasis:names:tc:SAML:metadata:X509:query}supportsX509SelfQuery"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface AttributeServiceType
    extends com.sun.identity.saml2.jaxb.metadata.EndpointType
{


    /**
     * Gets the value of the supportsX509SelfQuery property.
     * 
     */
    boolean isSupportsX509SelfQuery();

    /**
     * Sets the value of the supportsX509SelfQuery property.
     * 
     */
    void setSupportsX509SelfQuery(boolean value);

    /**
     * Gets the value of the supportsX509Query property.
     * 
     */
    boolean isSupportsX509Query();

    /**
     * Sets the value of the supportsX509Query property.
     * 
     */
    void setSupportsX509Query(boolean value);

}
