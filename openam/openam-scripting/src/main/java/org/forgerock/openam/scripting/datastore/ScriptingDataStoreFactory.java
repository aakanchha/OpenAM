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
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */
package org.forgerock.openam.scripting.datastore;

import javax.security.auth.Subject;

/**
 * A factory for providing new scripting data store instances.
 *
 * @param <T> The type of object managed by the data store.
 */
public interface ScriptingDataStoreFactory<T> {

    /**
     * Creates a new scripting data store instance based on the calling subject for the passed realm.
     * @param subject the calling subject
     * @param realm the realm
     * @return a scripting data store instance
     */
    ScriptingDataStore<T> create(Subject subject, String realm);

}
