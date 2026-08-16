package com.resolvex.dto.request;

public class RoleRequest {

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