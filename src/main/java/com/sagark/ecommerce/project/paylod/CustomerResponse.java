package com.sagark.ecommerce.project.paylod;

import com.sagark.ecommerce.project.model.AppRole;
import com.sagark.ecommerce.project.model.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long customerId;
    private String customerName;
    private String email;
    private String phone;
    private Long totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime createdAt;
    private CustomerStatus status;
    private List<AppRole> roles;

}
