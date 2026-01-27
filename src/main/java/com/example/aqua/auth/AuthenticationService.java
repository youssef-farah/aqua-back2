package com.example.aqua.auth;

import java.util.Collections;

import com.example.aqua.Useraqua.AuthProvider;
import com.example.aqua.Useraqua.User;
import com.example.aqua.Useraqua.UserRepository;
import com.example.aqua.mail.EmailVerificationService;
import com.example.aqua.security.JwtService;
import com.example.aqua.tocken.Token;
import com.example.aqua.tocken.TokenRepository;
import com.example.aqua.tocken.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService; // NEW
   
    @Value("${google.client.id}")
    private String googleClientId;

    /**
     * Register new user and send verification email
     * User will NOT receive JWT tokens until email is verified
     */
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
            .prenom(request.getFirstname())
            .nom(request.getLastname())
            .mail(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .adresse(request.getAdresse())
            .enabled(false) // NEW - Account disabled by default
            .build();
        
        var savedUser = repository.save(user);
        
        // Send verification email
        emailVerificationService.sendVerificationEmail(savedUser);
        
        // Return response WITHOUT tokens (user must verify first)
        return AuthenticationResponse.builder()
            .accessToken(null)
            .refreshToken(null)
            .build();
    }

    /**
     * Authenticate user - ONLY if email is verified
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // First, check if user exists and is enabled
        var user = repository.findByMail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if account is verified
        if (!user.isEnabled()) {
            throw new DisabledException("Please verify your email address first. Check your inbox.");
        }
        
        // Proceed with normal authentication
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        
        return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId_user());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByMail(userEmail)
                    .orElseThrow();
            
            // Check if user is still enabled
            if (!user.isEnabled()) {
                throw new DisabledException("Account is not verified");
            }
            
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void logout(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

        final String jwt = authHeader.substring(7);
        var userEmail = jwtService.extractUsername(jwt);
        if (userEmail == null) return;

        var user = repository.findByMail(userEmail).orElseThrow();

        // Revoke the token in DB
        tokenRepository.findByToken(jwt).ifPresent(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            tokenRepository.save(token);
        });

        // Clear SecurityContext for this request
        SecurityContextHolder.clearContext();

        System.out.println("User " + userEmail + " logged out and token revoked");
    }
    
    
    
    
    
    
    public AuthenticationResponse authenticateWithGoogle(String googleIdToken) {
        try {
            // Verify Google token and extract user info
            GoogleIdToken.Payload payload = verifyGoogleToken(googleIdToken);
            
            String email = payload.getEmail();
            String googleId = payload.getSubject();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            Boolean emailVerified = payload.getEmailVerified();
            
            if (emailVerified == null || !emailVerified) {
                throw new RuntimeException("Google email not verified");
            }
            
            // Find or create user
            User user = repository.findByMail(email)
                .orElseGet(() -> {
                    // Create new user from Google account
                    User newUser = User.builder()
                        .mail(email)
                        .prenom(firstName)
                        .nom(lastName)
                        .googleId(googleId)
                        .authProvider(AuthProvider.GOOGLE)
                        .password(null) // No password for Google users
                        .role("USER") // Default role
                        .enabled(true) // Google emails are pre-verified
                        .build();
                    return repository.save(newUser);
                });
            
            // Check if account is disabled (shouldn't happen for Google users, but safety check)
            if (!user.isEnabled()) {
                throw new RuntimeException("Account is disabled");
            }
            
            // If user exists but signed up with LOCAL (email/password)
            if (user.getAuthProvider() == AuthProvider.LOCAL && user.getGoogleId() == null) {
                // Link Google account to existing LOCAL account
                user.setGoogleId(googleId);
                repository.save(user);
            }
            
            // Generate JWT tokens (same as regular login)
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            
            return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
                
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate with Google: " + e.getMessage());
        }
    }

    /**
     * Verify Google ID token with Google's servers
     */
    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), 
                new GsonFactory())
        		.setAudience(Collections.singletonList(googleClientId))
            .build();
        
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            throw new RuntimeException("Invalid Google ID token");
        }
    }
    
    
}