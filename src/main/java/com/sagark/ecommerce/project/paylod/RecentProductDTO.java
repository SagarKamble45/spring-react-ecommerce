package com.sagark.ecommerce.project.paylod;

import lombok.Data;

@Data
public class RecentProductDTO {
    private String productId;
    private String productName;
    private String image;
    private String price;
    private String specialPrice;
    private String quantity;


}
