package com.resolvex.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.resolvex.dto.request.RoleRequest;
import com.resolvex.dto.response.RoleResponse;
import com.resolvex.entity.Role;
import com.resolvex.mapper.RoleMapper;
import com.resolvex.repository.RoleRepository;
import com.resolvex.service.RoleService;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            RoleMapper roleMapper) {

        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public RoleResponse createRole(RoleRequest request) {

        String name = validateRequest(request);
        requireAvailableName(name, null);

        Role role = roleMapper.toEntity(request);
        role.setRoleName(name);

        Role saved = roleRepository.saveAndFlush(role);

        return roleMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long roleId) {

        return roleMapper.toResponse(findRole(roleId));
    }

    @Override
    public RoleResponse updateRole(
            Long roleId,
            RoleRequest request) {

        String name = validateRequest(request);
        Role role = findRole(roleId);

        requireAvailableName(name, role.getRoleId());

        role.setRoleName(name);

        Role saved = roleRepository.saveAndFlush(role);

        return roleMapper.toResponse(saved);
    }

    @Override
    public void deleteRole(Long roleId) {

        Role role = findRole(roleId);

        roleRepository.delete(role);
        roleRepository.flush();
    }

    private Role findRole(Long roleId) {

        if (roleId == null || roleId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role ID must be positive");
        }

        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found"));
    }

    private void requireAvailableName(
            String name,
            Long currentRoleId) {

        roleRepository.findByRoleNameIgnoreCase(name)
                .ifPresent(existing -> {
                    if (!existing.getRoleId().equals(currentRoleId)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Role name already exists");
                    }
                });
    }

    private String validateRequest(RoleRequest request) {

        if (request == null
                || request.getRoleName() == null
                || request.getRoleName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role name is required");
        }

        if (request.getRoleName().length() > 50) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role name must not exceed 50 characters");
        }

        return request.getRoleName().trim();
    }
}