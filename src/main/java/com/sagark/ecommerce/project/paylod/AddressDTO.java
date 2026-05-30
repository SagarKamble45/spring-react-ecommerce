package com.sagark.ecommerce.project.paylod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long addressId;
    private String street;
    private String buildName;
    private String city;
    private String state;
    private String country;
    private String pincode;

}
