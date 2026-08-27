package com.resolvex.service;

import java.util.List;

import com.resolvex.dto.request.DepartmentRequest;
import com.resolvex.dto.response.DepartmentResponse;

public interface DepartmentService {

    DepartmentResponse createDepartment(DepartmentRequest request);

    DepartmentResponse getDepartmentById(Long departmentId);

    List<DepartmentResponse> getAllDepartments();

    DepartmentResponse updateDepartment(
            Long departmentId,
            DepartmentRequest request);

    void deleteDepartment(Long departmentId);
}