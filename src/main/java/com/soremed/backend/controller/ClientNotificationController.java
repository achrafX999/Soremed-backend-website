// src/main/java/com/soremed/backend/controller/ClientNotificationController.java
package com.soremed.backend.controller;

import com.soremed.backend.entity.NotificationLog;
import com.soremed.backend.entity.User;
import com.soremed.backend.service.NotificationService;
import com.soremed.backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")  // ou votre domaine React
@RestController
@RequestMapping("/api/notifications")  // → => GET /api/notifications/orders
public class ClientNotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public ClientNotificationController(NotificationService notificationService,
                                        UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    /**
     * Récupérer toutes les notifications (triées par date) pour le client connecté.
     * GET /api/notifications/orders
     */
    @GetMapping("/orders")
    public List<NotificationLog> getOrderNotifications(Authentication auth) {
        User user = userService
                .getUserByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return notificationService.loadNotificationsForUser(user);
    }

    /**
     * Marquer une notification comme lue pour le client connecté.
     * PUT /api/notifications/orders/{id}/read
     */
    @PutMapping("/orders/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication auth) {
        User user = userService
                .getUserByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        notificationService.markUserNotificationAsRead(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprimer une notification pour le client connecté.
     * DELETE /api/notifications/orders/{id}
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id, Authentication auth) {
        User user = userService
                .getUserByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        notificationService.deleteUserNotification(id, user);
        return ResponseEntity.noContent().build();
    }
}
