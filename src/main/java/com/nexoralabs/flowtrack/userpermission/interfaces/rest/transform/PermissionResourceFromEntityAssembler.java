package com.nexoralabs.flowtrack.userpermission.interfaces.rest.transform;

import com.nexoralabs.flowtrack.userpermission.domain.model.aggregates.Permission;
import com.nexoralabs.flowtrack.userpermission.interfaces.rest.resources.PermissionResource;

public class PermissionResourceFromEntityAssembler {

    public static PermissionResource toResourceFromEntity(Permission entity) {
        return new PermissionResource(entity.getId(), entity.getName(), entity.getDescription());
    }
}