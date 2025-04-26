package com.soremed.backend.controller;

import com.soremed.backend.entity.Medication;
import com.soremed.backend.service.MedicationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")  // autorise les appels du front dev
@RestController
@RequestMapping("/api/medications")
public class MedicationController {
    private final MedicationService medicationService;
    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    /**
     * GET /api/medications
     * params :
     *   - search      (String, optionnel, défaut = "")
     *   - minQuantity (int,    optionnel, défaut = 0)
     *   - page        (int,    optionnel, défaut = 0)
     *   - size        (int,    optionnel, défaut = 12)
     */
    @GetMapping
    public Page<Medication> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int minQuantity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return medicationService.search(search, minQuantity, page, size);
    }

    // 2. Récupérer un médicament par son ID
    @GetMapping("/{id}")
    public Medication getMedicationById(@PathVariable Long id) {
        return medicationService.getMedication(id);
    }

    @GetMapping("/new")
    public List<Medication> getNewMedications() {
        // Retourner les médicaments triés par date décroissante, avec une limitation (ex. 10 derniers)
        return medicationService.getNewMedications();
    }

    // 3. Ajouter un nouveau médicament (ADMIN)
    @PostMapping
    public Medication createMedication(@RequestBody Medication med) {
        // Dans un contexte réel, on vérifierait que l'utilisateur connecté a le rôle ADMIN
        return medicationService.createMedication(med);
    }

    // 4. Mettre à jour un médicament existant (ADMIN)
    @PutMapping("/{id}")
    public Medication updateMedication(@PathVariable Long id, @RequestBody Medication med) {
        return medicationService.updateMedication(id, med);
    }

    // Endpoint de mise à jour partielle de la quantité
    @PatchMapping("/{id}/quantity")
    public ResponseEntity<Medication> updateQuantity(
            @PathVariable Long id,
            @RequestParam int quantity
    ) {
        Medication updated = medicationService.updateQuantity(id, quantity);
        return ResponseEntity.ok(updated);
    }

    // 5. Supprimer un médicament (ADMIN)
    @DeleteMapping("/{id}")
    public void deleteMedication(@PathVariable Long id) {
        medicationService.deleteMedication(id);
    }
}
