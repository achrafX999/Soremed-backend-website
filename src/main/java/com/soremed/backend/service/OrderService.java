package com.soremed.backend.service;

import com.soremed.backend.dto.OrderDTO;
import com.soremed.backend.dto.OrderItemDTO;
import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.User;
import com.soremed.backend.entity.OrderItem;

// Imports ajoutés pour la notification
import com.soremed.backend.entity.NotificationLog;
import com.soremed.backend.entity.NotificationSettings;
import com.soremed.backend.repository.NotificationLogRepository;
import com.soremed.backend.repository.NotificationSettingsRepository;

import com.soremed.backend.repository.MedicationRepository;
import com.soremed.backend.repository.OrderItemRepository;
import com.soremed.backend.repository.OrderRepository;
import com.soremed.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final MedicationRepository medicationRepo;
    private final UserRepository userRepo;
    private final OrderItemRepository itemRepo;

    // Nouveaux champs pour la notification
    private final NotificationSettingsRepository settingsRepo;
    private final NotificationLogRepository      logRepo;

    public OrderService(OrderRepository orderRepo,
                        MedicationRepository medicationRepo,
                        UserRepository userRepo,
                        OrderItemRepository itemRepo,
                        NotificationSettingsRepository settingsRepo,
                        NotificationLogRepository logRepo) {
        this.orderRepo       = orderRepo;
        this.medicationRepo  = medicationRepo;
        this.userRepo        = userRepo;
        this.itemRepo        = itemRepo;
        this.settingsRepo    = settingsRepo;
        this.logRepo         = logRepo;
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
     * Crée une nouvelle commande pour un utilisateur donné, avec des items,
     * et génère une notification "newOrder" si activé en base.
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

        // Sauvegarde de la commande
        Order saved = orderRepo.save(order);

        // Génération de la notification newOrder
        NotificationSettings settings = settingsRepo.findById(1L)
                .orElseThrow(() -> new IllegalStateException("NotificationSettings introuvable"));
        if (settings.isNewOrder()) {
            NotificationLog log = new NotificationLog();
            log.setType("newOrder");
            log.setMessage("Nouvelle commande #" + saved.getId() + " reçue");
            log.setTimestamp(LocalDateTime.now());
            log.setSeverity("medium");
            log.setRead(false);
            logRepo.save(log);
        }

        return saved;
    }

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

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        String previousStatus = order.getStatus();
        order.setStatus(newStatus);
        Order savedOrder = orderRepo.save(order);

        // Si on vient de passer à COMPLETED, on décrémente les quantités
        if ("COMPLETED".equalsIgnoreCase(newStatus) && !"COMPLETED".equalsIgnoreCase(previousStatus)) {
            for (OrderItem item : savedOrder.getItems()) {
                Medication med = medicationRepo.findById(item.getMedication().getId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Medication not found for item " + item.getId()));
                int newQty = med.getQuantity() - item.getQuantity();
                if (newQty < 0) {
                    throw new IllegalStateException(
                            "Stock insuffisant pour le médicament " + med.getName());
                }
                med.setQuantity(newQty);
                medicationRepo.save(med);
            }
        }

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> listAllOrdersForAdmin() {
        return orderRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrderStatusDto(Long orderId, String newStatus) {
        Order updated = updateOrderStatus(orderId, newStatus);
        return toDTO(updated);
    }


    public byte[] exportOrdersCsv() {
        List<OrderDTO> orders = listAllOrdersForAdmin();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        writer.println("ID,Date,Status,UserID,Total");
        for (OrderDTO dto : orders) {
            String line = String.format(
                    "%d,%s,%s,%d,%.2f",
                    dto.getId(),
                    dto.getOrderDate(),
                    dto.getStatus(),
                    dto.getUserId(),
                    dto.getTotal()
            );
            writer.println(line);
        }

        writer.flush();
        return baos.toByteArray();
    }

    private OrderDTO toDTO(Order order) {
        Long userId = Optional.ofNullable(order.getUser())
                .map(User::getId)
                .orElseThrow(() ->
                        new IllegalStateException("Order #" + order.getId() + " sans user")
                );
        String username = order.getUser().getUsername();

        List<OrderItemDTO> items = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getMedication().getId(),
                        item.getMedication().getName(),
                        item.getQuantity(),
                        item.getMedication().getPrice()
                ))
                .collect(Collectors.toList());

        double total = items.stream()
                .mapToDouble(i -> i.getQuantity() * i.getPrice())
                .sum();

        return new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                userId,
                username,
                items,
                total
        );
    }
}
