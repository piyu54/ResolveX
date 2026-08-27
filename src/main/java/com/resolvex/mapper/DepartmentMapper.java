package com.resolvex.mapper;

import org.springframework.stereotype.Component;

import com.resolvex.dto.request.DepartmentRequest;
import com.resolvex.dto.response.DepartmentResponse;
import com.resolvex.entity.Department;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentRequest request) {

        Department department = new Department();

        department.setDepartmentName(request.getDepartmentName());
        department.setDescription(request.getDescription());

        return department;
    }

    public DepartmentResponse toResponse(Department department) {

        DepartmentResponse response = new DepartmentResponse();

        response.setDepartmentId(department.getDepartmentId());
        response.setDepartmentName(department.getDepartmentName());
        response.setDescription(department.getDescription());

        return response;
    }
}