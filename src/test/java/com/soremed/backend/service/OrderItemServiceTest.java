package com.soremed.backend.service;

import com.soremed.backend.entity.Medication;
import com.soremed.backend.entity.Order;
import com.soremed.backend.entity.OrderItem;
import com.soremed.backend.repository.MedicationRepository;
import com.soremed.backend.repository.OrderItemRepository;
import com.soremed.backend.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock OrderItemRepository itemRepo;
    @Mock OrderRepository     orderRepo;
    @Mock MedicationRepository medRepo;

    @InjectMocks
    OrderItemService service;

    Order order;
    Medication med;

    @BeforeEach
    void init() {
        order = new Order();
        order.setId(10L);
        med   = new Medication("Aspirine", "desc", 5.0, 50, "100mg", "comprimé", "Bayer");
        med.setId(20L);
    }

    @Test
    void addItem_createsNew_whenNoConflict() {
        when(orderRepo.findById(10L)).thenReturn(Optional.of(order));
        when(medRepo.findById(20L)).thenReturn(Optional.of(med));

        OrderItem saved = new OrderItem(order, med, 5);
        when(itemRepo.save(any(OrderItem.class))).thenReturn(saved);

        OrderItem result = service.addItem(10L, 20L, 5);

        assertThat(result.getQuantity()).isEqualTo(5);
        verify(itemRepo).save(any(OrderItem.class));
    }

    @Test
    void addItem_updatesExisting_whenConstraintViolation() {
        when(orderRepo.findById(10L)).thenReturn(Optional.of(order));
        when(medRepo.findById(20L)).thenReturn(Optional.of(med));

        // Simule une violation SQL à la première insertion
        OrderItem existing = new OrderItem(order, med, 3);
        // 1er save -> exception, 2e save -> existing mis à jour
        when(itemRepo.save(any(OrderItem.class)))
        .thenThrow(DataIntegrityViolationException.class)
                .thenReturn(existing);
            when(itemRepo.findByOrderAndMedication(order, med))
                .thenReturn(Optional.of(existing));

        OrderItem result = service.addItem(10L, 20L, 2);

        // quantité initiale 3 + 2 = 5
        assertThat(result.getQuantity()).isEqualTo(5);
        verify(itemRepo, times(2)).save(any(OrderItem.class));
    }

    @Test
    void addItem_throws_whenOrderNotFound() {
        when(orderRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.addItem(99L, 20L, 1))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
