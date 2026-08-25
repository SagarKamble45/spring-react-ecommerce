//package com.sagark.ecommerce.project.model;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Table(name ="addresses1")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//
//public class Address1 {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long addressId;
//
//    @NotBlank
//    @Size(min = 5, message = "Street name must be atleast 5 characters")
//    private String street;
//
//    @NotBlank
//    @Size(min = 5, message = "Building name must be atleast 5 characters")
//    private String buildName;
//
//
//    @NotBlank
//    @Size(min = 4, message = "City name must be atleast 5 characters")
//    private String city;
//
//    @NotBlank
//    @Size(min = 2, message = "State name must be atleast 5 characters")
//    private String state;
//
//    @NotBlank
//    @Size(min = 2, message = "Country name must be atleast 5 characters")
//    private String country;
//
//    @NotBlank
//    @Size(min = 4, message = "Pincode name must be atleast 5 characters")
//    private String pincode;
//
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    public Address1(String street, String buildName, String city, String state, String country, String pincode) {
//        this.street = street;
//        this.buildName = buildName;
//        this.city = city;
//        this.state = state;
//        this.country = country;
//        this.pincode = pincode;
//    }
//}
