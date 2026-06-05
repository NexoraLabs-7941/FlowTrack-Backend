package com.nexoralabs.flowtrack.userpermission.application.internal.queryservices;

import com.nexoralabs.flowtrack.userpermission.domain.model.aggregates.Permission;
import com.nexoralabs.flowtrack.userpermission.domain.model.queries.GetAllPermissionQuery;
import com.nexoralabs.flowtrack.userpermission.domain.model.queries.GetPermissionByIdQuery;
import com.nexoralabs.flowtrack.userpermission.domain.model.queries.GetPermissionByNameQuery;
import com.nexoralabs.flowtrack.userpermission.domain.services.PermissionQueryService;
import com.nexoralabs.flowtrack.userpermission.infrastructure.persistence.jpa.repositories.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionQueryServiceUser implements PermissionQueryService {

    private final PermissionRepository permissionRepository;

    public PermissionQueryServiceUser(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> handle(GetAllPermissionQuery query) {
        return permissionRepository.findAll();
    }

    @Override
    public Optional<Permission> handle(GetPermissionByIdQuery query) {
        return permissionRepository.findById(query.id());
    }

    @Override
    public Optional<Permission> handle(GetPermissionByNameQuery query) {
        return permissionRepository.findByName(query.name());
    }
}