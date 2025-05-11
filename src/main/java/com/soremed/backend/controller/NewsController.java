// src/main/java/com/soremed/backend/controller/NewsController.java
package com.soremed.backend.controller;

import com.soremed.backend.entity.News;
import com.soremed.backend.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/api/news")
public class NewsController {
    private final NewsService service;
    public NewsController(NewsService service) { this.service = service; }

    // 1. Liste publique
    @GetMapping
    public List<News> list() {
        return service.listAll();
    }

    // 2. Création (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public News create(@RequestBody News news) {
        return service.create(news);
    }

    // 3. MAJ (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public News update(@PathVariable Long id, @RequestBody News news) {
        return service.update(id, news);
    }

    // 4. Suppression (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
