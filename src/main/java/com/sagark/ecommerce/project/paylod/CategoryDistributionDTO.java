package com.sagark.ecommerce.project.paylod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDistributionDTO {
    private Long categoryId;
    private String categoryName;
    private Long productCount;
}
