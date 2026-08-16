package com.resolvex.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.resolvex.dto.request.RoleRequest;
import com.resolvex.dto.response.RoleResponse;
import com.resolvex.entity.Role;
import com.resolvex.exception.RoleNotFoundException;
import com.resolvex.mapper.RoleMapper;
import com.resolvex.repository.RoleRepository;
import com.resolvex.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository,
                           RoleMapper roleMapper) {
        this.roleRepository = roleRepository; 
        this.roleMapper = roleMapper;
    }

    // CREATE
    @Override
    public RoleResponse createRole(RoleRequest request) {

        Role role = roleMapper.toEntity(request);

        Role savedRole = roleRepository.save(role);

        return roleMapper.toResponse(savedRole);
    }

    // GET ALL
    @Override
    public List<RoleResponse> getAllRoles() {

        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    // GET BY ID
    @Override
    public RoleResponse getRoleById(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new RoleNotFoundException(
                                "Role not found with id: " + roleId));

        return roleMapper.toResponse(role);
    }

    // UPDATE
    @Override
    public RoleResponse updateRole(Long roleId, RoleRequest request) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new RoleNotFoundException(
                                "Role not found with id: " + roleId));

        role.setRoleName(request.getRoleName());

        Role updatedRole = roleRepository.save(role);

        return roleMapper.toResponse(updatedRole);
    }

    // DELETE
    @Override
    public void deleteRole(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new RoleNotFoundException(
                                "Role not found with id: " + roleId));

        roleRepository.delete(role);
    }
}