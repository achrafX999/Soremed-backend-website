package com.soremed.backend.controller;

import com.soremed.backend.dto.OrderDTO;
import com.soremed.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {
    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 1. Lister toutes les commandes pour l’Admin */
    @GetMapping
    public List<OrderDTO> listAll() {
        return orderService.listAllOrdersForAdmin();
    }

    /** 2. Mettre à jour le statut d’une commande */
    @PutMapping("/{id}/status")
    public OrderDTO updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return orderService.updateOrderStatusDto(id, status);
    }

    /** 3. Exporter les commandes (CSV ou autre) */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        byte[] csv = orderService.exportOrdersCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"orders.csv\"")
                .body(csv);
    }
}
