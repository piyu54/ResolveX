package com.resolvex.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resolvex.dto.request.RoleRequest;
import com.resolvex.dto.response.RoleResponse;
import com.resolvex.service.RoleService;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // CREATE
    @PostMapping
    public RoleResponse createRole(@RequestBody RoleRequest request) {

        return roleService.createRole(request);
    }

    // GET ALL
    @GetMapping
    public List<RoleResponse> getAllRoles() {

        return roleService.getAllRoles();
    }

    // GET BY ID
    @GetMapping("/{roleId}")
    public RoleResponse getRoleById(@PathVariable Long roleId) {

        return roleService.getRoleById(roleId);
    }

    // UPDATE
    @PutMapping("/{roleId}")
    public RoleResponse updateRole(
            @PathVariable Long roleId,
            @RequestBody RoleRequest request) {

        return roleService.updateRole(roleId, request);
    }

    // DELETE
    @DeleteMapping("/{roleId}")
    public void deleteRole(@PathVariable Long roleId) {

        roleService.deleteRole(roleId);
    }
}