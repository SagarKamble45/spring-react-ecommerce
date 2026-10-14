package com.sagark.ecommerce.project.paylod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsResponse {
    private String productCount;
    private String totalRevenue;
    private String totalOrders;
    private String totalCustomers;
}
