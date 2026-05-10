package com.nexoralabs.flowtrack.iam.domain.services;

import com.nexoralabs.flowtrack.iam.domain.model.commands.SeedRolesCommand;

/**
 * Service interface for role command operations
 */
public interface RoleCommandService {

    /**
     * Handle seed roles command
     * @param command The seed roles command
     */
    void handle(SeedRolesCommand command);
}

