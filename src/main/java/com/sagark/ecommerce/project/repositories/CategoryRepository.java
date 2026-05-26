package com.sagark.ecommerce.project.repositories;

import com.sagark.ecommerce.project.model.Category;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByCategoryName(String categoryName);
}
