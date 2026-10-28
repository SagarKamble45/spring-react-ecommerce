package com.sagark.ecommerce.project.paylod;

import lombok.Data;


@Data
public class CategoryStatsDTO {
    private long totalCategories;
    private long activeCategories;
    private long inActiveCategories;
    private TopCategoryDTO topCategoryDTO;
}
