package com.sagark.ecommerce.project.paylod;


import com.sagark.ecommerce.project.model.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO{
    private Long categoryId;

    @NotBlank(message = "Category Name is required")
    @Size(min=5, message = "Category name must contain at least 5 characters")
    private String categoryName;

    @Size(max=250, message = "Description cannot exceed 250 characters")
    private String categoryDescription;

    private CategoryStatus categoryStatus;

    private Integer productCount;

}
