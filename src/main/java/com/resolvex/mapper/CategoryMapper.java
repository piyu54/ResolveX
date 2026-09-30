package com.resolvex.mapper;

import org.springframework.stereotype.Component;

import com.resolvex.dto.request.CategoryRequest;
import com.resolvex.dto.response.CategoryResponse;
import com.resolvex.entity.Category;
import com.resolvex.entity.Department;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request, Department department) {
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setDepartment(department);
        return category;
    }

    public CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getCategoryName());
        response.setDescription(category.getDescription());

        Department department = category.getDepartment();
        if (department != null) {
            response.setDepartmentId(department.getDepartmentId());
            response.setDepartmentName(department.getDepartmentName());
        }

        return response;
    }
}