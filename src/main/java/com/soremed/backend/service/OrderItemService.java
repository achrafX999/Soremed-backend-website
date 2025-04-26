package com.soremed.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.persistence.EntityNotFoundException;

import com.soremed.backend.repository.OrderItemRepository;
import com.soremed.backend.repository.OrderRepository;
import com.soremed.backend.repository.MedicationRepository;

import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.OrderItem;


@Service
public class OrderItemService {
    private final OrderItemRepository itemRepo;
    private final OrderRepository orderRepo;
    private final MedicationRepository medRepo;

    public OrderItemService(OrderItemRepository itemRepo,
                            OrderRepository orderRepo,
                            MedicationRepository medRepo) {
        this.itemRepo = itemRepo;
        this.orderRepo = orderRepo;
        this.medRepo = medRepo;
    }

    @Transactional
    public OrderItem addItem(Long orderId, Long medId, int qty) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        Medication med = medRepo.findById(medId)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found"));

        try {
            // Tente l'insertion directe
            OrderItem item = new OrderItem(order, med, qty);
            return itemRepo.save(item);
        } catch (DataIntegrityViolationException ex) {
            // Contrainte unique violée : on met à jour la ligne existante
            OrderItem existing = itemRepo
                    .findByOrderAndMedication(order, med)
                    .orElseThrow(() -> ex);
            existing.setQuantity(existing.getQuantity() + qty);
            return itemRepo.save(existing);
        }
    }
}

