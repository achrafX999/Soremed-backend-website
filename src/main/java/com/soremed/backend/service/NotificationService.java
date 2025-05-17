// src/main/java/com/soremed/backend/service/NotificationService.java
package com.soremed.backend.service;

import com.soremed.backend.entity.NotificationSettings;
import com.soremed.backend.entity.NotificationLog;
import com.soremed.backend.repository.NotificationSettingsRepository;
import com.soremed.backend.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationSettingsRepository settingsRepo;
    private final NotificationLogRepository logRepo;

    public NotificationService(NotificationSettingsRepository settingsRepo,
                               NotificationLogRepository logRepo) {
        this.settingsRepo = settingsRepo;
        this.logRepo      = logRepo;
    }

    public NotificationSettings loadSettings() {
        return settingsRepo.findById(1L)
                .orElseGet(() -> {
                    NotificationSettings def = new NotificationSettings();
                    def.setId(1L);
                    return def;
                });
    }

    public NotificationSettings saveSettings(NotificationSettings s) {
        s.setId(1L);
        return settingsRepo.save(s);
    }

    public List<NotificationLog> loadRecentLogs() {
        return logRepo.findAllByOrderByTimestampDesc();
    }

    public void deleteLog(Long id) {
        logRepo.deleteById(id);
    }

    public void markAsRead(Long id) {
        NotificationLog log = logRepo.findById(id).orElseThrow();
        log.setRead(true);
        logRepo.save(log);
    }

    public void markAllAsRead() {
        logRepo.findAll().forEach(l -> {
            l.setRead(true);
            logRepo.save(l);
        });
    }
}
