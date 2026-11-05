package com.sagark.ecommerce.project.repositories;


import com.sagark.ecommerce.project.model.AppRole;
import com.sagark.ecommerce.project.model.SellerStatus;
import com.sagark.ecommerce.project.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserName(String username);

    boolean existsByEmail(String email);

    boolean existsByUserName(String username);

    @Query("""
    SELECT DISTINCT u
    FROM User u
    JOIN u.roles r
    WHERE r.roleName = :role
    AND (
        :keyword = ''
        OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(u.storeName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND (
        :status IS NULL
        OR u.sellerStatus = :status
    )
    """)
    Page<User> findAllSellers(
            @Param("role") AppRole appRole,
            @Param("keyword") String keyword,
            @Param("status") SellerStatus sellerStatus,
            Pageable pageable
    );

    // Total users having ROLE_SELLER
    @Query("""
    SELECT COUNT(DISTINCT u)
    FROM User u
    JOIN u.roles r
    WHERE r.roleName = :role
    """)
    long countSellersByRole(@Param("role") AppRole role);


    // Total ACTIVE sellers
    @Query("""
    SELECT COUNT(DISTINCT u)
    FROM User u
    JOIN u.roles r
    WHERE r.roleName = :role
    AND u.sellerStatus = :status
    """)
    long countSellersByRoleAndStatus(
            @Param("role") AppRole role,
            @Param("status") SellerStatus status
    );
}
