package com.soremed.backend.service;

import com.soremed.backend.dto.MedicationDTO;
import com.soremed.backend.entity.Medication;
import com.soremed.backend.repository.MedicationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicationService {
    private final MedicationRepository medicationRepo;

    public MedicationService(MedicationRepository medicationRepo) {
        this.medicationRepo = medicationRepo;
    }

    // —————————————————————————————————————————————————————
    // 1️⃣ CRUD classique (listAll, get, create, update, delete)
    // —————————————————————————————————————————————————————

    public List<Medication> listAllMedications(String search) {
        if (search != null && !search.isEmpty()) {
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
        return medicationRepo.findTop10ByOrderByIdDesc();
    }

    public Medication updateQuantity(Long id, int amount) {
        Medication med = medicationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medication not found: " + id));

        int currentQty = med.getQuantity();
        if (amount > currentQty) {
            throw new IllegalArgumentException("Stock insuffisant pour l’id : " + id);
        }

        med.setQuantity(currentQty - amount);
        return medicationRepo.save(med);
    }


    // —————————————————————————————————————————————————————
    // 2️⃣ Méthode de recherche paginée RENVOYANT DES DTO
    // —————————————————————————————————————————————————————

    /**
     * Recherche paginée en DTO :
     * @param name    chaîne à chercher (vide = tous)
     * @param minQty  quantité minimale (0 = aucun filtre)
     * @param pageable info de pagination (page, size, sort)
     * @return Page<MedicationDTO>
     */
    public Page<MedicationDTO> searchMedications(String name, int minQty, Pageable pageable) {
        // Appelle directement la requête projection du repository
        return medicationRepo.searchMedications(
                name == null ? "" : name,
                minQty < 0 ? 0 : minQty,
                pageable
        );
    }

    // —————————————————————————————————————————————————————
    // 3️⃣ Statistiques mensuelles (inchangé)
    // —————————————————————————————————————————————————————

    public record MedStats(
            long newProductsThisMonth,
            long inventoryUpdatesThisMonth,
            long priceChangesThisMonth
    ) {}

    @Transactional(readOnly = true)
    public MedStats computeStatsForCurrentMonth() {
        LocalDateTime start = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        long newProducts = medicationRepo.countByCreatedAtBetween(start, end);

        List<Medication> updated = medicationRepo.findAllByUpdatedAtBetween(start, end);

        long invUpdates = updated.stream()
                .filter(m -> m.getQuantity() != m.get_previousQuantity())
                .count();

        long priceUpdates = updated.stream()
                .filter(m -> m.getPrice() != m.get_previousPrice())
                .count();

        return new MedStats(newProducts, invUpdates, priceUpdates);
    }
}
