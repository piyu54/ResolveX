package com.resolvex.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.resolvex.dto.request.DepartmentRequest;
import com.resolvex.dto.response.DepartmentResponse;
import com.resolvex.entity.Department;
import com.resolvex.mapper.DepartmentMapper;
import com.resolvex.repository.DepartmentRepository;
import com.resolvex.service.DepartmentService;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(
            DepartmentRepository departmentRepository,
            DepartmentMapper departmentMapper) {

        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    // Create Department
    @Override
    public DepartmentResponse createDepartment(DepartmentRequest request) {

        Department department = departmentMapper.toEntity(request);

        Department savedDepartment =
                departmentRepository.save(department);

        return departmentMapper.toResponse(savedDepartment);
    }

    // Get Department by ID
    @Override
    public DepartmentResponse getDepartmentById(Long departmentId) {

        Department department = departmentRepository
                .findById(departmentId)
                .orElseThrow(() ->
                        new RuntimeException("Department not found"));

        return departmentMapper.toResponse(department);
    }

    // Get All Departments
    @Override
    public List<DepartmentResponse> getAllDepartments() {

        List<Department> departments =
                departmentRepository.findAll();

        return departments.stream()
                .map(departmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Update Department
    @Override
    public DepartmentResponse updateDepartment(
            Long departmentId,
            DepartmentRequest request) {

        Department department = departmentRepository
                .findById(departmentId)
                .orElseThrow(() ->
                        new RuntimeException("Department not found"));

        department.setDepartmentName(
                request.getDepartmentName());

        department.setDescription(
                request.getDescription());

        Department updatedDepartment =
                departmentRepository.save(department);

        return departmentMapper.toResponse(updatedDepartment);
    }

    // Delete Department
    @Override
    public void deleteDepartment(Long departmentId) {

        Department department = departmentRepository
                .findById(departmentId)
                .orElseThrow(() ->
                        new RuntimeException("Department not found"));

        departmentRepository.delete(department);
    }
}