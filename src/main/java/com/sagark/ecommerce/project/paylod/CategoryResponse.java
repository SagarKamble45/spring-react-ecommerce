package com.sagark.ecommerce.project.paylod;

import lombok.Data;

import java.util.List;

@Data

public class CategoryResponse {
    private List<CategoryDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private Boolean lastPage;
}
