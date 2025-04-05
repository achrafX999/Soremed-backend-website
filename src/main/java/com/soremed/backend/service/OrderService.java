package com.soremed.backend.service;

import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.OrderItem;
import com.soremed.backend.repository.MedicationRepository;
import com.soremed.backend.repository.OrderRepository;
import com.soremed.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final MedicationRepository medicationRepo;
    private final UserRepository userRepo;

    public OrderService(OrderRepository orderRepo, MedicationRepository medRepo, UserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.medicationRepo = medRepo;
        this.userRepo = userRepo;
    }

    public List<Order> listAllOrders() {
        return orderRepo.findAll();
    }
    public List<Order> listOrdersByUser(Long userId) {
        return orderRepo.findByUserId(userId);
    }
    public Order getOrder(Long id) {
        return orderRepo.findById(id).orElse(null);
    }

    /**
     * Crée une nouvelle commande pour un utilisateur donné, avec des items.
     * @param userId l'ID du user qui passe commande
     * @param items liste d'objets (medicationId, quantity) pour la commande
     */
    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items) {
        Order order = new Order();
        // Associer le user à la commande
        userRepo.findById(userId).ifPresent(order::setUser);
        // Ajouter chaque item à la commande
        for (OrderItem item : items) {
            // Récupérer le médicament depuis son ID
            Medication med = medicationRepo.findById(item.getMedication().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid medication ID"));
            item.setMedication(med);
            order.addItem(item);
        }
        // Sauvegarder la commande (cascade = ALL => items seront sauvegardés aussi)
        return orderRepo.save(order);
    }

    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(newStatus);
            orderRepo.save(order);
        }
        return order;
    }
}
