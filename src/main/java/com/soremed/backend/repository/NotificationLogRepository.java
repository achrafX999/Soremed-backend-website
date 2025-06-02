// src/main/java/com/soremed/backend/repository/NotificationLogRepository.java
package com.soremed.backend.repository;

import com.soremed.backend.entity.NotificationLog;
import com.soremed.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findAllByOrderByTimestampDesc();
    // Nouvelle méthode pour récupérer les notifications d’un utilisateur
    List<NotificationLog> findByUserOrderByTimestampDesc(User user);
}
