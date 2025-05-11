// src/main/java/com/soremed/backend/repository/NewsRepository.java
package com.soremed.backend.repository;

import com.soremed.backend.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<News,Long> {
    List<News> findAllByOrderByDateDesc();
}
