package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.exceptions.APIException;
import com.sagark.ecommerce.project.exceptions.ResourceNotFoundException;
import com.sagark.ecommerce.project.model.*;
import com.sagark.ecommerce.project.paylod.*;
import com.sagark.ecommerce.project.repositories.OrderRepository;
import com.sagark.ecommerce.project.repositories.UserRepository;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {


    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public CustomerServiceImpl(
            UserRepository userRepository,
            OrderRepository orderRepository,
            ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;

        // Step 1: Configure ModelMapper BEFORE creating the TypeMap.
        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true);

        // Step 2: Get an existing TypeMap or create one.
        TypeMap<User, CustomerResponse> typeMap =
                modelMapper.getTypeMap(User.class, CustomerResponse.class);

        if (typeMap == null) {
            typeMap = modelMapper.createTypeMap(
                    User.class,
                    CustomerResponse.class
            );
        }

        // Step 3: Explicitly configure customer-specific mappings.
        typeMap.addMappings(mapper -> {

            mapper.skip(CustomerResponse::setRoles);

            mapper.map(
                    User::getUserId,
                    CustomerResponse::setCustomerId
            );

            mapper.map(
                    User::getUserName,
                    CustomerResponse::setCustomerName
            );

            mapper.map(
                    User::getCustomerStatus,
                    CustomerResponse::setStatus
            );
        });
    }

    //    Get all customer with pagination, search, filtering and sorting
    @Override
    @Transactional(readOnly = true)
    public CustomerPageResponse getAllCustomers(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            String keyword,
            String status
    ) {
//        validate pagination parameters
        if (pageNumber == null || pageNumber < 0){
            throw new APIException("Page number cannot be negative");
        }

        if (pageSize == null || pageSize <= 0 || pageSize >100){
            throw new APIException(
                    "Page size must be between 1 and 100"
            );
        }

//        Allow only valid customer sorting fields
        List<String> allowedSortFields = List.of(
                "userId",
                "userName",
                "email",
                "phone",
                "createdAt",
                "customerStatus"
        );

        if (sortBy == null || !allowedSortFields.contains(sortBy)){
            sortBy = "userId";
        }

//        Configure sorting direction
        if (sortOrder == null || sortOrder.isBlank()){
            sortOrder = "asc";
        }

        Sort sort;
        if (sortOrder.equalsIgnoreCase("asc")){
            sort = Sort.by(sortBy).ascending();
        }else if(sortOrder.equalsIgnoreCase("desc")){
            sort = Sort.by(sortBy).descending();
        }else{
            throw new APIException(
                    "Invalid sort order. Use asc or desc"
            );
        }

//        Creating pagination request
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                sort
        );

//        Normalize search keyword
        String normalizedKeyword =
                (keyword == null) ? "" : keyword.trim().toLowerCase();

//        Convert customer status string into enum
        CustomerStatus customerStatus = null;

        if (status != null && !status.isBlank()){
            try {
                customerStatus = CustomerStatus.valueOf(
                        status.trim().toUpperCase()
                );
            }catch (IllegalArgumentException e){
                throw new APIException(
                        "Invalid customer status: " + status
                );
            }
        }


//        Fetch customers from database
        Page<User> customerPage = userRepository.findAllCustomers(
                AppRole.ROLE_USER,
                normalizedKeyword,
                customerStatus,
                CustomerStatus.ACTIVE,
                pageable
        );

//        Convert customer entities into resposne DTOs
        List<CustomerResponse> customerResponses =
                customerPage.getContent()
                        .stream()
                        .map(customer -> {
                            CustomerResponse response =
                                    modelMapper.map(
                                            customer,
                                            CustomerResponse.class
                                    );

                            // Treat legacy NULL Status as ACTIVE
                            if(customer.getCustomerStatus() == null){
                                response.setStatus(CustomerStatus.ACTIVE);
                            }

                            // Calculate total customer orders
                            long totalOrders = orderRepository.countByEmailIgnoreCase(
                                    customer.getEmail()
                            );

                            response.setTotalOrders(totalOrders);

                            // Calculate total customer spending
                            Double totalSpent = orderRepository.getTotalSpentByCustomerId(
                                    customer.getUserId()
                            );

                            response.setTotalSpent(
                                    totalSpent == null
                                            ? BigDecimal.ZERO
                                            : BigDecimal.valueOf(totalSpent)
                            );

                            // Set the Roles
                            response.setRoles(
                                    customer.getRoles()
                                            .stream()
                                            .map(Role::getRoleName)
                                            .toList()
                            );

                            return response;
                        }).toList();

//        Build paginated repsponse
        CustomerPageResponse response = new CustomerPageResponse();

        response.setContent(customerResponses);
        response.setPageNumber(customerPage.getNumber());
        response.setPageSize(customerPage.getSize());
        response.setTotalElements(customerPage.getTotalElements());
        response.setTotalPages(customerPage.getTotalPages());
        response.setLastPage(customerPage.isLast());

        return response;
    }

    @Override
    public CustomerStatsResponse getCustomerStats() {

//        Count all accounts having ROLE_USER
        // Additional ADMIN or SELLER roles do not exclude an account
        long totalCustomers = userRepository.countCustomers(
                AppRole.ROLE_USER
        );

        // Count ACTIVE customers, including legacy null statuses
        long activeCustomers = userRepository.countCustomersByStatus(
                AppRole.ROLE_USER,
                CustomerStatus.ACTIVE,
                CustomerStatus.ACTIVE
        );

        // Count INACTIVE customers
        long inactiveCustomers = userRepository.countCustomersByStatus(
                AppRole.ROLE_USER,
                CustomerStatus.INACTIVE,
                CustomerStatus.ACTIVE
        );

        // Count orders placed by customers
        long totalOrders = orderRepository.countCustomerOrders(
                AppRole.ROLE_USER
        );

        return new CustomerStatsResponse(
                totalCustomers,
                activeCustomers,
                inactiveCustomers,
                totalOrders
        );
    }

    @Override
    public CustomerDetailsResponse getCustomerDetails(Long customerId) {

        // Find the customer by ID
        User customer = userRepository.findById(customerId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Customer","customerId", customerId)
                );


        // Create the response and map personal information
        CustomerDetailsResponse response = new CustomerDetailsResponse();

        response.setCustomerId(customer.getUserId());
        response.setCustomerName(customer.getUserName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setCreatedAt(customer.getCreatedAt());

        // Include every assigned role
        response.setRoles(
                customer.getRoles()
                        .stream().map(Role::getRoleName).toList()
        );

        // Treat legacy NULL customer status as ACTIVE
        response.setStatus(
                customer.getCustomerStatus() == null
                        ? CustomerStatus.ACTIVE
                        : customer.getCustomerStatus()
        );

        // Map All saved address
        List<AddressDTO> addressDTOS = customer.getAddresses()
                .stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();

        response.setAddresses(addressDTOS);

        // Retrive all orders using the customer's email
        List<Order> orders = orderRepository
                .findByEmailIgnoreCaseOrderByOrderDateDesc(
                        customer.getEmail()
                );

        // Map order Information
        List<OrderDTO> orderDTOS = orders.stream()
                .map(order -> {
                    OrderDTO orderDTO =
                            modelMapper.map(order, OrderDTO.class);

                    // Map order Items explicitly
                    List<OrderItemDTO> orderItemDTOS =
                            order.getOrderItems()
                                    .stream()
                                    .map(orderItem ->
                                            modelMapper.map(orderItem, OrderItemDTO.class)
                                    ).toList();

                    orderDTO.setOrderItems(orderItemDTOS);

                    // Include address ID when available
                    if (order.getAddress() != null){
                        orderDTO.setAddressId(order.getAddress().getAddressId());
                    }

                    return orderDTO;
                }).toList();

        response.setOrders(orderDTOS);

        // Calculate purchase summary
        long totalOrders  = orders.size();

        double totalSpent = orders.stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount!=null)
                .mapToDouble(Double::doubleValue)
                .sum();

        response.setTotalOrders(totalOrders);
        response.setTotalSpent(BigDecimal.valueOf(totalSpent));

        return response;

    }

    @Override
    @Transactional
    public CustomerResponse updateCustomerStatus(
            Long customerId, CustomerStatus status) {

        // find the account by ID
        User customer = userRepository.findById(customerId)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Customer", "customerId", customerId
                        )
                );

        // Update customer status
        customer.setCustomerStatus(status);

        // Save the updated account
        User updatedCustomer = userRepository.save(customer);

        // Map updated account to response
        CustomerResponse response = modelMapper
                .map(updatedCustomer,CustomerResponse.class);

        // Map roles explictly
        response.setRoles(
                updatedCustomer.getRoles()
                        .stream()
                        .map(Role::getRoleName)
                        .toList()
        );

        // Treat NULL status as ACTIVE for legacy accounts
        response.setStatus(
                updatedCustomer.getCustomerStatus() == null
                        ? CustomerStatus.ACTIVE
                        : updatedCustomer.getCustomerStatus()
        );

        // include order count
        response.setTotalOrders(
                orderRepository.countByEmailIgnoreCase(
                        updatedCustomer.getEmail()
                )
        );

        // Include total Spending
        Double totalSpent =
                orderRepository.getTotalSpentByCustomerId(
                        updatedCustomer.getUserId()
                );

        response.setTotalSpent(
                totalSpent == null
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(totalSpent)
        );

        return response;
    }

}
