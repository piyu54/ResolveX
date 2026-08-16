package com.resolvex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import com.resolvex.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

}
