package com.nexoralabs.flowtrack.iam.interfaces.rest.transform;

import com.nexoralabs.flowtrack.iam.domain.model.entities.Role;
import com.nexoralabs.flowtrack.iam.interfaces.rest.resources.RoleResource;

/**
 * Assembler to convert a Role entity to a RoleResource.
 */
public class RoleResourceFromEntityAssembler {
    /**
     * Converts a Role entity to a RoleResource.
     * @param role The {@link Role} entity to convert.
     * @return The {@link RoleResource} resource.
     */
    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(
                role.getId(),
                role.getName().name()
        );
    }
}

