package com.soremed.backend.repository;

import com.soremed.backend.entity.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    // Méthode de recherche par nom (utilise la convention de requête dérivée)
    List<Medication> findByNameContaining(String keyword);

    // Récupère les 10 derniers médicaments ajoutés, triés par id en ordre décroissant.
    List<Medication> findTop10ByOrderByIdDesc();

    // Recherche insensible à la casse + quantité min, avec pagination
    Page<Medication> findByNameContainingIgnoreCaseAndQuantityGreaterThanEqual(
            String name,
            int minQuantity,
            Pageable pageable
    );

    // Statistiques du mois courant : nouveaux produits
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Liste des mises à jour (stock & prix) du mois courant
    List<Medication> findAllByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Medication> findByQuantityLessThanEqual(int threshold);
}
