package com.sagark.ecommerce.project.Controller;


import com.sagark.ecommerce.project.paylod.OrderDTO;
import com.sagark.ecommerce.project.paylod.OrderRequestDTO;
import com.sagark.ecommerce.project.service.OrderService;
import com.sagark.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.razorpay.RazorpayException;
import com.sagark.ecommerce.project.exceptions.APIException;
import com.sagark.ecommerce.project.service.RazorpayService;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts (@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO){
        String emailId = authUtil.loggedInEmail();

        String pgName=orderRequestDTO.getPgName();
        String pgPaymentId=orderRequestDTO.getPgPaymentId();
        String pgStatus =orderRequestDTO.getPgStatus();
        String pgResponseMessage=orderRequestDTO.getPgResponseMessage();

        if("razorpay".equalsIgnoreCase(paymentMethod)){
            boolean isValid;
            try {
                isValid = razorpayService.verifySignature(
                        orderRequestDTO.getRazorpayOrderId(),
                        orderRequestDTO.getRazorpayPaymentId(),
                        orderRequestDTO.getRazorpaySignature()
                );
            } catch (RazorpayException e) {
                throw new APIException("Payment method verification failed: " + e.getMessage());
            }
            if(!isValid){
                throw new APIException("Payment verification failed. Signature mismatch.");
            }

            pgName = "Razorpay";
            pgPaymentId = orderRequestDTO.getRazorpayPaymentId();
            pgStatus = "SUCCESS";
            pgResponseMessage = "Payment method verification success.";
        }


        OrderDTO orderDTO = orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                pgName,
                pgPaymentId,
                pgStatus,
                pgResponseMessage
        );
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
