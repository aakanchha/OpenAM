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
package org.forgerock.openam.entitlement.configuration;

import com.sun.identity.entitlement.EntitlementException;
import org.forgerock.openam.entitlement.ResourceType;
import org.forgerock.util.query.QueryFilter;

import javax.security.auth.Subject;
import java.util.Map;
import java.util.Set;

/**
 * Implementations of this interface are responsible for the persistence of the resource type entitlement configuration.
 */
public interface ResourceTypeConfiguration {

    /**
     * Retrieve a registered resource type by id.
     * @param subject The subject for whom the resource type should be retrieved.
     * @param realm The realm in which the resource type resides.
     * @param uuid The unique identifier for the resource type.
     * @return The registered resource type.
     * @throws EntitlementException If the retrieval of the resource type failed.
     */
    public ResourceType getResourceType(Subject subject, String realm, String uuid) throws EntitlementException;

    /**
     * Check to see if a resource type with the given UUID already exists in this realm.
     * @param subject The subject for whom the resource type should be retrieved.
     * @param realm The realm in which the resource type resides.
     * @param uuid The unique identifier for the resource type.
     * @return True if the resource type already exists.
     */
    public boolean containsUUID(Subject subject, String realm, String uuid) throws EntitlementException;

    /**
     * Check to see if a resource type with the given name already exists in this realm.
     * @param subject The subject for whom the resource type should be retrieved.
     * @param realm The realm in which the resource type resides.
     * @param name The name of the resource type.
     * @return True if the resource type already exists.
     */
    public boolean containsName(Subject subject, String realm, String name) throws EntitlementException;

    /**
     * Remove a resource type.
     * @param subject The subject for whom the resource type should be removed.
     * @param realm The realm in which the resource type resides.
     * @param uuid The unique identifier for the resource type.
     * @throws EntitlementException if the resource type cannot be removed.
     */
    public void removeResourceType(Subject subject, String realm, String uuid) throws EntitlementException;

    /**
     * Stores the resource type to the data store.
     * @param subject The subject for whom the resource type should be stored.
     * @param resourceType The resource type to store.
     * @throws EntitlementException if the resource type cannot be stored.
     */
    public void storeResourceType(Subject subject, ResourceType resourceType) throws EntitlementException;

    /**
     * Retrieves a set of resource types based on the passed query filter.
     *
     * @param filter
     *         the query filter
     * @param subject
     *         the calling subject
     * @param realm
     *         the realm to look within
     *
     * @return a set of matching resource types
     *
     * @throws EntitlementException
     *         should an error occur during lookup
     */
    public Set<ResourceType> getResourceTypes(QueryFilter<SmsAttribute> filter,
                                              Subject subject, String realm) throws EntitlementException;

    /**
     * Retrieve the resource types stored under the specified realm from the data store.
     * @param subject The subject with privilege to access the resource types in this realm.
     * @param realm The realm from which to retrieve the resource types.
     * @return The registered resource types in a map. The outer map holds the resource type UUID as the key. The inner
     * map (value of the outer map) holds the names of the attributes of the resource type as keys and a set of values
     * for that attribute as the value.
     * @throws EntitlementException If the retrieval of the resource type failed.
     */
    public Map<String, Map<String, Set<String>>> getResourceTypesData(Subject subject, String realm)
            throws EntitlementException;

}
