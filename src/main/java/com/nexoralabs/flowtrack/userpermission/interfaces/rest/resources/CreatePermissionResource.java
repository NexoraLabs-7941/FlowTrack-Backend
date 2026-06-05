package com.nexoralabs.flowtrack.userpermission.interfaces.rest.resources;

// Este record es el DTO que se recibe en la petición POST.
// Sus campos deben coincidir con el JSON esperado.
public record CreatePermissionResource(
    String name,
    String description
) {
}