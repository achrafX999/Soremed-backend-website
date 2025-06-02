package com.soremed.backend.service;

import com.soremed.backend.entity.User;
import com.soremed.backend.enums.Role;
import com.soremed.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepo;
    @Mock PasswordEncoder encoder;

    @InjectMocks
    UserService service;

    @Test
    void authenticate_returnsUser_whenPasswordMatches() {
        User user = new User();
        user.setUsername("u");
        user.setPassword("hash");
        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(encoder.matches("raw", "hash")).thenReturn(true);

        Optional<User> result = service.authenticate("u", "raw");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("u");
    }

    @Test
    void changeRole_updatesRole_orThrows() {
        User user = new User();
        user.setId(7L);
        user.setRole(Role.CLIENT);
        when(userRepo.findById(7L)).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);

        User updated = service.changeRole(7L, Role.SERVICE_ACHAT);
        assertThat(updated.getRole()).isEqualTo(Role.SERVICE_ACHAT);

        // cas d’erreur si ID inconnu
        when(userRepo.findById(8L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.changeRole(8L, Role.ADMIN))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
