
package com.sagark.ecommerce.project.repositories;

import com.sagark.ecommerce.project.model.AppRole;
import com.sagark.ecommerce.project.model.Order;
import com.sagark.ecommerce.project.paylod.SalesSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    // Count orders using email, including older orders with NULL address_id.
    long countByEmailIgnoreCase(String email);

    List<Order> findByEmailIgnoreCaseOrderByOrderDateDesc(String email);

    // Get only the authenticated customer's orders with pagination.
    Page<Order> findByEmailIgnoreCase(String email, Pageable pageable);



    // Fetch the customer's complete matching order history,
// applying filters before pagination.
    @Query("""
    SELECT o
    FROM Order o
    WHERE LOWER(o.email) = LOWER(:email)

    AND o.orderDate >= COALESCE(:fromDate, o.orderDate)

    AND o.orderDate <= COALESCE(:toDate, o.orderDate)

    AND (
        :status = 'ALL'

        OR (
            :status = 'IN_PROGRESS'
            AND LOWER(o.orderStatus) IN (
                'order accepted !',
                'pending',
                'processing',
                'shipped'
            )
        )

        OR (
            :status = 'DELIVERED'
            AND LOWER(o.orderStatus) = 'delivered'
        )

        OR (
            :status = 'CANCELLED'
            AND LOWER(o.orderStatus) = 'cancelled'
        )
    )
    """)
    Page<Order> findMyOrdersWithFilters(
            @Param("email") String email,
            @Param("status") String status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
}
