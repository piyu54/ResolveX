package com.resolvex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resolvex.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleNameIgnoreCase(String roleName);
}