package com.soremed.backend.service;


import com.soremed.backend.entity.User;
import com.soremed.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder= passwordEncoder;
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        return userRepo.findByUsername(username)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()));
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
    public List<User> getAllUsers(){
        return userRepo.findAll();
    }
    public User save(User user) {
        // Encode le mot de passe avant de sauvegarder
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

}


