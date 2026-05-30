package com.sagark.ecommerce.project.repositories;

import com.sagark.ecommerce.project.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
