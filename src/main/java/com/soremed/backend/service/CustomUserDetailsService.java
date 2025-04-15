package com.soremed.backend.service;

import com.soremed.backend.entity.User;
import com.soremed.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/* Cette classe ira chercher l’utilisateur via ton repository et transformera ton entité User en un objet
 UserDetails que Spring Security utilisera pour l’authentification.
*/
@Service

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Recherche dans la base de données via le repository
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Retourne un UserDetails construit à partir de l'entité utilisateur
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole()) // Assure-toi que ton rôle est au format attendu (par exemple, "ADMIN" ou "USER")
                .build();
    }
}
