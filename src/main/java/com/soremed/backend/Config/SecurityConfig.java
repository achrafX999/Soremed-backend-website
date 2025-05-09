// src/main/java/com/soremed/backend/Config/SecurityConfig.java
package com.soremed.backend.Config;

import com.soremed.backend.service.CustomUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                        .requestMatchers("/api/users/register", "/api/login").permitAll()
                        .requestMatchers("/api/medications/**", "/api/orders/**")
                        .hasAnyRole("ADMIN", "SERVICE_ACHAT", "CLIENT")
                        .anyRequest().authenticated()
                )
                // Form‐based login pointé sur votre endpoint custom
                .formLogin(form -> form
                        .loginProcessingUrl("/api/login")            // POST /api/login
                        .successHandler((req,res,auth) -> res.setStatus(HttpStatus.OK.value()))
                        .failureHandler((req,res,exc) -> res.sendError(HttpStatus.UNAUTHORIZED.value()))
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults())
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