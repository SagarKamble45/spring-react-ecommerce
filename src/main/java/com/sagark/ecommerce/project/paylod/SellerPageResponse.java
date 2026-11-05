package com.sagark.ecommerce.project.paylod;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerPageResponse {

    private List<SellerResponse> content;

    private Integer pageNumber;

    private Integer pageSize;

    private Long totalElements;

    private Integer totalPages;

    private Boolean lastPage;
}