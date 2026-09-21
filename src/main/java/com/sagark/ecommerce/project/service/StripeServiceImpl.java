package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.paylod.StripePaymentDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeApiKey;
    }


    @Override
    public PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) throws StripeException {

        Customer customer;

        // retrieve the customer i.e. exists or not
        CustomerSearchParams searchParams = CustomerSearchParams.builder()
                .setQuery("email:'" + stripePaymentDTO.getEmail() + "'")
                .build();

        // Customer.search is a static method — call it on the class, not a null instance
        CustomerSearchResult customers = Customer.search(searchParams);

        if (customers.getData().isEmpty()) {
            // create new customer
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(stripePaymentDTO.getName())
                    .setEmail(stripePaymentDTO.getEmail())
                    .setAddress(
                            CustomerCreateParams.Address.builder()
                                    .setLine1(stripePaymentDTO.getAddress().getAddress())
                                    .setCity(stripePaymentDTO.getAddress().getCity())
                                    .setState(stripePaymentDTO.getAddress().getState())
                                    .setCountry(stripePaymentDTO.getAddress().getCountry())
                                    .setPostalCode(stripePaymentDTO.getAddress().getPincode())
                                    .build()
                    )
                    .build();
            customer = Customer.create(customerParams);
        } else {
            // fetch the customer that exists
            customer = customers.getData().get(0);
        }

        long amountInPaise = Math.round(stripePaymentDTO.getAmount() * 100);
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amountInPaise)
                        .setCurrency(stripePaymentDTO.getCurrency())
                        .setCustomer(customer.getId())
                        .setDescription(stripePaymentDTO.getDescription())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();




        return  PaymentIntent.create(params);
    }
}
