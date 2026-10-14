package com.sagark.ecommerce.project.Controller;

import com.sagark.ecommerce.project.paylod.AnalyticsResponse;
import com.sagark.ecommerce.project.paylod.SalesSummaryDTO;
import com.sagark.ecommerce.project.repositories.OrderRepository;
import com.sagark.ecommerce.project.service.AnalyticService;
import com.sagark.ecommerce.project.service.OrderService;
import org.modelmapper.internal.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    @Autowired
    private AnalyticService analyticsService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/admin/app/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics(){
        AnalyticsResponse response = analyticsService.getAnalyticsData();
        return new ResponseEntity<AnalyticsResponse>(response, HttpStatus.OK);
    }

    @GetMapping("/admin/app/sales_summary")
    public ResponseEntity<List<SalesSummaryDTO>> getSalesOverview(){
        List<SalesSummaryDTO> salesData = orderService.getSalesSummary();
        return new ResponseEntity<List<SalesSummaryDTO>>(salesData, HttpStatus.OK);
    }
}
