package com.sagark.ecommerce.project.paylod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponseDTO {
    private String razorpayOrderId;
    private Integer amount;   // in paise
    private String currency;
    private String keyId;    // public key - safe to send to frontend
}