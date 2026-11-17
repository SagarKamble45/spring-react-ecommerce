package com.sagark.ecommerce.project.Controller;


import com.sagark.ecommerce.project.config.AppConstants;
import com.sagark.ecommerce.project.model.CustomerStatus;
import com.sagark.ecommerce.project.paylod.*;
import com.sagark.ecommerce.project.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    // Get all customer with pagination, search, filtering and sorting
    @GetMapping("/admin/customers")
    public ResponseEntity<CustomerPageResponse> getAllCustomers(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortOrder,
            @RequestParam( required = false) String keyword,
            @RequestParam(required = false) String status
    ){
        CustomerPageResponse customers = customerService.getAllCustomers(
                pageNumber,
                pageSize,
                sortBy,
                sortOrder,
                keyword,
                status
        );

        return new ResponseEntity<>(customers, HttpStatus.OK);
    }


//    Get Customer statistics
    @GetMapping("/admin/customers/stats")
    public ResponseEntity<CustomerStatsResponse> getCustomerStats(){
        CustomerStatsResponse customerStats = customerService.getCustomerStats();
        return new ResponseEntity<>(customerStats, HttpStatus.OK);
    }


    //Get Customer Details
    @GetMapping("/admin/customers/{customerId}")
    public ResponseEntity<CustomerDetailsResponse> getCustomerDetails(
            @PathVariable Long customerId
    ){
        CustomerDetailsResponse customerDetails = customerService.getCustomerDetails(customerId);

        return new ResponseEntity<>(customerDetails, HttpStatus.OK);
    }


    // Update the status of the customer
    @PatchMapping("/admin/customers/{customerId}/status")
    public ResponseEntity<CustomerResponse> updateCustomerStatus(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerStatusUpdate request
    ){
      CustomerResponse updatedCustomer = customerService.updateCustomerStatus(customerId, request.getCustomerStatus());

      return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }
}

