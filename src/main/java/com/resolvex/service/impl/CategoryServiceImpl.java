package com.resolvex.service.impl;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.resolvex.dto.request.CategoryRequest;
import com.resolvex.dto.response.CategoryResponse;
import com.resolvex.entity.Category;
import com.resolvex.entity.Department;
import com.resolvex.mapper.CategoryMapper;
import com.resolvex.repository.CategoryRepository;
import com.resolvex.repository.DepartmentRepository;
import com.resolvex.service.CategoryService;

@Service
@Transactional
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

        String name = validateRequest(request);
        requireAvailableName(name, null);

        Department department = findDepartment(request.getDepartmentId());

        Category category = categoryMapper.toEntity(request, department);
        category.setCategoryName(name);

        Category saved = categoryRepository.saveAndFlush(category);

        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {

        return categoryMapper.toResponse(findCategory(categoryId));
    }

    @Override
    @Transactional(readOnly = true)
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

        String name = validateRequest(request);
        Category category = findCategory(categoryId);

        requireAvailableName(name, category.getCategoryId());

        Department department = findDepartment(request.getDepartmentId());

        category.setCategoryName(name);
        category.setDescription(request.getDescription());
        category.setDepartment(department);

        Category saved = categoryRepository.saveAndFlush(category);

        return categoryMapper.toResponse(saved);
    }

    @Override
    public void deleteCategory(Long categoryId) {

        Category category = findCategory(categoryId);

        categoryRepository.delete(category);
        categoryRepository.flush();
    }

    private Category findCategory(Long categoryId) {

        if (categoryId == null || categoryId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category ID must be positive");
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"));
    }

    private Department findDepartment(Long departmentId) {

        if (departmentId == null || departmentId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Department ID must be positive");
        }

        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Department not found"));
    }

    private void requireAvailableName(
            String name,
            Long currentCategoryId) {

        categoryRepository.findByCategoryNameIgnoreCase(name)
                .ifPresent(existing -> {
                    if (!existing.getCategoryId()
                            .equals(currentCategoryId)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Category name already exists");
                    }
                });
    }

    private String validateRequest(CategoryRequest request) {

        if (request == null
                || request.getCategoryName() == null
                || request.getCategoryName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category name is required");
        }

        if (request.getCategoryName().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category name must not exceed 100 characters");
        }

        if (request.getDescription() != null
                && request.getDescription().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Description must not exceed 255 characters");
        }

        return request.getCategoryName().trim();
    }
}