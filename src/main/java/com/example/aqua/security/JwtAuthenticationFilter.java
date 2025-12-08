package com.example.aqua.security;


import java.beans.Transient;
import java.io.IOException;
import java.security.Security;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.example.aqua.tocken.TokenRepository;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	
		private final JwtService jwtService;
		private final UserDetailsService userDetailsService;
	    private final TokenRepository tokenRepository; // <-- ADD THIS

	    public JwtAuthenticationFilter(JwtService jwtService, 
                UserDetailsService userDetailsService,
                TokenRepository tokenRepository) {
this.jwtService = jwtService;
this.userDetailsService = userDetailsService;
this.tokenRepository = tokenRepository;
}
	
	    @Override
	    protected void doFilterInternal(
	            HttpServletRequest request,
	            HttpServletResponse response,
	            FilterChain filterChain
	    ) throws ServletException, IOException {

	        // Allow auth endpoints without filtering
	        if (!(request instanceof ContentCachingRequestWrapper)) {
	            request = new ContentCachingRequestWrapper(request);
	        }

	        // Allow auth endpoints without filtering
	        if (request.getServletPath().contains("/api/auth")) {
	            filterChain.doFilter(request, response);
	            return;
	        }

	        final String authHeader = request.getHeader("Authorization");
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            filterChain.doFilter(request, response);
	            return;
	        }

	        final String jwt = authHeader.substring(7);
	        final String userEmail = jwtService.extractUsername(jwt);

	        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

	            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

	            boolean isTokenValid = tokenRepository.findByToken(jwt)
	                    .map(t -> !t.isExpired() && !t.isRevoked())
	                    .orElse(false);

	            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {

	                // ★ ADDED — extract roles from JWT claims
	                List<String> roles = jwtService.extractRoles(jwt); // (we will implement this next)
	                Collection<? extends GrantedAuthority> authorities;
	                System.out.println("🔐 Extracted roles from JWT: " + roles);   // <-- PRINT HERE

	                if (roles != null && !roles.isEmpty()) {
	                    authorities = roles.stream()
	                            .map(SimpleGrantedAuthority::new)
	                            .toList();
	                } else {
	                    // fallback: use authorities from UserDetails
	                    authorities = userDetails.getAuthorities();
	                }
	                
	                

	                // Create authentication token WITH roles
	                UsernamePasswordAuthenticationToken authToken =
	                        new UsernamePasswordAuthenticationToken(
	                                userDetails,
	                                null,
	                                authorities
	                        );

	                authToken.setDetails(
	                        new WebAuthenticationDetailsSource().buildDetails(request)
	                );

	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }
	        }

	        filterChain.doFilter(request, response);
	    }
}
