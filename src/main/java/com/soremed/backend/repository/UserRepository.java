package com.soremed.backend.repository;

import com.soremed.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // éventuellement, pour l'auth:
    Optional<User> findByUsernameAndPassword(String username, String password);
}
