package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.paylod.AnalyticsResponse;
import com.sagark.ecommerce.project.paylod.ProductResponse;
import com.sagark.ecommerce.project.repositories.OrderRepository;
import com.sagark.ecommerce.project.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticServiceImpl implements AnalyticService {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public AnalyticsResponse getAnalyticsData() {

        AnalyticsResponse response = new AnalyticsResponse();

        long count = productRepository.count();
        long orderCount = orderRepository.count();
        Double totalRevenue = orderRepository.getTotalRevenue();
        long customerCount = orderRepository.getTotalCustomers();


        response.setProductCount(String.valueOf(count));
        response.setTotalOrders(String.valueOf(orderCount));
        response.setTotalRevenue(String.valueOf(totalRevenue != null ? totalRevenue : 0));
        response.setTotalCustomers(String.valueOf(customerCount));

        return response;
    }
}
