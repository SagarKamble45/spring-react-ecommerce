package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.exceptions.APIException;
import com.sagark.ecommerce.project.exceptions.ResourceNotFoundException;
import com.sagark.ecommerce.project.model.Category;
import com.sagark.ecommerce.project.model.CategoryStatus;
import com.sagark.ecommerce.project.paylod.*;
import com.sagark.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    //helper method for mapping
    private CategoryDTO maptoDTO(Category category) {
        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        categoryDTO.setProductCount(
                category.getProducts() == null ? 0 : category.getProducts().size()
        );
        return categoryDTO;
    }


    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy,
                                             String sortOrder, String keyword, String status) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // "%" matches everything when there is no keyword
        String pattern = (keyword == null || keyword.isBlank())
                ? "%"
                : "%" + keyword.trim().toLowerCase() + "%";

        Page<Category> categoryPage;

        if (status == null || status.isBlank()) {
            categoryPage = categoryRepository.search(pattern, pageDetails);
        } else {
            CategoryStatus categoryStatus;
            try {
                categoryStatus = CategoryStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new APIException("Invalid status: " + status);
            }
            categoryPage = categoryRepository.searchByStatus(pattern, categoryStatus, pageDetails);
        }

        List<CategoryDTO> categoryDTOS = categoryPage.getContent()
                .stream().map(this::maptoDTO).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());

        if(existingCategory != null)
            throw new APIException("Category already exists: " + categoryDTO.getCategoryName());

        Category category = new Category();
        category.setCategoryName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getCategoryDescription());

        category.setCategoryStatus(categoryDTO.getCategoryStatus() != null ? categoryDTO.getCategoryStatus() : CategoryStatus.ACTIVE);

        category.setProducts(new ArrayList<>());
        Category savedCategory = categoryRepository.save(category);
        return maptoDTO(savedCategory);
    }


    @Override
    public CategoryDTO deleteCategory(long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId ));

        if (category.getProducts() != null
                && !category.getProducts().isEmpty()) {

            throw new APIException(
                    "Cannot delete a category that contains products."
            );
        }

        CategoryDTO deletedCategory = maptoDTO(category);
        categoryRepository.delete(category);

        return deletedCategory;
    }


    @Override
    public CategoryDTO updateCategory(UpdateCategoryDTO updateCategoryDTO, long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new APIException(
                        "Category not found with ID: " + categoryId
                ));

        // Update category name only if provided
        if (updateCategoryDTO.getCategoryName() != null
                && !updateCategoryDTO.getCategoryName().isBlank()) {

            Category duplicate = categoryRepository.findByCategoryName(
                    updateCategoryDTO.getCategoryName()
            );

            if (duplicate != null
                    && !duplicate.getCategoryId().equals(categoryId)) {
                throw new APIException(
                        "Category name already exists: "
                                + updateCategoryDTO.getCategoryName()
                );
            }

            category.setCategoryName(updateCategoryDTO.getCategoryName());
        }

        // Update description only if provided
        if (updateCategoryDTO.getCategoryDescription() != null) {
            category.setDescription(updateCategoryDTO.getCategoryDescription());
        }

        // Update status only if provided
        if (updateCategoryDTO.getCategoryStatus() != null) {
            category.setCategoryStatus(updateCategoryDTO.getCategoryStatus());
        }

        Category updatedCategory = categoryRepository.save(category);

        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryStatsDTO getCategoryStats() {

        CategoryStatsDTO categoryStatsDTO = new CategoryStatsDTO();

        categoryStatsDTO.setTotalCategories(categoryRepository.count());

        categoryStatsDTO.setActiveCategories(
                categoryRepository.countByCategoryStatus(CategoryStatus.ACTIVE)
        );

        categoryStatsDTO.setInActiveCategories(
                categoryRepository.countByCategoryStatus(CategoryStatus.INACTIVE)
        );

        // 1. Fetch the category name with the highest product count
        List<String> topCategoryNames =
                categoryRepository.findTopCategoryName(PageRequest.of(0, 1));

        // 2. If categories exist, fetch the name and product count
        if (!topCategoryNames.isEmpty()) {

            String highestCategoryName = topCategoryNames.get(0);

            long productCount =
                    categoryRepository.findProductsByCategoryName(highestCategoryName);

            // 3. Store both values in TopCategoryDTO
            TopCategoryDTO topCategoryDTO = new TopCategoryDTO();
            topCategoryDTO.setCategoryName(highestCategoryName);
            topCategoryDTO.setProductCount(productCount);

            // 4. Store the DTO in CategoryStatsDTO
            categoryStatsDTO.setTopCategoryDTO(topCategoryDTO);
        }

        return categoryStatsDTO;
    }

    @Override
    public List<CategoryDistributionDTO> getCategoryDistribution() {
        return categoryRepository.findCategoryDistribution();
    }


}
