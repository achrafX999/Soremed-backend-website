// src/main/java/com/soremed/backend/service/ClientDashboardService.java
package com.soremed.backend.service;

import com.soremed.backend.dto.ClientTopProductDTO;
import com.soremed.backend.dto.DashboardStatsDTO;
import com.soremed.backend.dto.OrderStatusDTO;
import com.soremed.backend.repository.OrderItemRepository;
import com.soremed.backend.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientDashboardService {

    private final OrderItemRepository orderItemRepo;
    private final OrderRepository     orderRepo;

    public ClientDashboardService(OrderItemRepository orderItemRepo,
                                  OrderRepository orderRepo) {
        this.orderItemRepo = orderItemRepo;
        this.orderRepo     = orderRepo;
    }

    /**
     * Top 5 des produits commandés par le client
     */
    public List<ClientTopProductDTO> getTop5Products(Long clientId) {
        return orderItemRepo.findTopProductsByClient(clientId, PageRequest.of(0, 5));
    }

    /**
     * Répartition des commandes par statut pour le client
     */
    public List<OrderStatusDTO> getStatusDistribution(Long clientId) {
        return orderRepo.countByStatus(clientId);
    }

    /**
     * Statistiques globales (en cours vs terminées)
     */
    public DashboardStatsDTO getStats(Long clientId) {
        long inProgress = orderRepo.countInProgressByClient(clientId);
        long completed  = orderRepo.countCompletedByClient(clientId);
        return new DashboardStatsDTO(inProgress, completed);
    }
}
