package com.sagark.ecommerce.project.paylod;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderStatsDTO {
private String totalOrders;
private String TotalRevenue;

private String totalDelivered;
private String totalProcessed;
private String pendingOrders;
private String shippedOrders;
private String cancelledOrders;
}