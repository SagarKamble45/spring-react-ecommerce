package com.sagark.ecommerce.project.paylod;

import com.sagark.ecommerce.project.model.SellerStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponse {

    private Long sellerId;

    private String sellerName;

    private String userName;

    private String email;

    private String storeName;

    private String phone;

    private SellerStatus sellerStatus;

    private LocalDateTime createdAt;

    private Long totalProducts;

    private BigDecimal totalSales;

    private String message;
}