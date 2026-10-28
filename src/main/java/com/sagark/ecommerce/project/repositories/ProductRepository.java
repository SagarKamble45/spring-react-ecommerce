package com.sagark.ecommerce.project.repositories;

import com.sagark.ecommerce.project.model.Category;
import com.sagark.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {


    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetails);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity > 0")
    long getTotalActiveProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity = 0")
    long getTotalOutOfStockProducts();

    @Query(
            value = "SELECT * FROM products ORDER BY product_id DESC LIMIT 5",
            nativeQuery = true
    )
    List<Product> findLatestFiveProducts();
}
