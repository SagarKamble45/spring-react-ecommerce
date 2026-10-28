package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.paylod.ProductDTO;
import com.sagark.ecommerce.project.paylod.ProductResponse;
import com.sagark.ecommerce.project.paylod.ProductStatsDTO;
import com.sagark.ecommerce.project.paylod.RecentProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category);

    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

//    ProductResponse searchProductByKeyword(String s, Integer pageNumber, Integer pageSize, String sortBy, String keyword);


    ProductResponse searchProductByKeyword( Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

    ProductResponse getAllProductsforAdmin(String keyword, String category, String stockStatus, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductStatsDTO getProductStats();

    List<RecentProductDTO> getLatestFiveOrders();
}
