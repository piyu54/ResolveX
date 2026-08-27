package com.resolvex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.resolvex.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

}