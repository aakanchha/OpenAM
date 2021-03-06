/*
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
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright 2013-2015 ForgeRock AS. All rights reserved.
 */

package org.forgerock.openam.sts.token;


import org.forgerock.json.resource.ResourceException;
import org.forgerock.openam.sts.TokenCreationException;

import javax.inject.Inject;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * @see org.forgerock.openam.sts.token.ThreadLocalAMTokenCache
 * Note that AMSessionCache and AMSessionCacheEntry state is created and read in the class methods which implement
 * the ThreadLocalAMTokenCache interface. Note that these methods are not synchronized, as:
 * 1. the reading and writing of state does not happen concurrently - first the AM sessions are cached by the TokenValidators,
 * and then this state is referenced by the TokenProviders
 * 2. this state is thread-local, which means that memory visibility issues do not apply across threads, so no memory
 * barriers are necessary.
 */
public class ThreadLocalAMTokenCacheImpl implements ThreadLocalAMTokenCache {
    private static class AMSessionCache {
        private static class AMSessionCacheEntry {
            final String sessionId;
            final boolean invalidateAfterTokenCreation;
            private AMSessionCacheEntry(String sessionId, boolean invalidateAfterTokenCreation) {
                this.sessionId = sessionId;
                this.invalidateAfterTokenCreation = invalidateAfterTokenCreation;
            }
        }

        AMSessionCacheEntry delegatedSessionEntry;
        AMSessionCacheEntry sessionEntry;

        /**
         * @param sessionId the sessionId to cache
         * @param invalidateAfterTokenCreation whether the session should be invalidated after token creation
         * @throws IllegalStateException if there was a previous cached session - an illegal state. The exception is caught
         * by the methods defined in ThreadLocalAMTokenCache, and serves as a sanity check to insure that things are
         * indeed working as designed.
         */
        void setSessionEntry(String sessionId, boolean invalidateAfterTokenCreation) throws IllegalStateException {
            IllegalStateException illegalStateException = null;
            if (sessionEntry != null) {
                illegalStateException =  new IllegalStateException("In the ThreadLocalAMTokenCache, a session entry is " +
                        "being set over an existing session entry. Illegal state!");
            }
            sessionEntry = new AMSessionCacheEntry(sessionId, invalidateAfterTokenCreation);
            if (illegalStateException != null) {
                throw illegalStateException;
            }
        }

        AMSessionCacheEntry getSessionEntry() {
            return sessionEntry;
        }

        /**
         * @param delegatedSessionId the sessionId to cache
         * @param invalidateAfterTokenCreation whether the session should be invalidated after token creation
         * @throws IllegalStateException if there was a previous cached session - an illegal state. The exception is caught
         * by the methods defined in ThreadLocalAMTokenCache, and serves as a sanity check to insure that things are
         * indeed working as designed.
         */
        void setDelegatedSessionEntry(String delegatedSessionId, boolean invalidateAfterTokenCreation) throws IllegalStateException {
            IllegalStateException illegalStateException = null;
            if (delegatedSessionEntry != null) {
                illegalStateException = new IllegalStateException("In the ThreadLocalAMTokenCache, a session entry is " +
                        "being set over an existing delegated session entry. Illegal state!");
            }
            delegatedSessionEntry = new AMSessionCacheEntry(delegatedSessionId, invalidateAfterTokenCreation);
            if (illegalStateException != null) {
                throw illegalStateException;
            }
        }

        AMSessionCacheEntry getDelegatedSessionEntry() {
            return delegatedSessionEntry;
        }
    }

    private static final ThreadLocal<AMSessionCache> sessionCacheHolder = new ThreadLocal<AMSessionCache>() {
        /*
        Called the first time get is called, including after any remove call. I will get before I set any value, so this
        method will insure that the AMSessionCache() reference is non-null prior to any setSessionEntry or
        setDelegatedSessionEntry call.
         */
        @Override
        protected AMSessionCache initialValue() {
            return new AMSessionCache();
        }
    };

    private final Logger logger;

    @Inject
    ThreadLocalAMTokenCacheImpl(Logger logger) {
        this.logger = logger;
    }


    @Override
    public void cacheAMSessionId(String sessionId, boolean invalidateAfterTokenCreation) {
        try {
            sessionCacheHolder.get().setSessionEntry(sessionId, invalidateAfterTokenCreation);
        } catch (IllegalStateException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getAMSessionId() throws TokenCreationException {
        String sessionId = sessionCacheHolder.get().getSessionEntry().sessionId;
        if (sessionId == null) {
            throw new TokenCreationException(ResourceException.INTERNAL_ERROR,
                    "No sessionId cached in ThreadLocal. Illegal State!");
        }
        return sessionId;
    }

    @Override
    public void cacheDelegatedAMSessionId(String sessionId, boolean invalidateAfterTokenCreation) {
        try {
            sessionCacheHolder.get().setDelegatedSessionEntry(sessionId, invalidateAfterTokenCreation);
        } catch (IllegalStateException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String getDelegatedAMSessionId() throws TokenCreationException {
        String sessionId = sessionCacheHolder.get().getDelegatedSessionEntry().sessionId;
        if (sessionId == null) {
            throw new TokenCreationException(ResourceException.INTERNAL_ERROR,
                    "No delegated sessionId cached in ThreadLocal. Illegal State!");
        }
        return sessionId;
    }

    @Override
    public Set<String> getToBeInvalidatedAMSessionIds() {
        Set<String> ids = new HashSet<String>(2);
        AMSessionCache sessionCache = sessionCacheHolder.get();
        if ((sessionCache.getSessionEntry() != null) && sessionCache.getSessionEntry().invalidateAfterTokenCreation) {
            ids.add(sessionCache.getSessionEntry().sessionId);
        }
        if ((sessionCache.getDelegatedSessionEntry() != null) && sessionCache.getDelegatedSessionEntry().invalidateAfterTokenCreation) {
            ids.add(sessionCache.getDelegatedSessionEntry().sessionId);
        }
        return ids;
    }

    @Override
    public void clearCachedSessions() {
        sessionCacheHolder.remove();
    }
}
