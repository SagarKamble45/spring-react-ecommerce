package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.paylod.*;
import jakarta.transaction.Transactional;

import java.util.List;


public interface OrderService {
    @Transactional
    OrderDTO placeOrder(
            String emailId,
            String username,
            Long addressId,
            String paymentMethod,
            String pgName,
            String pgPaymentId,
            String pgStatus,
            String pgResponseMessage
    );
    OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    List<SalesSummaryDTO> getSalesSummary();

    OrderDTO updateOrder(Long orderId, String status);

    OrderStatsDTO getOrderStats();

    List<RecentOrderDTO> getLatestFiveOrders();
}
