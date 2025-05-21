package com.soremed.backend.controller;

import com.soremed.backend.dto.MedicationDTO;                // ← import DTO
import com.soremed.backend.entity.Medication;
import com.soremed.backend.service.MedicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    // ── 1) CRUD & liste entités (inchangé) ─────────────────────────────────────

    @GetMapping
    public Page<MedicationDTO> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0")  int    minQuantity,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "12") int    size
    ) {
        Pageable pg = PageRequest.of(page, size, Sort.by("name"));
        return medicationService.searchMedications(search, minQuantity, pg);
    }

    @GetMapping("/{id}")
    public Medication getMedicationById(@PathVariable Long id) {
        return medicationService.getMedication(id);
    }

    @GetMapping("/new")
    public List<Medication> getNewMedications() {
        return medicationService.getNewMedications();
    }

    @PostMapping
    public Medication createMedication(@RequestBody Medication med) {
        return medicationService.createMedication(med);
    }

    @PutMapping("/{id}")
    public Medication updateMedication(@PathVariable Long id, @RequestBody Medication med) {
        return medicationService.updateMedication(id, med);
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<Medication> updateQuantity(
            @PathVariable Long id,
            @RequestParam int quantity
    ) {
        Medication updated = medicationService.updateQuantity(id, quantity);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteMedication(@PathVariable Long id) {
        medicationService.deleteMedication(id);
    }

    // ── 2) NOUVEAU : endpoint paginé qui renvoie des DTO ────────────────────────

    /**
     * GET /api/medications/search
     *   - name       (String) optional, default ""
     *   - minQuantity(int)    optional, default 0
     *   - page       (int)    optional, default 0
     *   - size       (int)    optional, default 12
     *
     * Retourne une Page<MedicationDTO> contenant uniquement les champs utiles.
     */
    @GetMapping("/search")
    public Page<MedicationDTO> search(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0")  int    minQuantity,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "12") int    size
    ) {
        Pageable pg = PageRequest.of(page, size);
        return medicationService.searchMedications(name, minQuantity, pg);
    }
}
