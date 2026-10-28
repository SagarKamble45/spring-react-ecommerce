package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.exceptions.APIException;
import com.sagark.ecommerce.project.exceptions.ResourceNotFoundException;
import com.sagark.ecommerce.project.model.*;
import com.sagark.ecommerce.project.paylod.*;

import com.sagark.ecommerce.project.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRespository orderItemRespository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, String username, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        // Getting User cart
        Cart userCart = cartRepository.findCartByEmail(emailId);
        if (userCart == null){
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }

        Address address = addressRepository.findById(addressId).orElseThrow(()-> new ResourceNotFoundException("Address", "addressId", addressId));

        // Create a new order with payment info
        Order order = new Order();
        order.setUserName(username);
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(userCart.getTotalPrice());
        order.setOrderStatus("Order Accepted !");

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment=paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        // Get items from the cart into the order items
        List<CartItem> cartItems = userCart.getCartItems();
        if (cartItems.isEmpty()){
            throw new APIException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem: cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRespository.saveAll(orderItems);

        // Update product stock
        userCart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity()- quantity);
            productRepository.save(product);

            // Clear the cart
            cartService.deleteProductFromCart(userCart.getCartId(), item.getProduct().getProductId());
        });

        // Send back the order summary
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(
                item-> orderDTO.getOrderItems().add(
                        modelMapper.map(item, OrderItemDTO.class)
                ));

        orderDTO.setAddressId(addressId);
        return orderDTO;
    }

    @Override
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Order> pageOrders = orderRepository.findAll(pageDetails);
        List<Order> orders = pageOrders.getContent();

        List<OrderDTO> orderDTOs = orders.stream().map(order->modelMapper.map(order, OrderDTO.class)).toList();

        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setContent(orderDTOs);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setTotalElements(pageOrders.getTotalElements());

        return orderResponse;
    }

    @Override
    public List<SalesSummaryDTO> getSalesSummary() {
        return orderRepository.findDailySalesSummary();
    }

    @Override
    public OrderDTO updateOrder( Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("Order","orderId",orderId));
        order.setOrderStatus(status);
        orderRepository.save(order);

        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderStatsDTO getOrderStats() {

        OrderStatsDTO orderStatsDTO = new OrderStatsDTO();
        long orderCount = orderRepository.count();
        Double totalRevenue = orderRepository.getTotalRevenue();

        long totalDeliverdOrders = orderRepository.getTotalDeliverdOrders();
        long totalProcessedOrders = orderRepository.getTotalProcessedOrders();
        long totalPendingOrders = orderRepository.getTotalPendingOrders();
        long totalCancelledOrders = orderRepository.getTotalCancelledOrders();
        long totalShippedOrders = orderRepository.getTotalShippedOrders();

        orderStatsDTO.setTotalOrders(String.valueOf(orderCount));
        orderStatsDTO.setTotalRevenue(String.valueOf(totalRevenue));
        orderStatsDTO.setTotalDelivered(String.valueOf(totalDeliverdOrders));
        orderStatsDTO.setTotalProcessed(String.valueOf(totalProcessedOrders));
        orderStatsDTO.setPendingOrders(String.valueOf(totalPendingOrders));
        orderStatsDTO.setCancelledOrders(String.valueOf(totalCancelledOrders));
        orderStatsDTO.setShippedOrders(String.valueOf(totalShippedOrders));

        return orderStatsDTO;
    }

    @Override
    public List<RecentOrderDTO> getLatestFiveOrders() {
        List<Order> orders = orderRepository.findLatestFiveOrders();

        List<RecentOrderDTO> result = orders.stream()
                .map(order -> modelMapper.map(order, RecentOrderDTO.class))
                .toList();

        return result;
    }
}