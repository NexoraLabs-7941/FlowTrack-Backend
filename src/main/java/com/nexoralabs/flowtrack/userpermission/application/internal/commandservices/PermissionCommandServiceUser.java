package com.nexoralabs.flowtrack.userpermission.application.internal.commandservices;

import com.nexoralabs.flowtrack.userpermission.domain.model.aggregates.Permission;
import com.nexoralabs.flowtrack.userpermission.domain.model.commands.CreatePermissionCommand;
import com.nexoralabs.flowtrack.userpermission.domain.services.PermissionCommandService;
import com.nexoralabs.flowtrack.userpermission.infrastructure.persistence.jpa.repositories.PermissionRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionCommandServiceUser implements PermissionCommandService {

    private final PermissionRepository permissionRepository;

    public PermissionCommandServiceUser(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Long handle(CreatePermissionCommand command) {
        // Validación: Verificar que no exista un permiso con el mismo nombre
        if (permissionRepository.existsByName(command.name())) {
            throw new IllegalArgumentException("Permission with name " + command.name() + " already exists");
        }
        
        // Crear la entidad y guardarla
        var permission = new Permission(command);
        permissionRepository.save(permission);
        return permission.getId();
    }
}