package com.sagark.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, message = "Full name must be at least 3 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(min = 10, message = "Address must be at least 10 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(min = 2, message = "City must be at least 2 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, message = "State must be at least 2 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(min = 2, message = "Country must be at least 2 characters")
    private String country;

    @NotBlank(message = "Pincode is required")
    @Size(min = 6, max = 6, message = "Pincode must be 6 digits")
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(
            String fullName,
            String phone,
            String address,
            String city,
            String state,
            String country,
            String pincode
    ) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }
}