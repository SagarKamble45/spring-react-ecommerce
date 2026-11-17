package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.model.CustomerStatus;
import com.sagark.ecommerce.project.paylod.CustomerDetailsResponse;
import com.sagark.ecommerce.project.paylod.CustomerPageResponse;
import com.sagark.ecommerce.project.paylod.CustomerResponse;
import com.sagark.ecommerce.project.paylod.CustomerStatsResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;


public interface CustomerService {

    // Get all customers with pagination, search, filtering and sorting
    CustomerPageResponse getAllCustomers(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            String keyword,
            String status
    );

//    Get customer statistics
    CustomerStatsResponse getCustomerStats();

    CustomerDetailsResponse getCustomerDetails(Long customerId);

    CustomerResponse updateCustomerStatus(Long customerId, @NotNull(message = "Customer status is required") CustomerStatus status);
}
