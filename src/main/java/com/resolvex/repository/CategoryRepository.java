package com.resolvex.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resolvex.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}