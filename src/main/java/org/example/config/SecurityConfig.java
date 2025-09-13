package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .authorizeHttpRequests(authz -> authz
                // Allow access to static resources and public endpoints
                .requestMatchers("/", "/index.html", "/login.html", "/register.html",
                               "/dashboard.html", "/groups.html", "/sessions.html", "/schedules.html",
                               "/api/auth/**", "/api/status", "/h2-console/**",
                               "/css/**", "/js/**", "/images/**", "/styles.css", "/script.js")
                .permitAll()
                // Require authentication for protected API endpoints
                .requestMatchers("/api/dashboard/**", "/api/groups/**", "/api/sessions/**", "/api/schedules/**")
                .authenticated()
                // Allow all other requests
                .anyRequest().permitAll()
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin() // Allow H2 console frames
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
