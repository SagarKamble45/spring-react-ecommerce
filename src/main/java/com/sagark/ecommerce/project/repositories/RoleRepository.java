package com.sagark.ecommerce.project.repositories;

import com.sagark.ecommerce.project.model.AppRole;
import com.sagark.ecommerce.project.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(AppRole appRole);
}
