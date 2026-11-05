package com.sagark.ecommerce.project.service;

import com.sagark.ecommerce.project.paylod.*;

import java.util.List;

public interface SellerService {


    SellerPageResponse getAllSellers(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder,
            String keyword,
            String status
    );

    SellerResponse createSeller( SellerDTO sellerDTO);

    SellerResponse getSellerById(Long sellerId);

     SellerResponse updateSeller(Long sellerId, SellerDTO sellerDTO);

     SellerResponse deleteSeller(Long sellerId);

    SellerResponse updateSellerStatus(
            Long sellerId,
            SellerStatusDTO sellerStatusDTO
    );

    SellerStatsResponse getSellerStats();
}
