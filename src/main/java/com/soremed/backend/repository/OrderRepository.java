package com.soremed.backend.repository;

import com.soremed.backend.dto.OrderStatusDTO;
import com.soremed.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Trouver les commandes d'un utilisateur spécifique (par son id)
    List<Order> findByUserId(Long userId);

    @Query("""
  SELECT new com.soremed.backend.dto.OrderStatusDTO(o.status, COUNT(o))
  FROM Order o
  WHERE o.user.id = :clientId
  GROUP BY o.status
""")
    List<OrderStatusDTO> countByStatus(@Param("clientId") Long clientId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :clientId AND o.status = 'In Progress'")
    long countInProgressByClient(@Param("clientId") Long clientId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :clientId AND o.status = 'Completed'")
    long countCompletedByClient(@Param("clientId") Long clientId);
}
