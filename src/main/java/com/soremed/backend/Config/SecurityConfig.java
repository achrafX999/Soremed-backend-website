// src/main/java/com/soremed/backend/Config/SecurityConfig.java
package com.soremed.backend.Config;

import com.soremed.backend.service.CustomUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // 1) Autorisation ouverte pour l'enregistrement et le login
                        .requestMatchers("/api/users/register", "/api/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/news/**").permitAll()

                        // 2) Seul ADMIN peut accéder aux notifications admin
                        .requestMatchers("/api/admin/notifications/**").hasRole("ADMIN")

                        // 3) Autres API nécessitent les rôles listés
                        .requestMatchers("/api/medications/**", "/api/orders/**")
                        .hasAnyRole("ADMIN", "SERVICE_ACHAT", "CLIENT")

                        // 4) Toute autre requête authentifiée
                        .anyRequest().authenticated()
                )
                // Form‐based login sur /api/login
                .formLogin(form -> form
                        .loginProcessingUrl("/api/login")
                        .successHandler((req, res, auth) -> res.setStatus(HttpStatus.OK.value()))
                        .failureHandler((req, res, exc) -> res.sendError(HttpStatus.UNAUTHORIZED.value()))
                        .permitAll()
                )
                // Autoriser HTTP Basic (optionnel)
                .httpBasic(Customizer.withDefaults())
                // Déconnexion
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailsService(CustomUserDetailsService custom) {
        return custom;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
