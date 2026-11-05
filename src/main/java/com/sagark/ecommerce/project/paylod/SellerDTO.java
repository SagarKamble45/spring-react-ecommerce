package com.sagark.ecommerce.project.paylod;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerDTO {

    @NotBlank(message = "Username is required")
    @Size(max = 20)
    private String userName;

    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email cannot exceed 50 characters")
    @Email(message = "please provide a valid email")
    private String email;

    // Required when creating a seller; optional during update
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    private String password;


    @NotBlank(message = "Store name is required")
    private String storeName;

    private String phone;

}
