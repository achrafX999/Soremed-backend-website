// src/main/java/com/soremed/backend/service/NotificationScheduler.java
package com.soremed.backend.service;

import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.NotificationLog;
import com.soremed.backend.entity.NotificationSettings;
import com.soremed.backend.entity.User;
import com.soremed.backend.repository.MedicationRepository;
import com.soremed.backend.repository.NotificationLogRepository;
import com.soremed.backend.repository.NotificationSettingsRepository;
import com.soremed.backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationScheduler {

    private final MedicationRepository medicationRepo;
    private final NotificationSettingsRepository settingsRepo;
    private final NotificationLogRepository logRepo;
    private final UserRepository userRepo;

    public NotificationScheduler(
            MedicationRepository medicationRepo,
            NotificationSettingsRepository settingsRepo,
            NotificationLogRepository logRepo,
            UserRepository userRepo
    ) {
        this.medicationRepo = medicationRepo;
        this.settingsRepo = settingsRepo;
        this.logRepo = logRepo;
        this.userRepo = userRepo;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkLowStock() {
        NotificationSettings settings = settingsRepo.findById(1L)
                .orElseThrow(() -> new IllegalStateException("NotificationSettings introuvable"));
        if (!settings.isLowStock()) {
            return;
        }

        List<Medication> lowStockMeds = medicationRepo.findByQuantityLessThanEqual(settings.getLowStockThreshold());
        if (lowStockMeds.isEmpty()) {
            return;
        }

        // Récupérer l’utilisateur “système” (admin ou bot) dont l’id = 1
        User systemUser = userRepo.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Utilisateur système introuvable (id=1)"));

        for (Medication med : lowStockMeds) {
            NotificationLog log = new NotificationLog();
            log.setType("lowStock");
            log.setMessage("Attention, stock faible pour le médicament : " + med.getName());
            log.setTimestamp(LocalDateTime.now());
            log.setSeverity("high");
            log.setRead(false);

            // ← On assigne impérativement un 'user' non nul
            log.setUser(systemUser);

            logRepo.save(log);
        }
    }
}

