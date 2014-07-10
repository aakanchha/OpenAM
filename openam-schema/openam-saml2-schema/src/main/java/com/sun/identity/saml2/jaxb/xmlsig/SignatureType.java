//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v1.0.6-b27-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.11 at 10:34:07 AM PDT 
//


package com.sun.identity.saml2.jaxb.xmlsig;


/**
 * Java content class for SignatureType complex type.
 * <p>The following schema fragment specifies the expected content contained within this java content object. (defined at file:/Users/allan/A-SVN/trunk/opensso/products/federation/library/xsd/saml2/xmldsig-core-schema.xsd line 46)
 * <p>
 * <pre>
 * &lt;complexType name="SignatureType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}SignedInfo"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}SignatureValue"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}KeyInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Object" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
public interface SignatureType {


    /**
     * Gets the value of the keyInfo property.
     * 
     * @return
     *     possible object is
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.KeyInfoElement}
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.KeyInfoType}
     */
    com.sun.identity.saml2.jaxb.xmlsig.KeyInfoType getKeyInfo();

    /**
     * Sets the value of the keyInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.KeyInfoElement}
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.KeyInfoType}
     */
    void setKeyInfo(com.sun.identity.saml2.jaxb.xmlsig.KeyInfoType value);

    /**
     * Gets the value of the signatureValue property.
     * 
     * @return
     *     possible object is
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.SignatureValueType}
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.SignatureValueElement}
     */
    com.sun.identity.saml2.jaxb.xmlsig.SignatureValueType getSignatureValue();

    /**
     * Sets the value of the signatureValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.SignatureValueType}
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.SignatureValueElement}
     */
    void setSignatureValue(com.sun.identity.saml2.jaxb.xmlsig.SignatureValueType value);

    /**
     * Gets the value of the signedInfo property.
     * 
     * @return
     *     possible object is
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.SignedInfoType}
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.SignedInfoElement}
     */
    com.sun.identity.saml2.jaxb.xmlsig.SignedInfoType getSignedInfo();

    /**
     * Sets the value of the signedInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.SignedInfoType}
     *     {@link com.sun.identity.saml2.jaxb.xmlsig.SignedInfoElement}
     */
    void setSignedInfo(com.sun.identity.saml2.jaxb.xmlsig.SignedInfoType value);

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String}
     */
    java.lang.String getId();

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    void setId(java.lang.String value);

    /**
     * Gets the value of the Object property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the Object property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link com.sun.identity.saml2.jaxb.xmlsig.ObjectElement}
     * {@link com.sun.identity.saml2.jaxb.xmlsig.ObjectType}
     * 
     */
    java.util.List getObject();

}
