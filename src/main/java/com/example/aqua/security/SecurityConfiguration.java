package com.example.aqua.security;


import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ========================================
            // CORS Configuration - MUST be first
            // ========================================
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // ========================================
            // CSRF - Disabled for REST APIs
            // ========================================
            .csrf(AbstractHttpConfigurer::disable)
            
            // ========================================
            // Authorization Rules
            // ========================================
            .authorizeHttpRequests(req -> req
                // ---------------------------
                // Public Auth Endpoints
                // ---------------------------
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/authenticate").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()
                .requestMatchers("/api/auth/refresh-token").permitAll()
                .requestMatchers("/api/auth/forgot-password").permitAll()
                .requestMatchers("/api/auth/reset-password").permitAll()
                .requestMatchers("/api/auth/verify-email").permitAll()
                
                // ---------------------------
                // Public Static Resources
                // ---------------------------
                .requestMatchers("/uploads/**").permitAll()
                
                // ---------------------------
                // Public GET Endpoints (Read-only for everyone)
                // ---------------------------
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/offres/**").permitAll()
                // ---------------------------
                // Categories - ADMIN only for write operations
                // ---------------------------
                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasRole("ADMIN")


                
                // ---------------------------
                // Products - ADMIN only for write operations
                // ---------------------------
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
                
                // ---------------------------
                // Offres - ADMIN only for write operations
                // ---------------------------
                .requestMatchers(HttpMethod.POST, "/api/offres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/offres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/offres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/offres/**").hasRole("ADMIN")
                
                // ---------------------------
                // Orders - Authenticated users can view their own
                // ---------------------------
                .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/orders/**").authenticated()

                
                // ---------------------------
                // Everything else requires authentication
                // ---------------------------
                .anyRequest().authenticated()
            )
            
            // ========================================
            // Session Management - Stateless (JWT)
            // ========================================
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ========================================
            // Authentication Provider & JWT Filter
            // ========================================
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            
            // ========================================
            // Logout - Using controller-based logout
            // ========================================
            .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * CORS Configuration Source
     * Allows requests from Angular frontend with authentication headers
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow credentials (Authorization header, cookies, etc.)
        configuration.setAllowCredentials(true);
        
        // Allow specific origins (Angular dev servers)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "http://localhost:3600"
            // Add production URLs when deploying:
            // "https://yourdomain.com"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));
        
        // Allow all standard HTTP methods + OPTIONS for preflight
        configuration.setAllowedMethods(Arrays.asList(
            "GET", 
            "POST", 
            "PUT", 
            "DELETE", 
            "PATCH", 
            "OPTIONS",
            "HEAD"
        ));
        
        // Expose headers that the frontend can access
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Cache-Control"
        ));
        
        // Cache preflight response for 1 hour (reduces preflight requests)
        configuration.setMaxAge(3600L);
        
        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}