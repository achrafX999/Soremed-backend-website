// src/main/java/com/soremed/backend/controller/ClientDashboardController.java
package com.soremed.backend.controller;

import com.soremed.backend.dto.ClientTopProductDTO;
import com.soremed.backend.dto.DashboardStatsDTO;
import com.soremed.backend.dto.OrderStatusDTO;
import com.soremed.backend.service.ClientDashboardService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.soremed.backend.service.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/client/dashboard")
public class ClientDashboardController {

    private final ClientDashboardService svc;

    public ClientDashboardController(ClientDashboardService svc) {
        this.svc = svc;
    }


    /**
     * Top 5 des produits commandés par le client
     */
    @GetMapping("/top-products")
    public List<ClientTopProductDTO> topProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return svc.getTop5Products(userDetails.getId());
    }

    @GetMapping("/status-distribution")
    public List<OrderStatusDTO> statusDistribution(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return svc.getStatusDistribution(userDetails.getId());
    }

    @GetMapping("/stats")
    public DashboardStatsDTO stats(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return svc.getStats(userDetails.getId());
    }

}
