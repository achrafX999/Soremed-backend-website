// src/main/java/com/soremed/backend/service/NotificationService.java
package com.soremed.backend.service;

import com.soremed.backend.entity.NotificationLog;
import com.soremed.backend.entity.NotificationSettings;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.User;
import com.soremed.backend.repository.NotificationLogRepository;
import com.soremed.backend.repository.NotificationSettingsRepository;
import com.soremed.backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationSettingsRepository settingsRepo;
    private final NotificationLogRepository logRepo;
    private final UserRepository userRepo;

    public NotificationService(NotificationSettingsRepository settingsRepo,
                               NotificationLogRepository logRepo,
                               UserRepository userRepo) {
        this.settingsRepo = settingsRepo;
        this.logRepo = logRepo;
        this.userRepo = userRepo;
    }

    // ─── Gestion des paramètres (admin) ────────────────────────────────────────

    public NotificationSettings loadSettings() {
        // L’ID est toujours 1 (singleton row)
        Optional<NotificationSettings> opt = settingsRepo.findById(1L);
        return opt.orElseGet(() -> {
            NotificationSettings s = new NotificationSettings();
            s.setId(1L);
            return settingsRepo.save(s);
        });
    }

    public NotificationSettings saveSettings(NotificationSettings settings) {
        settings.setId(1L);
        return settingsRepo.save(settings);
    }

    // ─── Gestion du log (admin) ────────────────────────────────────────────────

    public List<NotificationLog> loadRecentLogs() {
        // Retourne TOUTES les notifications pour tous les utilisateurs, triées par date
        return logRepo.findAllByOrderByTimestampDesc();
    }

    public void deleteLog(Long id) {
        logRepo.deleteById(id);
    }

    @Transactional
    public void markAsRead(Long id) {
        NotificationLog log = logRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("NotificationLog not found"));
        log.setRead(true);
        logRepo.save(log);
    }

    @Transactional
    public void markAllAsRead() {
        List<NotificationLog> all = logRepo.findAll();
        all.forEach(n -> n.setRead(true));
        logRepo.saveAll(all);
    }

    // ─── Nouvelle partie : notifications “client” ───────────────────────────────

    /**
     * Enregistrer en base une notification de changement de statut pour une commande.
     * @param order    la commande dont le statut a changé
     * @param oldStatus  ancien statut
     * @param newStatus  nouveau statut
     */
    @Transactional
    public void createNotificationForOrderStatus(Order order, String oldStatus, String newStatus) {
        User orderOwner = order.getUser();
        if (orderOwner == null) {
            return; // Pas de propriétaire => pas de notification
        }

        // Charger les préférences pour ce type de notification
        NotificationSettings settings = loadSettings();
        if (!settings.isOrderStatusChange()) {
            // Si les notifications de changement de statut sont désactivées, on ne fait rien
            return;
        }

        // Créer l’élément NotificationLog
        NotificationLog nl = new NotificationLog();
        nl.setUser(orderOwner);
        nl.setType("orderStatus");
        nl.setSeverity("info");

        String message = String.format(
                "Votre commande #%d est passée de « %s » à « %s ».",
                order.getId(), oldStatus, newStatus
        );
        nl.setMessage(message);

        logRepo.save(nl);
    }

    /**
     * Récupérer toutes les notifications (non lues + lues) pour un utilisateur donné, triées par date décroissante.
     * @param user l’utilisateur connecté
     */
    public List<NotificationLog> loadNotificationsForUser(User user) {
        return logRepo.findByUserOrderByTimestampDesc(user);
    }

    /**
     * Marquer une notification spécifique comme lue, pour un utilisateur. (côté client)
     */
    @Transactional
    public void markUserNotificationAsRead(Long notificationId, User user) {
        NotificationLog log = logRepo.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (! log.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Vous n’avez pas le droit de modifier cette notification");
        }
        log.setRead(true);
        logRepo.save(log);
    }

    /**
     * Supprimer une notification spécifique, pour un utilisateur. (côté client)
     */
    @Transactional
    public void deleteUserNotification(Long notificationId, User user) {
        NotificationLog log = logRepo.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (! log.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Vous n’avez pas le droit de supprimer cette notification");
        }
        logRepo.delete(log);
    }
}
