package com.soremed.backend.controller;

import com.soremed.backend.dto.OrderDTO;
import com.soremed.backend.dto.OrderItemDTO;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.OrderItem;
import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.User;                                      // ← Import User
import com.soremed.backend.service.OrderService;
import com.soremed.backend.service.UserService;                              // ← Import UserService

import org.springframework.security.core.Authentication;                     // ← Import Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException; // ← Import Exception
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;  // ← ajout

    public OrderController(OrderService orderService,
                           UserService userService) {  // ← injection
        this.orderService = orderService;
        this.userService  = userService;
    }

    // 1. Liste de toutes les commandes (pour ADMIN/SERVICE_ACHAT)
    @GetMapping
    public List<OrderDTO> getAllOrders(Authentication auth) {
        User user = userService
                .getUserByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Order> orders = orderService.listOrdersByUser(user.getId());
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // 2. Récupérer une commande par son id
    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        return order != null ? convertToDTO(order) : null;
    }

    // 3. Créer une nouvelle commande
    @PostMapping
    public OrderDTO createOrder(@RequestParam Long userId, @RequestBody List<Map<String, Object>> items) {
        List<OrderItem> orderItems = items.stream()
            .map(itemMap -> {
                OrderItem item = new OrderItem();
                item.setQuantity((Integer) itemMap.get("quantity"));

                // Gérer les deux formats possibles
                Long medicationId = null;
                if (itemMap.containsKey("medicationId")) {
                    medicationId = ((Number) itemMap.get("medicationId")).longValue();
                } else if (itemMap.containsKey("medication")) {
                    Map<String, Object> medication = (Map<String, Object>) itemMap.get("medication");
                    medicationId = ((Number) medication.get("id")).longValue();
                }

                // Créer un médicament temporaire avec l'ID
                if (medicationId != null) {
                    Medication med = new Medication();
                    med.setId(medicationId);
                    item.setMedication(med);
                }

                return item;
            })
            .collect(Collectors.toList());

        Order order = orderService.createOrder(userId, orderItems);
        return convertToDTO(order);
    }

    @PostMapping("/{orderId}/items")
    public OrderDTO addItem(
            @PathVariable Long orderId,
            @RequestBody OrderItemDTO itemDto
    ) {
        // Appelle ton service qui gère la fusion ou l’insertion
        Order order = orderService.addOrUpdateItem(
                orderId,
                itemDto.getMedicationId(),
                itemDto.getQuantity()
        );
        return convertToDTO(order);
    }

    // 4. Mettre à jour le statut d'une commande (ADMIN/SERVICE_ACHAT)
    @PutMapping("/{id}/status")
    public OrderDTO updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderService.updateOrderStatus(id, status);
        return order != null ? convertToDTO(order) : null;
    }

    // src/main/java/com/soremed/backend/controller/OrderController.java
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);

        // Mapping des items
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setMedicationId(
                            item.getMedication() != null
                                    ? item.getMedication().getId()
                                    : null
                    );
                    // ← NOUVEAU : récupérer et transmettre le nom
                    itemDTO.setMedicationName(
                            item.getMedication() != null
                                    ? item.getMedication().getName()
                                    : null
                    );
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(
                            item.getMedication() != null
                                    ? item.getMedication().getPrice()
                                    : null
                    );
                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setItems(itemDTOs);

        // ← NOUVEAU : calculer le total de la commande
        double total = itemDTOs.stream()
                .mapToDouble(i ->
                        (i.getPrice() != null ? i.getPrice() : 0.0)
                                * (i.getQuantity() != null ? i.getQuantity() : 0)
                )
                .sum();
        dto.setTotal(total);

        return dto;
    }
}
