package com.resolvex.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RoleRequest {

    @NotBlank(message = "Role name is required")
    @Size(
            max = 50,
            message = "Role name must not exceed 50 characters")
    private String roleName;

    public RoleRequest() {
    }

    public RoleRequest(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}