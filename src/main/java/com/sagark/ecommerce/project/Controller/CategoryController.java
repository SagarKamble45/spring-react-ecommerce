package com.sagark.ecommerce.project.Controller;


import com.sagark.ecommerce.project.config.AppConstants;
import com.sagark.ecommerce.project.model.Category;
import com.sagark.ecommerce.project.paylod.*;
import com.sagark.ecommerce.project.repositories.CategoryRepository;
import com.sagark.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    CategoryService categoryService;



    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
    }


    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse > getAllCategories(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "status", required = false) String status)
    {
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder,keyword,status);
        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }


    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO,HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{CategoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory( @PathVariable long CategoryId) {
            CategoryDTO deletedCategory = categoryService.deleteCategory(CategoryId);
            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody UpdateCategoryDTO updateCategoryDTO, @PathVariable long categoryId){

        CategoryDTO updatedCategory = categoryService.updateCategory(updateCategoryDTO, categoryId);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);

    }

    @GetMapping("/admin/categoryStats")
    public ResponseEntity<CategoryStatsDTO> fetchCategoryStats(){
        CategoryStatsDTO categoryStatsDTO = categoryService.getCategoryStats();
        return new ResponseEntity<>(categoryStatsDTO,HttpStatus.OK);
    }


    @GetMapping("admin/categoryDistribution")
    public ResponseEntity<List<CategoryDistributionDTO>> fetchCategoryDistribution() {
        return new ResponseEntity<>(categoryService.getCategoryDistribution(), HttpStatus.OK);
    }
}
