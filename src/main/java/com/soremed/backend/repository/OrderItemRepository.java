package com.soremed.backend.repository;

import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /** Permet de trouver la ligne existante pour cette commande & ce médicament */
    Optional<OrderItem> findByOrderAndMedication(Order order, Medication medication);

    /** (Optionnel) récupérer tous les items d’une commande */
    List<OrderItem> findByOrder(Order order);
}