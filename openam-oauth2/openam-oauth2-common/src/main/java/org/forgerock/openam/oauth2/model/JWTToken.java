/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 ForgeRock US Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information:
 *
 * "Portions copyright [year] [name of copyright owner]".
 *
 */

package org.forgerock.openam.oauth2.model;

import com.sun.identity.shared.OAuth2Constants;
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.openam.forgerockrest.authn.JWTBuilder;
import org.forgerock.openam.forgerockrest.authn.JWTBuilderFactory;

import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class JWTToken extends CoreToken {
    private JWTBuilderFactory factory = JWTBuilderFactory.getInstance();
    public JWTBuilder builder = factory.getJWTBuilder();
    private static ResourceBundle rb = ResourceBundle.getBundle("OAuth2CoreToken");

    /**
     *  Constructs a JWT token
     * @param iss issuer of the response
     * @param sub The subject of the jwt
     * @param aud The audience this JWT is meant for
     * @param azp The OAuth 2 client allowed to use this JWT as an access token
     * @param exp The time the JWT expires in seconds since epoc
     * @param iat The time the JWT was issued at in seconds since epoc.
     * @param ath The time the authorization for the JWT was made.
     * @param realm The realm the JWT belongs too.
     */
    public JWTToken(String iss, String sub, String aud, String azp, long exp, long iat, long ath, String realm){
        setIssuer(iss);
        setSubject(sub);
        setAudience(aud);
        setAuthorizeParty(azp);
        setExpirationTime(exp);
        setIssueTime(iat);
        setAuthTime(ath);
        setRealm(realm);
        builder.addValuePairs(super.asMap());
    }

    public JWTBuilder sign(PrivateKey pk) throws SignatureException{
        return builder.sign(pk);
    }

    public String build(){
        return builder.build();
    }

    /**
     * Creates an Bearer Access Token
     *
     * @param id
     *            Id of the access Token
     * @param value
     *            A JsonValue map to populate this token with.
     */
    public JWTToken(String id, JsonValue value) {
        super(id, value);
    }

    @Override
    /**
     * @{inheritDoc}
     */
    public Map<String, Object> convertToMap(){
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        return tokenMap;
    }

    @Override
    /**
     * @{inheritDoc}
     */
    public Map<String, Object> getTokenInfo() {
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        return tokenMap;
    }

    /**
     *  Sets the issuer of the response
     * @param issuer user identifier of the issuer
     */
    private void setIssuer(String issuer){
        super.put("iss", issuer);
    }

    /**
     *  Sets the subject of the JWT enduser
     * @param subject identifier of the end user
     */
    private void setSubject(String subject){
        super.put("sub", subject);
    }

    /**
     * Sets the audience this JWT is intended for. Must contain the OAuth 2 client_id
     * @param audience The identifier of the audience
     */
    private void setAudience(String audience){
        super.put("aud", audience);

    }

    /**
     * Sets the authorizing party of this JWT. The client_id that can use this token as an access_token
     * @param party identifier of the client_id
     */
    private void setAuthorizeParty(String party){
        super.put("azp", party);
    }

    /**
     * Sets the expiration time of the token in seconds
     * @param time time the token expires in seconds since epoc
     */
    private void setExpirationTime(long time){
        super.put("exp", time);
    }

    /**
     * The time in seconds the JWT was issued
     * @param time Seconds since epoc
     */
    private void setIssueTime(long time){
        super.put("iat", time);
    }

    /**
     * The time in seconds the enduser authenticated for the token
     * @param time Seconds since epoc
     */
    private void setAuthTime(long time){
        super.put("ath", time);
    }
}