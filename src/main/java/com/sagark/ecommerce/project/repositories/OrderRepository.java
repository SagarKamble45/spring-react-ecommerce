package com.sagark.ecommerce.project.repositories;

import com.sagark.ecommerce.project.model.AppRole;
import com.sagark.ecommerce.project.model.Order;
import com.sagark.ecommerce.project.paylod.SalesSummaryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    Double getTotalRevenue();
    @Query("SELECT COUNT(DISTINCT o.email) FROM Order o WHERE o.orderStatus = 'Order Accepted !'")
    long getTotalCustomers();

    @Query("SELECT new com.sagark.ecommerce.project.paylod.SalesSummaryDTO(o.orderDate, SUM(o.totalAmount)) " +
            "FROM Order o GROUP BY o.orderDate ORDER BY o.orderDate")
    List<SalesSummaryDTO> findDailySalesSummary();



    @Query("SELECT COUNT(o) from Order o where o.orderStatus= 'Delivered'")
    long getTotalDeliverdOrders();

    @Query("SELECT COUNT(o) from Order o where o.orderStatus= 'Processing'")
    long getTotalProcessedOrders();

    @Query("SELECT COUNT(o) from Order o where o.orderStatus= 'Pending'")
    long getTotalPendingOrders();

    @Query("SELECT COUNT(o) from Order o where o.orderStatus= 'Shipped'")
    long getTotalShippedOrders();

    @Query("SELECT COUNT(o) from Order o where o.orderStatus= 'Cancelled'")
    long getTotalCancelledOrders();


    @Query(
            value = "SELECT * FROM orders ORDER BY order_date DESC LIMIT 5",
            nativeQuery = true
    )
    List<Order> findLatestFiveOrders();

    long countByAddress_User_UserId(Long userId);


    @Query("""
        select coalesce(sum (o.totalAmount),0)
        from Order o
        where o.address.user.userId = :userId
""")
    Double getTotalSpentByCustomerId(
            @Param("userId") Long userId
    );

    // Count orders placed by users having ROLE_USER.
    // Uses email instead of address because older orders may have NULL address_id.
    // Includes ADMIN and SELLER accounts when they also have ROLE_USER.
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE EXISTS (
            SELECT u.userId
            FROM User u
            JOIN u.roles r
            WHERE LOWER(u.email) = LOWER(o.email)
            AND r.roleName = :role
        )
        """)
    long countCustomerOrders(@Param("role") AppRole role);

    // Count orders using the customer's email.
    // This also includes older orders where address_id is NULL.
    long countByEmailIgnoreCase(String email);

    List<Order> findByEmailIgnoreCaseOrderByOrderDateDesc(String email);
}
