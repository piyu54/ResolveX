package com.resolvex.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.resolvex.dto.request.CategoryRequest;
import com.resolvex.dto.response.CategoryResponse;
import com.resolvex.entity.Category;
import com.resolvex.entity.Department;
import com.resolvex.mapper.CategoryMapper;
import com.resolvex.repository.CategoryRepository;
import com.resolvex.repository.DepartmentRepository;
import com.resolvex.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            DepartmentRepository departmentRepository,
            CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.departmentRepository = departmentRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Department department = findDepartment(request.getDepartmentId());

        Category category = categoryMapper.toEntity(request, department);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = findCategory(categoryId);
        return categoryMapper.toResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse updateCategory(
            Long categoryId,
            CategoryRequest request) {

        Category category = findCategory(categoryId);
        Department department = findDepartment(request.getDepartmentId());

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setDepartment(department);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = findCategory(categoryId);
        categoryRepository.delete(category);
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found with ID: " + categoryId));
    }

    private Department findDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Department not found with ID: " + departmentId));
    }
    
}