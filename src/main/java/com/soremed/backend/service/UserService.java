package com.soremed.backend.service;


import com.soremed.backend.entity.User;
import com.soremed.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> authenticate(String username, String password) {
        // Recherche l'utilisateur correspondant aux identifiants
        return userRepo.findByUsernameAndPassword(username, password);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User getUser(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    // (On n'implémente pas de createUser ni updateUser via API publique
    //  car les comptes sont gérés par le système central.
    //  Néanmoins, on pourrait en ajouter pour un admin si nécessaire.)
}


