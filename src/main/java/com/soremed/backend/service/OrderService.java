// src/main/java/com/soremed/backend/service/OrderService.java
package com.soremed.backend.service;

import com.soremed.backend.dto.OrderDTO;
import com.soremed.backend.dto.OrderItemDTO;
import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.User;
import com.soremed.backend.entity.OrderItem;
import com.soremed.backend.entity.NotificationLog;
import com.soremed.backend.entity.NotificationSettings;
import com.soremed.backend.repository.MedicationRepository;
import com.soremed.backend.repository.OrderItemRepository;
import com.soremed.backend.repository.OrderRepository;
import com.soremed.backend.repository.UserRepository;
import com.soremed.backend.repository.NotificationLogRepository;
import com.soremed.backend.repository.NotificationSettingsRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository                 orderRepo;
    private final MedicationRepository            medicationRepo;
    private final UserRepository                  userRepo;
    private final OrderItemRepository             itemRepo;
    private final NotificationSettingsRepository  settingsRepo;
    private final NotificationLogRepository       logRepo;
    private final NotificationService              notificationService; // ← ajouté

    public OrderService(
            OrderRepository orderRepo,
            MedicationRepository medicationRepo,
            UserRepository userRepo,
            OrderItemRepository itemRepo,
            NotificationSettingsRepository settingsRepo,
            NotificationLogRepository logRepo,
            NotificationService notificationService    // ← injecté ici
    ) {
        this.orderRepo      = orderRepo;
        this.medicationRepo = medicationRepo;
        this.userRepo       = userRepo;
        this.itemRepo       = itemRepo;
        this.settingsRepo   = settingsRepo;
        this.logRepo        = logRepo;
        this.notificationService = notificationService; // ← initialisation
    }

    /**
     * Récupère toutes les commandes (pour l’admin).
     */
    public List<Order> listAllOrders() {
        return orderRepo.findAll();
    }

    /**
     * Récupère les commandes d’un utilisateur donné.
     */
    public List<Order> listOrdersByUser(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }
        return orderRepo.findByUserId(userId);
    }

    /**
     * Récupère une commande par son ID.
     */
    public Order getOrder(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    /**
     * Crée une nouvelle commande pour un utilisateur, avec la liste d’items fournie.
     * Si les notifications "newOrder" sont activées dans NotificationSettings, crée une NotificationLog.
     */
    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items) {
        // 1) Récupérer le User qui passe la commande
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        Order order = new Order();
        order.setUser(user);

        for (OrderItem item : items) {
            Long medId = Optional.ofNullable(item.getMedication())
                    .map(Medication::getId)
                    .orElseThrow(() -> new IllegalArgumentException("Medication ID is required"));
            Medication med = medicationRepo.findById(medId)
                    .orElseThrow(() -> new EntityNotFoundException("Medication not found: " + medId));
            OrderItem newItem = new OrderItem(order, med, item.getQuantity());
            order.addItem(newItem);
        }

        Order savedOrder = orderRepo.save(order);

        // 2) Génération de la notification "newOrder"
        NotificationSettings settings = settingsRepo.findById(1L)
                .orElseThrow(() -> new IllegalStateException("NotificationSettings introuvable"));
        if (settings.isNewOrder()) {
            NotificationLog log = new NotificationLog();
            log.setType("newOrder");
            log.setMessage("Nouvelle commande #" + savedOrder.getId() + " reçue");
            log.setTimestamp(LocalDateTime.now());
            log.setSeverity("medium");
            log.setRead(false);

            // ← Ici on doit OBLIGATOIREMENT assigner le user de la notification
            log.setUser(user);  // on lie la notification au client qui a passé la commande

            logRepo.save(log);
        }

        return savedOrder;
    }

    /**
     * Ajoute ou met à jour un OrderItem dans une commande existante.
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

        return orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found after update: " + orderId));
    }

    /**
     * Met à jour le statut d’une commande. Si on passe au statut COMPLETED,
     * on décrémente les quantités de chaque médicament. Puis, si les notifications
     * "orderStatusChange" sont activées, crée une NotificationLog correspondant.
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        // 1. Récupérer la commande et stocker l’ancien statut
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        String previousStatus = order.getStatus();
        order.setStatus(newStatus);
        Order savedOrder = orderRepo.save(order);

        // 2. Si on vient de passer à COMPLETED, ajuster le stock
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

        // 3. Générer la notification de changement de statut (via NotificationService)
        NotificationSettings settings = settingsRepo.findById(1L)
                .orElseThrow(() -> new IllegalStateException("NotificationSettings introuvable"));

        if (settings.isOrderStatusChange()) {
            // Appel au service de notification, qui crée la NotificationLog et l’associe à l’utilisateur
            notificationService.createNotificationForOrderStatus(
                    savedOrder,
                    previousStatus,
                    newStatus
            );
        }

        return savedOrder;
    }

    /**
     * Pour l’admin : liste toutes les commandes au format DTO.
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> listAllOrdersForAdmin() {
        return orderRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour le statut et retourne la commande au format DTO.
     */
    @Transactional
    public OrderDTO updateOrderStatusDto(Long orderId, String newStatus) {
        Order updated = updateOrderStatus(orderId, newStatus);
        return toDTO(updated);
    }

    /**
     * Exporte toutes les commandes (pour l’admin) au format CSV.
     */
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

    /**
     * Conversion interne d’une entité Order en OrderDTO.
     */
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
