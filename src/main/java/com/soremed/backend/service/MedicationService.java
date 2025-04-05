package com.soremed.backend.service;


import com.soremed.backend.entity.Medication;
import com.soremed.backend.repository.MedicationRepository;
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
}
