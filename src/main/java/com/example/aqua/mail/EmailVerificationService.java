package com.example.aqua.mail;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aqua.Useraqua.User;
import com.example.aqua.Useraqua.UserRepository;

@Service
public class EmailVerificationService {

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Send verification email to new user
     */
    public void sendVerificationEmail(User user) {
        // Generate unique token
        String token = UUID.randomUUID().toString();

        // Create verification token entity
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(60)); // 60 minutes expiry

        tokenRepository.save(verificationToken);

        // Create verification link
        String verificationLink = "http://localhost:8085/api/auth/verify-email?token=" + token;

        // Send email
        emailService.sendEmail(
            user.getMail(),
            "Verify Your Email Address",
            "Welcome! Please click the link below to verify your email address:\n\n" + 
            verificationLink + "\n\n" +
            "This link will expire in 60 minutes.\n\n" +
            "If you didn't create an account, please ignore this email."
        );
    }

    /**
     * Verify email using token
     */
    public void verifyEmail(String token) {
        // Find token
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        // Check if token expired
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        // Get user and enable account
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        // Delete used token
        tokenRepository.delete(verificationToken);
    }
}