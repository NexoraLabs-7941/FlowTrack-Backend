package com.nexoralabs.flowtrack.iam.domain.model.queries;

import com.nexoralabs.flowtrack.iam.domain.model.valueobjects.Roles;

/**
 * Query to get a role by name
 * @param name The role name
 */
public record GetRoleByNameQuery(Roles name) {
}

