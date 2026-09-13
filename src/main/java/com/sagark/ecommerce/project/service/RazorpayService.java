package com.sagark.ecommerce.project.service;

import com.razorpay.RazorpayException;
import com.sagark.ecommerce.project.paylod.RazorpayOrderResponseDTO;
import com.sagark.ecommerce.project.paylod.RazorpayPaymentDetailsDTO;
import org.springframework.stereotype.Service;


public interface RazorpayService {
    RazorpayOrderResponseDTO createOrder(Double amountInRupees) throws RazorpayException;

    boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws RazorpayException;

    RazorpayPaymentDetailsDTO fetchPaymentDetails(String paymentId) throws RazorpayException;
}