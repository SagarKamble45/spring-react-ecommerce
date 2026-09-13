package com.sagark.ecommerce.project.paylod;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayPaymentDetailsDTO {
    private String paymentId;
    private String orderId;
    private Integer amount;
    private String currency;
    private String status;
    private String method;
    private String cardHolderName;
    private String email;
    private String contact;
    private Boolean captured;
}
