package com.sagark.ecommerce.project.paylod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatsResponse {

    private Long totalCustomers;
    private Long activeCustomers;
    private Long inActiveCustomers;
    private Long totalOrders;
}
