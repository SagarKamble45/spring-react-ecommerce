package com.sagark.ecommerce.project.paylod;

import lombok.Data;

@Data
public class ProductStatsDTO {
    private String totalProducts;
    private String totalActiveProducts;
    private String totalOutOfStockProducts;
    private String totalCategories;
}
