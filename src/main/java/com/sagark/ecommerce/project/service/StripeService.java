package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.paylod.StripePaymentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {
    PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) throws StripeException;
}
