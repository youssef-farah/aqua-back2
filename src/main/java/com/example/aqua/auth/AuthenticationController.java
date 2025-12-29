package com.example.aqua.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aqua.Useraqua.User;
import com.example.aqua.Useraqua.UserRepository;
import com.example.aqua.mail.PasswordResetService;
import com.example.aqua.mail.PasswordResetToken;
import com.example.aqua.mail.PasswordResetTokenRepository;
import com.example.aqua.tocken.TokenRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  
  
  
  @Autowired
  private PasswordResetService passwordResetService;
  
  @Autowired
  private UserRepository userRepository;
  
  
  @Autowired 
  private PasswordResetTokenRepository resetrepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(
          @RequestParam String email) {

      passwordResetService.forgotPassword(email);
      
      return ResponseEntity.ok("Reset link sent to email");
      
  }
  
  
  
  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(
          @RequestParam String token,
          @RequestParam String newPassword) {

      PasswordResetToken resetToken = resetrepo
              .findByToken(token)
              .orElseThrow(() -> new RuntimeException("Invalid token"));

      if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
          throw new RuntimeException("Token expired");
      }

      User user = resetToken.getUser();
      user.setPassword(passwordEncoder.encode(newPassword));
      userRepository.save(user);

      resetrepo.delete(resetToken);

      return ResponseEntity.ok("Password reset successful");
  }



@PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
	
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }

  
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
      service.logout(request);
      return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
  }

  

}