/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [2012] [ForgeRock Inc]"
 */

package org.forgerock.restlet.ext.oauth2.flow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.identity.shared.OAuth2Constants;
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.openam.oauth2.model.CoreToken;
import org.forgerock.openam.oauth2.model.SessionClient;
import org.forgerock.openam.oauth2.model.SessionClientImpl;
import org.forgerock.openam.oauth2.utils.OAuth2Utils;
import org.forgerock.openam.oauth2.exceptions.OAuthProblemException;
import org.forgerock.openam.oauth2.model.AuthorizationCode;
import org.forgerock.restlet.ext.oauth2.provider.OAuth2Client;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.routing.Redirector;

/**
 * Implements the Authorization Code Flow
 *
 * @see <a href="http://tools.ietf.org/html/rfc6749#section-4.1">4.1.  Authorization Code Grant</a>
 */
public class AuthorizationCodeServerResource extends AbstractFlow {

    protected boolean decisionIsAllow = false;
    protected Form formPost = null;

    @Post("form:json")
    public Representation represent(Representation entity) {
        // Validate the client
        client = validateRemoteClient();
        // Validate Redirect URI throw exception
        sessionClient =
                client.getClientInstance(OAuth2Utils.getRequestParameter(getRequest(),
                        OAuth2Constants.Params.REDIRECT_URI, String.class));
        return token(entity);
    }

    public Representation token(Representation entity) {
        /*
         * The authorization server MUST:
         * 
         * o require client authentication for confidential clients or for any
         * client that was issued client credentials (or with other
         * authentication requirements), o authenticate the client if client
         * authentication is included and ensure the authorization code was
         * issued to the authenticated client, o verify that the authorization
         * code is valid, and o ensure that the "redirect_uri" parameter is
         * present if the "redirect_uri" parameter was included in the initial
         * authorization request as described in Section 4.1.1, and if included
         * ensure their values are identical.
         */

        // Find code
        String code_p =
                OAuth2Utils.getRequestParameter(getRequest(), OAuth2Constants.Params.CODE, String.class);
        CoreToken code = getTokenStore().readAuthorizationCode(code_p);

        if (null == code) {
            OAuth2Utils.DEBUG.error("AuthorizationCodeServerResource::Authorization code doesn't exist.");
            throw OAuthProblemException.OAuthError.INVALID_REQUEST.handle(getRequest(),
                    "Authorization code doesn't exist.");
        } else if (code.isIssued()) {
            invalidateTokens(code_p);
            getTokenStore().deleteAuthorizationCode(code_p);
            OAuth2Utils.DEBUG.error("AuthorizationCodeServerResource::Authorization code has been used");
            throw OAuthProblemException.OAuthError.INVALID_REQUEST.handle(getRequest(),
                    "Authorization code has been used.");
        } else {
            if (code.isExpired()) {
                OAuth2Utils.DEBUG.error("AuthorizationCodeServerResource::Authorization code expired.");
                throw OAuthProblemException.OAuthError.INVALID_CODE.handle(getRequest(),
                        "Authorization code expired.");
            }

            //get code sessionClient
            String clientID = code.getClientID();
            String redirectURI = code.getRedirectURI();
            SessionClient sc = new SessionClientImpl(clientID, redirectURI);
            if (!sc.equals(sessionClient)) {
                // Throw redirect_uri mismatch
                //throw OAuthProblemException.OAuthError.REDIRECT_URI_MISMATCH.handle(getRequest());
            }

            // Generate Token
            CoreToken token = createAccessToken(code);

            //set access token issued
            // TODO code.put(OAuth2Constants.CoreTokenParams.ISSUED, "true");
            //getTokenStore().updateAuthorizationCode(code_p, code);
            Map<String, Object> response = token.convertToMap();

            //execute post token creation pre return scope plugin for extra return data.
            Set<String> data = new HashSet<String>();
            response.putAll(executeExtraDataScopePlugin(data ,token));

            return new JacksonRepresentation<Map>(response);
        }
    }

    // Get the decision [allow,deny]
    protected boolean getDecision(Representation entity) {
        if (!decisionIsAllow) {
            String decision =
                    OAuth2Utils.getRequestParameter(getRequest(), OAuth2Constants.Custom.DECISION,
                            String.class);
            if (OAuth2Constants.Custom.ALLOW.equalsIgnoreCase(decision)) {
                decisionIsAllow = true;
            } else {
                OAuth2Utils.DEBUG.error("AuthorizationCodeServerResource::Resource Owner did not authorize the request");
                throw OAuthProblemException.OAuthError.ACCESS_DENIED.handle(getRequest(),
                        "Resource Owner did not authorize the request");
            }
        }
        return decisionIsAllow;
    }

    @Override
    protected String[] getRequiredParameters() {
        Set<String> required = null;
        switch (endpointType) {
        case AUTHORIZATION_ENDPOINT: {
            return new String[] { OAuth2Constants.Params.RESPONSE_TYPE, OAuth2Constants.Params.CLIENT_ID };
        }
        case TOKEN_ENDPOINT: {
            return new String[] { OAuth2Constants.Params.GRANT_TYPE, OAuth2Constants.Params.CODE,
                OAuth2Constants.Params.REDIRECT_URI };
        }
        default: {
            return null;
        }
        }
    }

    protected CoreToken createRefreshToken(CoreToken code){
        resourceOwner = getAuthenticatedResourceOwner();
        return getTokenStore().createRefreshToken(code.getScope(),
                                                    OAuth2Utils.getRealm(getRequest()),
                                                    code.getUserID(),
                                                    sessionClient.getClientId(),
                                                    sessionClient.getRedirectUri());
    }

    /**
     * This method is intended to be overridden by subclasses.
     * 
     * @param code
     * @return
     * @throws OAuthProblemException
     */
    protected CoreToken createAccessToken(CoreToken code) {
        if (checkIfRefreshTokenIsRequired(getRequest())){
            //create refresh token
            CoreToken token = createRefreshToken(code);


            return getTokenStore().createAccessToken(client.getClient().getAccessTokenType(),
                    code.getScope(), OAuth2Utils.getRealm(getRequest()), token.getUserID(),
                    token.getClientID(), token.getRedirectURI(), code.getTokenID(), token.getTokenID());
        } else {
            return getTokenStore().createAccessToken(client.getClient().getAccessTokenType(),
                    code.getScope(), OAuth2Utils.getRealm(getRequest()), code.getUserID(),
                    code.getClientID(), code.getRedirectURI(), code.getTokenID(), null);
        }
    }

    private void invalidateTokens(String id){

        JsonValue token = getTokenStore().queryForToken(id);

        Set<HashMap<String,String>> list = (Set<HashMap<String,String>>) token.getObject();

        if (list != null && !list.isEmpty() ){
            for (HashMap<String,String> entry : list){
                if (entry.get(OAuth2Constants.CoreTokenParams.ID) != null && !entry.get(OAuth2Constants.CoreTokenParams.ID).isEmpty()){
                    String entryID = entry.get(OAuth2Constants.CoreTokenParams.ID);
                    invalidateTokens(entry.get(OAuth2Constants.CoreTokenParams.ID));
                    String type = null;
                    if (entry.get(OAuth2Constants.CoreTokenParams.TOKEN_TYPE) != null){
                        type = entry.get(OAuth2Constants.CoreTokenParams.TOKEN_TYPE);
                    }
                    deleteToken(type, entryID);
                }
            }
        }
    }

    private void deleteToken(String type, String id){
        if (type.equalsIgnoreCase(OAuth2Constants.Token.OAUTH_ACCESS_TOKEN)){
            getTokenStore().deleteAccessToken(id);
        } else if (type.equalsIgnoreCase(OAuth2Constants.Token.OAUTH_REFRESH_TOKEN)){
            getTokenStore().deleteRefreshToken(id);
        } else if (type.equalsIgnoreCase(OAuth2Constants.Params.CODE)){
            getTokenStore().deleteAuthorizationCode(id);
        } else {
            //shouldnt ever happen
        }
    }
}