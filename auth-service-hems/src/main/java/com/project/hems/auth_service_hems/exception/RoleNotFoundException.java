package com.project.hems.auth_service_hems.exception;

public class RoleNotFoundException extends RuntimeException {

    private final String roleName;

    public RoleNotFoundException(String roleName) {
        super("Role not found: " + roleName);
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}