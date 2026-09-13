package com.sagark.ecommerce.project.paylod;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Long addressId;
    private Long paymentMethod;
    private String pgName;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;


    // New: filled in by the frontend from the Razorpay checkout response.
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
