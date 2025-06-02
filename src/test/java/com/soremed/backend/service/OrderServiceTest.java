package com.soremed.backend.service;

import com.soremed.backend.entity.*;
import com.soremed.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepo;
    @Mock MedicationRepository medRepo;
    @Mock UserRepository userRepo;
    @Mock OrderItemRepository itemRepo;
    @Mock NotificationSettingsRepository settingsRepo;
    @Mock NotificationLogRepository logRepo;

    @InjectMocks
    OrderService service;

    Order order;
    Medication med;
    OrderItem item;

    @BeforeEach
    void init() {
        // Création d'une commande existante avec un item
        order = new Order();
        order.setId(99L);
        order.setStatus("EN_COURS");

        med = new Medication("TestMed", "desc", 5.0, 20, "", "", "");
        med.setId(5L);

        item = new OrderItem(order, med, 2);
        // Lier l'item à la commande
        order.addItem(item);
    }

    @Test
    void updateOrderStatus_generatesNotification_whenToggleEnabled() {
        // 1. Stub de la commande existante
        when(orderRepo.findById(99L)).thenReturn(Optional.of(order));
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            // simule le retour de la commande "sauvegardée"
            o.setStatus(o.getStatus());
            return o;
        });

        // 2. Stub du médicament (pour la décrémentation si COMPLETED)
        when(medRepo.findById(5L)).thenReturn(Optional.of(med));
        when(medRepo.save(any(Medication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 3. Activation du toggle "orderStatusChange"
        NotificationSettings settings = new NotificationSettings();
        settings.setId(1L);
        settings.setOrderStatusChange(true);
        when(settingsRepo.findById(1L)).thenReturn(Optional.of(settings));

        // 4. Appel de la méthode avec passage du statut EN_COURS → COMPLETED
        Order result = service.updateOrderStatus(99L, "COMPLETED");

        // 5. Vérifications
        assertThat(result.getStatus()).isEqualTo("COMPLETED");

        // a) On a décrémenté le stock : 20 - 2 = 18
        assertThat(med.getQuantity()).isEqualTo(18);
        verify(medRepo).save(med);

        // b) On a bien enregistré une notification de type "orderStatusChange"
        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepo).save(captor.capture());

        NotificationLog logged = captor.getValue();
        assertThat(logged.getType()).isEqualTo("orderStatusChange");
        assertThat(logged.getMessage()).contains("Commande #99")
                .contains("EN_COURS")
                .contains("COMPLETED");
        assertThat(logged.isRead()).isFalse();
        assertThat(logged.getTimestamp()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    void updateOrderStatus_throws_whenOrderNotFound() {
        when(orderRepo.findById(123L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.updateOrderStatus(123L, "SHIPPED"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Order not found: 123");
    }
}
