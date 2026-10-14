package com.sagark.ecommerce.project.paylod;

import java.time.LocalDate;

public record SalesSummaryDTO(
        LocalDate day,
        Double sales
) {
}
