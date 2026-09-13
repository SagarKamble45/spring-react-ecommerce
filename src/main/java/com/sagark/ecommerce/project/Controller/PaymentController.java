package com.sagark.ecommerce.project.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.razorpay.RazorpayException;
import com.sagark.ecommerce.project.exceptions.ResourceNotFoundException;
import com.sagark.ecommerce.project.model.Cart;
import com.sagark.ecommerce.project.paylod.RazorpayOrderResponseDTO;
import com.sagark.ecommerce.project.paylod.RazorpayPaymentDetailsDTO;
import com.sagark.ecommerce.project.repositories.CartRepository;
import com.sagark.ecommerce.project.service.RazorpayService;
import com.sagark.ecommerce.project.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<RazorpayOrderResponseDTO> createRazorpayOrder() throws RazorpayException {
        String emailId = authUtil.loggedInEmail();

        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }

        RazorpayOrderResponseDTO response = razorpayService.createOrder(cart.getTotalPrice());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/razorpay/details/{paymentId}")
    public ResponseEntity<RazorpayPaymentDetailsDTO> getPaymentDetails(@PathVariable String paymentId) throws RazorpayException {
        RazorpayPaymentDetailsDTO paymentDetails = razorpayService.fetchPaymentDetails(paymentId);
        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }
}