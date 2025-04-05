package com.soremed.backend.controller;

import com.soremed.backend.entity.Medication;
import com.soremed.backend.service.MedicationService;
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

    // 1. Récupérer la liste de tous les médicaments, avec option de recherche par nom
    @GetMapping
    public List<Medication> getAllMedications(@RequestParam(name="search", required=false) String search) {
        return medicationService.listAllMedications(search);
    }

    // 2. Récupérer un médicament par son ID
    @GetMapping("/{id}")
    public Medication getMedicationById(@PathVariable Long id) {
        return medicationService.getMedication(id);
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

    // 5. Supprimer un médicament (ADMIN)
    @DeleteMapping("/{id}")
    public void deleteMedication(@PathVariable Long id) {
        medicationService.deleteMedication(id);
    }
}
