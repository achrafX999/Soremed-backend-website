package com.soremed.backend.service;
import com.soremed.backend.entity.Medication;
import com.soremed.backend.repository.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {

    @Mock
    private MedicationRepository repo;              // << faux repo

    @InjectMocks
    private MedicationService service;              // << service réel

    @Test
    void decreaseStock_shouldUpdateQuantity() {
        // given
        Medication doliprane = new Medication("Doliprane", "Antalgique", 10.0, 100,
                "500 mg", "comprimé", "Sanofi");
        when(repo.findById(1L)).thenReturn(Optional.of(doliprane));

        // when
        service.updateQuantity(1L, 3);               // –3 unités

        // then
        assertThat(doliprane.getQuantity()).isEqualTo(97);
        verify(repo).save(doliprane);               // vérifie l'appel au repo
    }
}