package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.paylod.*;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String status);


    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(long categoryId);

    CategoryDTO updateCategory(UpdateCategoryDTO updateCategoryDTO, long categoryId);

    CategoryStatsDTO getCategoryStats();

    List<CategoryDistributionDTO> getCategoryDistribution();

}