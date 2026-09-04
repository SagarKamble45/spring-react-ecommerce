package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.paylod.CartDTO;
import com.sagark.ecommerce.project.paylod.CartItemDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;


public interface CartService {
    public CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCars();

    CartDTO getCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);

    String createOrUpdateCartWithItems(List<CartItemDTO> cartItems);
}
