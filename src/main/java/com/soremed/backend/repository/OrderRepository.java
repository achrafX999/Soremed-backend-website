package com.soremed.backend.repository;

import com.soremed.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Trouver les commandes d'un utilisateur spécifique (par son id)
    List<Order> findByUserId(Long userId);

    // On pourrait aussi ajouter findByStatus(...), etc. si besoin de filtrer par statut.
}
