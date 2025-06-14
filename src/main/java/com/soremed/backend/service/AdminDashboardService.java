package com.soremed.backend.service;

import com.soremed.backend.dto.ClientTopProductDTO;
import com.soremed.backend.repository.MedicationRepository;
import com.soremed.backend.repository.OrderItemRepository;
import com.soremed.backend.repository.OrderRepository;
import com.soremed.backend.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminDashboardService {

    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final MedicationRepository medicationRepo;
    private final OrderItemRepository orderItemRepo;

    public record Metrics(
            long totalUsers,
            long activeOrders,
            long totalProducts,
            List<ClientTopProductDTO> topProducts
    ) {}

    public AdminDashboardService(
            UserRepository userRepo,
            OrderRepository orderRepo,
            MedicationRepository medicationRepo,
            OrderItemRepository orderItemRepo
    ) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.medicationRepo = medicationRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @Transactional(readOnly = true)
    public Metrics getMetrics() {
        long users = userRepo.countTotalUsers();
        long active = orderRepo.countActiveOrders();
        long products = medicationRepo.count();
        List<ClientTopProductDTO> top = orderItemRepo.findGlobalTopProducts(PageRequest.of(0, 5));
        return new Metrics(users, active, products, top);
    }
}