// src/main/java/com/soremed/backend/service/NewsService.java
package com.soremed.backend.service;

import com.soremed.backend.entity.News;
import com.soremed.backend.repository.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NewsService {
    private final NewsRepository repo;
    public NewsService(NewsRepository repo) { this.repo = repo; }

    @Transactional(readOnly=true)
    public List<News> listAll() {
        return repo.findAllByOrderByDateDesc();
    }

    @Transactional
    public News create(News news) {
        return repo.save(news);
    }

    @Transactional
    public News update(Long id, News news) {
        News existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("News not found"));
        existing.setTitle(news.getTitle());
        existing.setDescription(news.getDescription());
        existing.setCategory(news.getCategory());
        existing.setImageUrl(news.getImageUrl());
        existing.setDate(news.getDate());
        return repo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
