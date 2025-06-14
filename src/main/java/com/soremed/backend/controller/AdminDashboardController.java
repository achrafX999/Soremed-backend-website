package com.soremed.backend.controller;

import com.soremed.backend.service.AdminDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService svc;

    public AdminDashboardController(AdminDashboardService svc) {
        this.svc = svc;
    }

    @GetMapping
    public AdminDashboardService.Metrics metrics() {
        return svc.getMetrics();
    }
}