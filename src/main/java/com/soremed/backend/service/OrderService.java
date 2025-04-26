package com.soremed.backend.service;

import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.OrderItem;
import com.soremed.backend.repository.MedicationRepository;
import com.soremed.backend.repository.OrderItemRepository;
import com.soremed.backend.repository.OrderRepository;
import com.soremed.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final MedicationRepository medicationRepo;
    private final UserRepository userRepo;
    private final OrderItemRepository itemRepo;

    public OrderService(OrderRepository orderRepo,
                        MedicationRepository medicationRepo,
                        UserRepository userRepo,
                        OrderItemRepository itemRepo) {
        this.orderRepo = orderRepo;
        this.medicationRepo = medicationRepo;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
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
     */
    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items) {
        Order order = new Order();
        if (!userRepo.existsById(userId)) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
        userRepo.findById(userId).ifPresent(order::setUser);

        for (OrderItem item : items) {
            Long medId = Optional.ofNullable(item.getMedication())
                    .map(Medication::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Medication ID is required"));
            Medication med = medicationRepo.findById(medId)
                    .orElseThrow(() -> new IllegalArgumentException("Medication with ID " + medId + " not found"));
            OrderItem newItem = new OrderItem(order, med, item.getQuantity());
            order.addItem(newItem);
        }

        return orderRepo.save(order);
    }

    /**
     * Ajoute ou met à jour un seul OrderItem dans une commande existante.
     * Si l’item n’existe pas => INSERT, sinon => quantity += qty
     */
    @Transactional
    public Order addOrUpdateItem(Long orderId, Long medicationId, int qty) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        Medication med = medicationRepo.findById(medicationId)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found: " + medicationId));

        try {
            OrderItem item = new OrderItem(order, med, qty);
            itemRepo.save(item);
        } catch (DataIntegrityViolationException ex) {
            OrderItem existing = itemRepo.findByOrderAndMedication(order, med)
                    .orElseThrow(() -> new EntityNotFoundException("Existing OrderItem not found"));
            existing.setQuantity(existing.getQuantity() + qty);
            itemRepo.save(existing);
        }

        return orderRepo.findById(orderId).orElseThrow();
    }

    /**
     * Met à jour le statut d'une commande.
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        order.setStatus(newStatus);
        return orderRepo.save(order);
    }
}
