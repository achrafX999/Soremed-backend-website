// src/main/java/com/soremed/backend/service/NotificationScheduler.java
package com.soremed.backend.service;

import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.NotificationLog;
import com.soremed.backend.entity.NotificationSettings;
import com.soremed.backend.repository.MedicationRepository;
import com.soremed.backend.repository.NotificationLogRepository;
import com.soremed.backend.repository.NotificationSettingsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationScheduler {

    private final MedicationRepository medRepo;
    private final NotificationSettingsRepository settingsRepo;
    private final NotificationLogRepository logRepo;

    public NotificationScheduler(MedicationRepository medRepo,
                                 NotificationSettingsRepository settingsRepo,
                                 NotificationLogRepository logRepo) {
        this.medRepo      = medRepo;
        this.settingsRepo = settingsRepo;
        this.logRepo      = logRepo;
    }

    /** Toutes les 5 minutes (300 000 ms) */
    @Scheduled(fixedRate = 300_000)
    public void checkLowStock() {
        NotificationSettings settings = settingsRepo.findById(1L)
                .orElseThrow(() -> new IllegalStateException("NotificationSettings manquant"));

        if (!settings.isLowStock()) {
            return; // alertes désactivées
        }

        List<Medication> lowMeds = medRepo.findByQuantityLessThanEqual(
                settings.getLowStockThreshold());

        for (Medication m : lowMeds) {
            NotificationLog log = new NotificationLog();
            log.setType("lowStock");
            log.setMessage("Stock faible : " + m.getName() +
                    " (" + m.getQuantity() + " unités)");
            log.setTimestamp(LocalDateTime.now());
            log.setSeverity("high");
            log.setRead(false);
            logRepo.save(log);
        }
    }
}
