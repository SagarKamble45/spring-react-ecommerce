package com.sagark.ecommerce.project.paylod;


import com.sagark.ecommerce.project.model.CustomerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerStatusUpdate {

    @NotNull(message = "Customer status is required")
    private CustomerStatus customerStatus;
}
