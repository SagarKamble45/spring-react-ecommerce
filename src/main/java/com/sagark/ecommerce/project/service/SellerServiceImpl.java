package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.exceptions.APIException;
import com.sagark.ecommerce.project.exceptions.ResourceNotFoundException;
import com.sagark.ecommerce.project.model.*;
import com.sagark.ecommerce.project.paylod.*;
import com.sagark.ecommerce.project.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;


@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final OrderItemRespository orderItemRespository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public SellerServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ProductRepository productRepository, OrderItemRespository orderItemRespository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, OrderRepository orderRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.orderItemRespository = orderItemRespository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;

        var typeMap = modelMapper.getTypeMap(User.class, SellerResponse.class);

        if (typeMap == null) {
            typeMap = modelMapper.createTypeMap(User.class, SellerResponse.class);
        }

        typeMap.addMappings(mapper -> {
            mapper.map(User::getUserId, SellerResponse::setSellerId);
            mapper.map(User::getUserName, SellerResponse::setSellerName);
        });
    }

    // helper: to find seller
    private User findSellerOrThrow(Long sellerId) {

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seller", "sellerId", sellerId
                ));

        boolean isSeller = seller.getRoles()
                .stream()
                .anyMatch(role -> role.getRoleName() == AppRole.ROLE_SELLER);

        if (!isSeller) {
            throw new ResourceNotFoundException(
                    "Seller", "sellerId", sellerId
            );
        }

        return seller;
    }


    @Override
    @Transactional(readOnly = true)
    public SellerPageResponse getAllSellers(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            String keyword,
            String status
    ) {
        List<String> allowedSortFields = List.of(
                "userId",
                "userName",
                "email",
                "storeName",
                "createdAt",
                "sellerStatus"
        );

        if (sortBy == null || !allowedSortFields.contains(sortBy)) {
            sortBy = "userId";
        }
        if (sortOrder == null) {
            sortOrder = "asc";
        }

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        String normalizedKeyword =
                (keyword == null) ? "" : keyword.trim().toLowerCase();

        SellerStatus sellerStatus = null;

        if (status != null && !status.isBlank()) {
            try {
                sellerStatus = SellerStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new APIException("Invalid status value: " + status);
            }
        }


        Page<User> sellerPage = userRepository.findAllSellers(
                AppRole.ROLE_SELLER,
                normalizedKeyword,
                sellerStatus,
                pageable
        );

        List<SellerResponse> sellerResponses = sellerPage.getContent()
                .stream()
                .map(seller -> {
                    SellerResponse response = modelMapper.map(seller, SellerResponse.class);
                    response.setTotalProducts(
                            productRepository.countByUser_UserId(seller.getUserId())
                    );
                    Double totalSales = orderItemRespository.getTotalSalesBySellerId(seller.getUserId());

                    response.setTotalSales(
                            totalSales == null ? BigDecimal.ZERO : BigDecimal.valueOf(totalSales)
                    );
                    return response;
                }).toList();

        SellerPageResponse response = new SellerPageResponse();

        response.setContent(sellerResponses);
        response.setPageNumber(sellerPage.getNumber());
        response.setPageSize(sellerPage.getSize());
        response.setTotalElements(sellerPage.getTotalElements());
        response.setTotalPages(sellerPage.getTotalPages());
        response.setLastPage(sellerPage.isLast());

        return response;
    }


    @Override
    public SellerResponse createSeller(SellerDTO sellerDTO) {

        if (userRepository.existsByUserName(sellerDTO.getUserName())) {
            throw new APIException("Username already exists: " + sellerDTO.getUserName()
            );
        }

        if (userRepository.existsByEmail(sellerDTO.getEmail())) {
            throw new APIException("Email already exists: " + sellerDTO.getEmail()
            );
        }

        if (sellerDTO.getPassword() == null || sellerDTO.getPassword().isBlank()) {
            throw new APIException("Password is required for seller creation");
        }

        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(() -> new ResourceNotFoundException(
                "Role", "roleName", AppRole.ROLE_SELLER.name()
        ));

        User seller = new User();

        seller.setUserName(sellerDTO.getUserName());
        seller.setEmail(sellerDTO.getEmail());
        seller.setPassword(passwordEncoder.encode(sellerDTO.getPassword()));

        seller.setStoreName(sellerDTO.getStoreName());
        seller.setPhone(sellerDTO.getPhone());

        seller.setSellerStatus(SellerStatus.ACTIVE);

        seller.setRoles(new HashSet<>());
        seller.getRoles().add(sellerRole);

        User savedSeller = userRepository.save(seller);

        SellerResponse response = modelMapper.map(savedSeller, SellerResponse.class);

        response.setTotalProducts(productRepository.countByUser_UserId(savedSeller.getUserId())
        );

        Double totalSales = orderItemRespository.getTotalSalesBySellerId(savedSeller.getUserId()
        );

        response.setTotalSales(
                totalSales == null ? BigDecimal.ZERO : BigDecimal.valueOf(totalSales)
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public SellerResponse getSellerById(Long sellerId) {
        User seller = findSellerOrThrow(sellerId);

        SellerResponse response = modelMapper.map(seller, SellerResponse.class);

        response.setTotalProducts(productRepository.countByUser_UserId(sellerId));

        Double totalSales = orderItemRespository.getTotalSalesBySellerId(sellerId);

        response.setTotalSales(
                totalSales == null ? BigDecimal.ZERO : BigDecimal.valueOf(totalSales)
        );
        return response;
    }

    @Override
    public SellerResponse updateSeller(Long sellerId, SellerDTO sellerDTO) {

        User seller = findSellerOrThrow(sellerId);

        if (!seller.getUserName().equalsIgnoreCase(sellerDTO.getUserName()) && userRepository.existsByUserName(sellerDTO.getUserName())) {
            throw new APIException("Username already exists: " + sellerDTO.getUserName());
        }

        if (!seller.getEmail().equalsIgnoreCase(sellerDTO.getEmail()) && userRepository.existsByEmail(sellerDTO.getEmail())) {
            throw new APIException("Email already exists: " + sellerDTO.getEmail());
        }

        seller.setUserName(sellerDTO.getUserName());
        seller.setEmail(sellerDTO.getEmail());
        seller.setStoreName(sellerDTO.getStoreName());
        seller.setPhone(sellerDTO.getPhone());

        // change password only when a new password is provided
        if (sellerDTO.getPassword() != null && !sellerDTO.getPassword().isBlank()) {
            seller.setPassword(passwordEncoder.encode(sellerDTO.getPassword()));
        }

        User updatedSeller = userRepository.save(seller);
        SellerResponse response = modelMapper.map(updatedSeller, SellerResponse.class);

        return response;
    }

    @Override
    @Transactional
    public SellerResponse deleteSeller(Long sellerId) {

        // 1. Find seller
        User seller = findSellerOrThrow(sellerId);

        // 2. Calculate seller-related records BEFORE deletion
        Long totalProducts =
                productRepository.countByUser_UserId(sellerId);

        Long totalSellerOrderItems =
                orderItemRespository.countByProduct_User_UserId(sellerId);

        Long totalPersonalOrders =
                orderRepository.countByAddress_User_UserId(sellerId);

        Double totalSales =
                orderItemRespository.getTotalSalesBySellerId(sellerId);

        // 3. Prepare response
        SellerResponse response = modelMapper.map(
                seller,
                SellerResponse.class
        );

        response.setTotalProducts(totalProducts);

        response.setTotalSales(
                totalSales == null
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(totalSales)
        );

        // 4. Check whether seller has associated records
        if (totalProducts > 0 ||
                totalSellerOrderItems > 0 ||
                totalPersonalOrders > 0) {

            // Preserve seller and associated historical records
            seller.setSellerStatus(SellerStatus.INACTIVE);

            userRepository.save(seller);

            response.setSellerStatus(SellerStatus.INACTIVE);

            response.setMessage(
                    "This seller cannot be permanently deleted because they have associated products or order history. The seller has been marked as INACTIVE instead."
            );

        } else {

            // 5. Delete seller's cart first, if it exists
            Cart cart = cartRepository.findCartByEmail(seller.getEmail());

            if (cart != null) {

                // CartItem records are deleted through Cart's
                // CascadeType.REMOVE and orphanRemoval settings.
                cartRepository.delete(cart);
            }

            // 6. Permanently delete seller
            // Hibernate handles the user_role join-table entries.
            // Address records are handled by the User mapping's
            // orphanRemoval = true setting.
            userRepository.delete(seller);

            response.setMessage(
                    "Seller has been permanently deleted successfully."
            );
        }

        return response;
    }

    @Override
    public SellerResponse updateSellerStatus(
            Long sellerId,
            SellerStatusDTO sellerStatusDTO) {

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seller",
                                "sellerId",
                                sellerId
                        )
                );

        // Ensure the selected user is actually a seller.
        boolean isSeller = seller.getRoles().stream()
                .anyMatch(role ->
                        role.getRoleName() == AppRole.ROLE_SELLER
                );

        if (!isSeller) {
            throw new RuntimeException("User is not a seller");
        }

        seller.setSellerStatus(sellerStatusDTO.getSellerStatus());

        userRepository.save(seller);

        // Reuse existing mapping and seller statistics logic.
        return getSellerById(sellerId);
    }

    @Override
    @Transactional(readOnly = true)
    public SellerStatsResponse getSellerStats() {

        long totalSellers = userRepository.countSellersByRole(
                AppRole.ROLE_SELLER
        );

        long activeSellers = userRepository.countSellersByRoleAndStatus(
                AppRole.ROLE_SELLER,
                SellerStatus.ACTIVE
        );

        long inactiveSellers = userRepository.countSellersByRoleAndStatus(
                AppRole.ROLE_SELLER,
                SellerStatus.INACTIVE
        );

        long totalProducts = productRepository.countProductsBySellerRole(
                AppRole.ROLE_SELLER
        );

        return new SellerStatsResponse(
                totalSellers,
                activeSellers,
                inactiveSellers,
                totalProducts
        );
    }

}