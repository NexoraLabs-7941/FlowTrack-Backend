package com.nexoralabs.flowtrack.userpermission.interfaces.rest.transform;

import com.nexoralabs.flowtrack.userpermission.domain.model.commands.CreatePermissionCommand;
import com.nexoralabs.flowtrack.userpermission.interfaces.rest.resources.CreatePermissionResource;

public class CreatePermissionCommandFromResourceAssembler {
    
    public static CreatePermissionCommand toCommandFromResource(CreatePermissionResource resource) {
        return new CreatePermissionCommand(resource.name(), resource.description());
    }
}