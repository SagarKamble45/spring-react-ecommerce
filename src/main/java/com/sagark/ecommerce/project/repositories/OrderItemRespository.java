package com.sagark.ecommerce.project.repositories;

import com.sagark.ecommerce.project.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRespository extends JpaRepository<OrderItem, Long> {

    @Query("""
    SELECT COALESCE(SUM(oi.orderedProductPrice * oi.quantity), 0)
    FROM OrderItem oi
    WHERE oi.product.user.userId = :sellerId
    """)
    Double getTotalSalesBySellerId(@Param("sellerId") Long sellerId);

    long countByProduct_User_UserId(Long sellerId);
}
