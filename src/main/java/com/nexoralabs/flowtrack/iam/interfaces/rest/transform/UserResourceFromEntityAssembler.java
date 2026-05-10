package com.nexoralabs.flowtrack.iam.interfaces.rest.transform;

import com.nexoralabs.flowtrack.iam.domain.model.aggregates.User;
import com.nexoralabs.flowtrack.iam.interfaces.rest.resources.UserResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler to convert a User entity to a UserResource.
 */
public class UserResourceFromEntityAssembler {
    /**
     * Converts a User entity to a UserResource.
     * @param user The {@link User} entity to convert.
     * @return The {@link UserResource} resource.
     */
    public static UserResource toResourceFromEntity(User user) {
        List<String> permissions = user.getPermissions() != null 
                ? user.getPermissionsAsStringList()
                : new ArrayList<>();
        
        return new UserResource(
                user.getId(),
                user.getEmail(),
                user.getRolesAsStringList(),
                permissions
        );
    }
}

