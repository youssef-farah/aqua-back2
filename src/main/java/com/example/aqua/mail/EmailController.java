package com.example.aqua.mail;



import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/contact")
public class EmailController {

	@Autowired
    private EmailService emailService;

    @Value("${app.contact.email}") // set this in application.properties
    private String contactEmail;

    /**
     * Handles construction quote requests
     */
    @PostMapping("/construction")
    public ResponseEntity<String> sendConstructionRequest(@Valid @RequestBody ContactRequestDTO request) {
        try {
            String subject = "Nouvelle demande de construction - " + request.getName();

            String body = """
                    📋 NOUVELLE DEMANDE DE CONSTRUCTION DE PISCINE
                    ===============================================

                    👤 INFORMATIONS CLIENT
                    Nom       : %s
                    Téléphone : %s
                    Email     : %s

                    🏊 CARACTÉRISTIQUES DE LA PISCINE
                    Forme      : %s
                    Surface    : %s m²
                    Profondeur : %s m
                    Volume     : %s m³

                    💰 PRIX ESTIMÉ : %s DT

                    ===============================================
                    """.formatted(
                    request.getName(),
                    request.getPhone(),
                    request.getEmail() != null ? request.getEmail() : "Non fourni",
                    request.getShape(),
                    request.getSurface(),
                    request.getDepth(),
                    request.getVolume() != null ? request.getVolume() : "Non fourni",
                    request.getEstimatedPrice() != null ? request.getEstimatedPrice() : "À calculer"
            );

            // Send notification to your company
            emailService.sendEmail(contactEmail, subject, body);

            // Send confirmation to client if email provided
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                String confirmationBody = """
                        Bonjour %s,

                        Nous avons bien reçu votre demande de devis pour la construction d'une piscine.

                        Récapitulatif de votre demande :
                        - Forme    : %s
                        - Surface  : %s m²
                        - Prix estimé : %s DT

                        Un de nos conseillers vous contactera dans les plus brefs délais.

                        Cordialement,
                        L'équipe Premium Pool Services
                        """.formatted(
                        request.getName(),
                        request.getShape(),
                        request.getSurface(),
                        request.getEstimatedPrice() != null ? request.getEstimatedPrice() : "À confirmer"
                );
                emailService.sendEmail(request.getEmail(), "Confirmation de votre demande - Premium Pool Services", confirmationBody);
            }

            return ResponseEntity.ok("Demande envoyée avec succès");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi: " + e.getMessage());
        }
    }

    /**
     * Handles maintenance contract requests
     */
    @PostMapping("/maintenance")
    public ResponseEntity<String> sendMaintenanceRequest(@Valid @RequestBody ContactRequestDTO request) {
        try {
            String subject = "Nouvelle demande de maintenance - " + request.getName();

            String body = """
                    📋 NOUVELLE DEMANDE DE MAINTENANCE DE PISCINE
                    ===============================================

                    👤 INFORMATIONS CLIENT
                    Nom       : %s
                    Téléphone : %s
                    Email     : %s

                    📦 FORMULE CHOISIE
                    %s

                    ===============================================
                    """.formatted(
                    request.getName(),
                    request.getPhone(),
                    request.getEmail() != null ? request.getEmail() : "Non fourni",
                    request.getSelectedOffer()
            );

            emailService.sendEmail(contactEmail, subject, body);

            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                String confirmationBody = """
                        Bonjour %s,

                        Nous avons bien reçu votre demande de contrat de maintenance.

                        Formule sélectionnée : %s

                        Un de nos techniciens vous contactera très prochainement.

                        Cordialement,
                        L'équipe Premium Pool Services
                        """.formatted(request.getName(), request.getSelectedOffer());

                emailService.sendEmail(request.getEmail(), "Confirmation de votre demande de maintenance - Premium Pool Services", confirmationBody);
            }

            return ResponseEntity.ok("Demande envoyée avec succès");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'envoi: " + e.getMessage());
        }
    }
}
