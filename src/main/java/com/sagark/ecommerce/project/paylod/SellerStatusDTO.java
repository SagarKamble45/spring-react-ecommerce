
package com.sagark.ecommerce.project.paylod;

import com.sagark.ecommerce.project.model.SellerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerStatusDTO {

    @NotNull(message = "Seller status is required")
    private SellerStatus sellerStatus;
}
