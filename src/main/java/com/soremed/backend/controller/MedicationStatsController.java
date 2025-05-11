package com.soremed.backend.controller;

import com.soremed.backend.service.MedicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// src/main/java/com/soremed/backend/controller/MedicationStatsController.java
@RestController
@RequestMapping("/api/medications/stats")
public class MedicationStatsController {
    private final MedicationService service;
    public MedicationStatsController(MedicationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<MedicationService.MedStats> getStats() {
        return ResponseEntity.ok(service.computeStatsForCurrentMonth());
    }
}
