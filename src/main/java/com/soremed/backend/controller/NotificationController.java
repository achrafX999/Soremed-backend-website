// src/main/java/com/soremed/backend/controller/NotificationController.java
package com.soremed.backend.controller;

import com.soremed.backend.entity.NotificationSettings;
import com.soremed.backend.entity.NotificationLog;
import com.soremed.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/settings")
    public NotificationSettings getSettings() {
        return service.loadSettings();
    }

    @PutMapping("/settings")
    public NotificationSettings updateSettings(@RequestBody NotificationSettings settings) {
        return service.saveSettings(settings);
    }

    @GetMapping("/recent")
    public List<NotificationLog> getRecentNotifications() {
        return service.loadRecentLogs();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        service.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead() {
        service.markAllAsRead();
        return ResponseEntity.noContent().build();
    }
}
