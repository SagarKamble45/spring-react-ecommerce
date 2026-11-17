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
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailsResponse {

    //Customer personal information
    private Long customerId;
    private String customerName;
    private String email;
    private String phone;
    private List<AppRole> roles;
    private CustomerStatus status;
    private LocalDateTime createdAt;

    // Customer Address
    private List<AddressDTO> addresses;

    // Customer order history
    private List<OrderDTO> orders;

    // Purchase summary
    private Long totalOrders;
    private BigDecimal totalSpent;

}
