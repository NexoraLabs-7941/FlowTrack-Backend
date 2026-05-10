package com.nexoralabs.flowtrack.iam.application.internal.commandservices;

import com.nexoralabs.flowtrack.iam.domain.model.commands.SeedRolesCommand;
import com.nexoralabs.flowtrack.iam.domain.model.entities.Role;
import com.nexoralabs.flowtrack.iam.domain.model.valueobjects.Roles;
import com.nexoralabs.flowtrack.iam.domain.services.RoleCommandService;
import com.nexoralabs.flowtrack.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Implementation of RoleCommandService
 */
@Service
public class RoleCommandServiceImpl implements RoleCommandService {

    private final RoleRepository roleRepository;

    public RoleCommandServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values()).forEach(role -> {
            if (!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(role));
            }
        });
    }
}

