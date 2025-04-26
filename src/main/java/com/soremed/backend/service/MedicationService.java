package com.soremed.backend.service;


import com.soremed.backend.entity.Medication;
import com.soremed.backend.repository.MedicationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MedicationService {
    private final MedicationRepository medicationRepo;

    public MedicationService(MedicationRepository medicationRepo) {
        this.medicationRepo = medicationRepo;
    }

    public List<Medication> listAllMedications(String search) {
        if (search != null && !search.isEmpty()) {
            // Si un terme de recherche est fourni, on utilise la méthode personnalisée
            return medicationRepo.findByNameContaining(search);
        } else {
            return medicationRepo.findAll();
        }
    }

    public Medication getMedication(Long id) {
        return medicationRepo.findById(id).orElse(null);
    }

    public Medication createMedication(Medication med) {
        return medicationRepo.save(med);
    }

    public Medication updateMedication(Long id, Medication med) {
        med.setId(id);
        return medicationRepo.save(med);
    }

    public void deleteMedication(Long id) {
        medicationRepo.deleteById(id);
    }

    public List<Medication> getNewMedications() {
        // Utilise la méthode du repository pour récupérer les 10 derniers enregistrement
        return medicationRepo.findTop10ByOrderByIdDesc();
    }

    /**
     * Recherche paginée :
     * - name : mot‑clé (vide = tous)
     * - minQty : quantité minimale (0 = aucun filtre)
     * - page  : index 0-based
     * - size  : nb d’éléments par page
     */
    public Page<Medication> search(
            String name,
            int minQty,
            int page,
            int size
    ) {
        return medicationRepo.findByNameContainingIgnoreCaseAndQuantityGreaterThanEqual(
                name == null ? "" : name,
                minQty < 0 ? 0 : minQty,
                PageRequest.of(page, size, Sort.by("name"))
        );
    }

    // Met à jour la quantité d'un médicament existant
    public Medication updateQuantity(Long id, int quantity) {
        Medication med = medicationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found: " + id));
        med.setQuantity(quantity);
        return medicationRepo.save(med);
    }
}
