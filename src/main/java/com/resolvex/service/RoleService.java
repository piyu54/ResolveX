


package com.resolvex.service;

import java.util.List;

import com.resolvex.dto.request.RoleRequest;
import com.resolvex.dto.response.RoleResponse;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long roleId);

    RoleResponse updateRole(Long roleId, RoleRequest request);

    void deleteRole(Long roleId);
}