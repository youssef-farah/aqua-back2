package com.example.aqua.mail;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aqua.Useraqua.User;
import com.example.aqua.Useraqua.UserRepository;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public void forgotPassword(String email) {
        User user = userRepository.findByMail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        tokenRepository.save(resetToken);

        String resetLink = 
            "http://localhost:4200/reset-password?token=" + token;

        emailService.sendEmail(
                user.getMail(),
                "Reset Your Password",
                "Click the link to reset your password:\n" + resetLink
        );
    }
}
