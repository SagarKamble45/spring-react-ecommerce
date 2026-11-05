package com.sagark.ecommerce.project.Controller;

import com.sagark.ecommerce.project.config.AppConstants;
import com.sagark.ecommerce.project.paylod.*;
import com.sagark.ecommerce.project.service.SellerService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // Create Seller
    @PostMapping("/admin/sellers")
    public ResponseEntity<SellerResponse> createSeller(@Valid @RequestBody SellerDTO sellerDTO) {

        SellerResponse savedSeller = sellerService.createSeller(sellerDTO);

        return new ResponseEntity<>(savedSeller, HttpStatus.OK);
    }

    // Get all Sellers
    @GetMapping("/admin/sellers")
    public ResponseEntity<SellerPageResponse> getAllSellers(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER)
            Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE)
            Integer pageSize,
            @RequestParam(defaultValue = "userId")
            String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR)
            String sortOrder,
            @RequestParam(required = false)
            String keyword,
            @RequestParam(required = false)
            String status
    ) {

        SellerPageResponse sellers = sellerService.getAllSellers(
                pageNumber,
                pageSize,
                sortBy,
                sortOrder,
                keyword,
                status
        );
        return new ResponseEntity<>(sellers, HttpStatus.OK);
    }

    // Get seller by ID
    @GetMapping("/admin/seller/{sellerId}")
    public ResponseEntity<SellerResponse> getSellerById(@PathVariable Long sellerId) {

        SellerResponse seller = sellerService.getSellerById(sellerId);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    // Update Seller
    @PutMapping("/admin/seller/{sellerId}")
    public ResponseEntity<SellerResponse> updateSeller(
            @PathVariable Long sellerId,
            @Valid @RequestBody SellerDTO sellerDTO
    ){
        SellerResponse updateSeller = sellerService.updateSeller(sellerId, sellerDTO);
        return new ResponseEntity<>(updateSeller, HttpStatus.OK);
    }

    // Delete seller
    @DeleteMapping("/admin/seller/{sellerId}")
    public ResponseEntity<SellerResponse> deleteSeller(
            @PathVariable Long sellerId
    ){
        SellerResponse deletedSeller = sellerService.deleteSeller(sellerId);
        return new ResponseEntity<>(deletedSeller, HttpStatus.OK);
    }

    // Change or update the status
    @PatchMapping("/admin/seller/{sellerId}/status")
    public ResponseEntity<SellerResponse> updateSellerStatus(
            @PathVariable Long sellerId,
            @Valid @RequestBody SellerStatusDTO sellerStatusDTO
    ){
        SellerResponse updatedSeller = sellerService.updateSellerStatus(sellerId, sellerStatusDTO);
        return new ResponseEntity<>(updatedSeller, HttpStatus.OK);
    }


    // Get Seller Statistics
    @GetMapping("/admin/sellerStats")
    public ResponseEntity<SellerStatsResponse> getSellerStats() {

        SellerStatsResponse sellerStats = sellerService.getSellerStats();

        return new ResponseEntity<>(sellerStats, HttpStatus.OK);
    }
}
