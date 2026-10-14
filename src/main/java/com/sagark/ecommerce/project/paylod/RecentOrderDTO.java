package com.sagark.ecommerce.project.paylod;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RecentOrderDTO {
    private String orderId;
    private String userName;
    private String email;
    private Double totalAmount;
    private LocalDate orderDate;
    private String orderStatus;
}

