package com.resolvex.mapper;

import org.springframework.stereotype.Component;

import com.resolvex.dto.request.RoleRequest;
import com.resolvex.dto.response.RoleResponse;
import com.resolvex.entity.Role;

@Component
public class RoleMapper {

    // RoleRequest → Role Entity
    public Role toEntity(RoleRequest request) {

        Role role = new Role();

        role.setRoleName(request.getRoleName());

        return role;
    }

    // Role Entity → RoleResponse
    public RoleResponse toResponse(Role role) {

        RoleResponse response = new RoleResponse();

        response.setRoleId(role.getRoleId());
        response.setRoleName(role.getRoleName());

        return response;
    }
}