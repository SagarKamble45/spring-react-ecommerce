package com.sagark.ecommerce.project.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.sagark.ecommerce.project.paylod.RazorpayOrderResponseDTO;
import com.sagark.ecommerce.project.paylod.RazorpayPaymentDetailsDTO;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    @Autowired
    private RazorpayClient razorpayClient;

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;


    @Override
    public RazorpayOrderResponseDTO createOrder(Double orderAmount) throws RazorpayException {

        int amount = (int) Math.round(orderAmount* 100);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(orderRequest);

        RazorpayOrderResponseDTO orderResponseDTO = new RazorpayOrderResponseDTO();
        orderResponseDTO.setRazorpayOrderId(order.get("id"));
        orderResponseDTO.setAmount(amount);
        orderResponseDTO.setCurrency("INR");
        orderResponseDTO.setKeyId(keyId);
        return orderResponseDTO;
    }


    @Override
    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws RazorpayException {

        if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
            return false;   // treat missing fields as a failed verification, not a crash
        }

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", razorpayOrderId);
        options.put("razorpay_payment_id", razorpayPaymentId);
        options.put("razorpay_signature", razorpaySignature);

        return Utils.verifyPaymentSignature(options, keySecret);
    }

    @Override
    public RazorpayPaymentDetailsDTO fetchPaymentDetails(String paymentId) throws RazorpayException {
        com.razorpay.Payment payment = razorpayClient.payments.fetch(paymentId);

        RazorpayPaymentDetailsDTO rpDetailsDto = new RazorpayPaymentDetailsDTO();

        rpDetailsDto.setPaymentId(payment.get("id"));
        rpDetailsDto.setOrderId(payment.get("order_id"));
        rpDetailsDto.setAmount(payment.get("amount"));
        rpDetailsDto.setCurrency(payment.get("currency"));
        rpDetailsDto.setStatus(payment.get("status"));
        rpDetailsDto.setMethod(payment.get("method"));
        if ("card".equals(payment.get("method"))) {
            try {
                String cardHolderName = payment.get("card.name");
                rpDetailsDto.setCardHolderName(cardHolderName);
            } catch (Exception e) {
                rpDetailsDto.setCardHolderName(null);
            }
        }
        rpDetailsDto.setEmail(payment.get("email"));
        rpDetailsDto.setContact(payment.get("contact"));
        rpDetailsDto.setCaptured(payment.get("captured"));

        return rpDetailsDto;
    }
}
