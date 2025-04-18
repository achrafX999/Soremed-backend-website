package com.soremed.backend.repository;

import com.soremed.backend.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    // Méthode de recherche par nom (utilise la convention de requête dérivée)
    List<Medication> findByNameContaining(String keyword);
    // Récupère les 10 derniers médicaments ajoutés, triés par id en ordre décroissant.
    List<Medication> findTop10ByOrderByIdDesc();
}