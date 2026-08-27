package com.resolvex.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.resolvex.dto.request.DepartmentRequest;
import com.resolvex.dto.response.DepartmentResponse;
import com.resolvex.service.DepartmentService;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // Create Department
    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @RequestBody DepartmentRequest request) {

        DepartmentResponse response =
                departmentService.createDepartment(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get Department by ID
    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(
            @PathVariable Long departmentId) {

        DepartmentResponse response =
                departmentService.getDepartmentById(departmentId);

        return ResponseEntity.ok(response);
    }

    // Get All Departments
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {

        List<DepartmentResponse> response =
                departmentService.getAllDepartments();

        return ResponseEntity.ok(response);
    }

    // Update Department
    @PutMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long departmentId,
            @RequestBody DepartmentRequest request) {

        DepartmentResponse response =
                departmentService.updateDepartment(
                        departmentId, request);

        return ResponseEntity.ok(response);
    }

    // Delete Department
    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable Long departmentId) {

        departmentService.deleteDepartment(departmentId);

        return ResponseEntity.noContent().build();
    }
}