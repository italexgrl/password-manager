package com.passwordmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Tymczasowa konfiguracja bezpieczeństwa dla aplikacji.
 * Na początek wyłącza ochronę CSRF i autoryzację dla wszystkich żądań,
 * aby ułatwić rozwój API. Zostanie rozbudowana w przyszłości.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Konfiguruje łańcuch filtrów bezpieczeństwa HTTP.
     * Wyłącza CSRF i autoryzuje wszystkie żądania dla celów deweloperskich.
     * Umożliwia również dostęp do konsoli H2.
     * @param http Obiekt HttpSecurity do konfiguracji bezpieczeństwa.
     * @return Skonfigurowany SecurityFilterChain.
     * @throws Exception Jeśli wystąpi błąd podczas konfiguracji.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Wyłącz CSRF dla łatwiejszego testowania REST API
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll() // Zezwól na dostęp do konsoli H2
                        .anyRequest().permitAll() // Zezwól na wszystkie inne żądania
                );
        return http.build();
    }
}