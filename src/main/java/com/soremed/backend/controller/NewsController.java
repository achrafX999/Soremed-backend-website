// src/main/java/com/soremed/backend/controller/NewsController.java
package com.soremed.backend.controller;

import com.soremed.backend.entity.News;
import com.soremed.backend.service.NewsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@CrossOrigin(origins = {"http://localhost:3000","http://localhost:5173"})
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService service;

    @Value("${news.images.dir}")
    private String imagesDir;

    public NewsController(NewsService service) {
        this.service = service;
    }

    // Liste publique
    @GetMapping
    public List<News> list() {
        return service.listAll();
    }

    // Création (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public News create(@RequestBody News news) {
        return service.create(news);
    }

    // MAJ (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public News update(@PathVariable Long id, @RequestBody News news) {
        return service.update(id, news);
    }

    // Suppression (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Upload d’une image de news */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(
            path     = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Fichier vide"));
        }
        String filename = UUID.randomUUID() + "_" +
                Paths.get(file.getOriginalFilename()).getFileName();
        Path target = Paths
                .get(imagesDir)
                .toAbsolutePath()
                .normalize()
                .resolve(filename);
        Files.createDirectories(target.getParent());
        file.transferTo(target.toFile());

        String url = "/images/news/" + filename;
        return ResponseEntity.ok(Map.of("imageUrl", url));
    }
}
