package com.resolvex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.resolvex.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	
	Optional<Category> findByCategoryNameIgnoreCase(String categoryName);
}