package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.model.Category;
import com.sagark.ecommerce.project.paylod.CategoryDTO;
import com.sagark.ecommerce.project.paylod.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);



    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId);
}
