package com.soremed.backend.repository;

import com.soremed.backend.dto.ClientTopProductDTO;
import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /** Permet de trouver la ligne existante pour cette commande & ce médicament */
    Optional<OrderItem> findByOrderAndMedication(Order order, Medication medication);

    /** (Optionnel) récupérer tous les items d’une commande */
    List<OrderItem> findByOrder(Order order);

    @Query("""
  SELECT new com.soremed.backend.dto.ClientTopProductDTO(
    m.name, SUM(i.quantity)
  )
  FROM OrderItem i
    JOIN i.order o
    JOIN i.medication m
  WHERE o.user.id = :clientId
  GROUP BY m.name
  ORDER BY SUM(i.quantity) DESC
""")
    List<ClientTopProductDTO> findTopProductsByClient(
            @Param("clientId") Long clientId,
            Pageable limit
    );
}