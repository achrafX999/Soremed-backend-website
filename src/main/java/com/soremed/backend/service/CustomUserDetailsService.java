package com.soremed.backend.service;

import com.soremed.backend.entity.User;
import com.soremed.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.soremed.backend.service.CustomUserDetails;
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

        // on renvoie notre implémentation qui expose getId()
        return new CustomUserDetails(user);
    }
}
