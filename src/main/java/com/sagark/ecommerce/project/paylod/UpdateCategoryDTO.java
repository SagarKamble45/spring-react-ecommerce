
package com.sagark.ecommerce.project.paylod;

import com.sagark.ecommerce.project.model.CategoryStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCategoryDTO {

    @Size(min = 5, message = "Category name must contain at least 5 characters")
    private String categoryName;

    @Size(max = 250, message = "Description cannot exceed 250 characters")
    private String categoryDescription;

    private CategoryStatus categoryStatus;
}
