package com.sagark.ecommerce.project.Controller;

import com.razorpay.RazorpayException;
import com.sagark.ecommerce.project.exceptions.ResourceNotFoundException;
import com.sagark.ecommerce.project.model.Cart;
import com.sagark.ecommerce.project.paylod.RazorpayOrderResponseDTO;
import com.sagark.ecommerce.project.paylod.RazorpayPaymentDetailsDTO;
import com.sagark.ecommerce.project.paylod.StripePaymentDTO;
import com.sagark.ecommerce.project.repositories.CartRepository;
import com.sagark.ecommerce.project.service.RazorpayService;
import com.sagark.ecommerce.project.service.StripeService;
import com.sagark.ecommerce.project.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private StripeService stripeService; // Added missing injection

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    // Razorpay: Create Order
    @PostMapping("/razorpay/orders")
    public ResponseEntity<RazorpayOrderResponseDTO> createRazorpayOrder() throws RazorpayException {
        String emailId = authUtil.loggedInEmail();

        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }

        RazorpayOrderResponseDTO response = razorpayService.createOrder(cart.getTotalPrice());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Razorpay: Fetch Details
    @GetMapping("/razorpay/payments/{paymentId}")
    public ResponseEntity<RazorpayPaymentDetailsDTO> getRazorpayPaymentDetails(@PathVariable String paymentId) throws RazorpayException {
        RazorpayPaymentDetailsDTO paymentDetails = razorpayService.fetchPaymentDetails(paymentId);
        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }

    // Stripe: Create Client Secret
    @PostMapping("/stripe/client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDTO stripePaymentDto) throws StripeException {
        System.out.println("StripePaymentDTO Received: " + stripePaymentDto);
        PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDto);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }
}