package com.sagark.ecommerce.project.repositories;

import com.sagark.ecommerce.project.model.Category;
import com.sagark.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.sagark.ecommerce.project.model.AppRole;
import org.springframework.data.repository.query.Param;


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

    long countByUser_UserId(Long sellerId);

    @Query("""
    SELECT COUNT(DISTINCT p)
    FROM Product p
    JOIN p.user u
    JOIN u.roles r
    WHERE r.roleName = :role
    """)
    long countProductsBySellerRole(@Param("role") AppRole role);
}
