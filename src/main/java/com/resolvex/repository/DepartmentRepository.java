package com.resolvex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resolvex.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}