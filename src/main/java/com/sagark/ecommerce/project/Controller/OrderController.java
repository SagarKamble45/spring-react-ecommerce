package com.sagark.ecommerce.project.Controller;


import com.sagark.ecommerce.project.config.AppConstants;
import com.sagark.ecommerce.project.paylod.*;

import com.sagark.ecommerce.project.security.services.UserDetailsImpl;
import com.sagark.ecommerce.project.service.OrderService;
import com.sagark.ecommerce.project.util.AuthUtil;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.observation.autoconfigure.ObservationProperties;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.razorpay.RazorpayException;
import com.sagark.ecommerce.project.exceptions.APIException;
import com.sagark.ecommerce.project.service.RazorpayService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private RazorpayService razorpayService;


    private Authentication authentication;


    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts (@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO, Authentication authentication){
        String emailId = authUtil.loggedInEmail();
        String username = authentication.getName();

        System.out.println("username : " + username);


        System.out.println("Order DTO: " + orderRequestDTO);

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
                username,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                pgName,
                pgPaymentId,
                pgStatus,
                pgResponseMessage
        );

        System.out.println("new Order DTO: " + orderDTO);
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }


    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateDto orderStatusUpdateDto){

        OrderDTO order = orderService.updateOrder(orderId, orderStatusUpdateDto.getStatus());
        return new ResponseEntity<OrderDTO>(order, HttpStatus.OK);

    }

    @GetMapping("/admin/orderStats")
    public ResponseEntity<OrderStatsDTO> getOrderStats(){
        OrderStatsDTO orderStatsDTO = orderService.getOrderStats();
        return new ResponseEntity<OrderStatsDTO>(orderStatsDTO, HttpStatus.OK);
    }


    @GetMapping("/admin/orders/recent")
    public ResponseEntity<List<RecentOrderDTO>> getLatestFiveOrders() {

        List<RecentOrderDTO> recentOrderDto = orderService.getLatestFiveOrders();

       return  new ResponseEntity<List<RecentOrderDTO>>(recentOrderDto, HttpStatus.OK);
    }

    @GetMapping("/user/orders")
    public ResponseEntity<OrderResponse> getMyOrders(
            @RequestParam(
                    name = "pageNumber",
                    defaultValue = "0",
                    required = false
            ) Integer pageNumber,

            @RequestParam(
                    name = "pageSize",
                    defaultValue = "10",
                    required = false
            ) Integer pageSize,

            @RequestParam(
                    name = "sortBy",
                    defaultValue = "orderDate",
                    required = false
            ) String sortBy,

            @RequestParam(
                    name = "sortOrder",
                    defaultValue = "desc",
                    required = false
            ) String sortOrder,

            // Customer Order-history & filters
            @RequestParam(
                    name = "status",
                    defaultValue = "ALL",
                    required = false
            ) String status,

            @RequestParam(
                    name="fromDate",
                    required = false
            ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(
                    name = "toDate",
                    required = false
            ) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate
    ) {

        // Get the authenticated customer's email
        String email = authUtil.loggedInEmail();

        // fetch only customer's order
        OrderResponse orderResponse = orderService.getMyOrders(
                email,
                pageNumber,
                pageSize,
                sortBy,
                sortOrder,
                status,
                fromDate,
                toDate
        );

        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

}

