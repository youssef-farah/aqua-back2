package com.example.aqua.payement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FlouciService {

    private static final String FLOUCI_GENERATE_URL = 
        "https://developers.flouci.com/api/v2/generate_payment";
    
    private static final String FLOUCI_VERIFY_URL = 
        "https://developers.flouci.com/api/v2/verify_payment/";

    // TODO: Move these to application.properties or environment variables
    @Value("${flouci.public.key:b8b65140-caf1-400f-8faa-d3b60edc1608}")
    private String publicKey;

    @Value("${flouci.private.key:5f573c17-b76c-485c-9fcb-863494112871}")
    private String privateKey;

    @Value("${flouci.success.url:http://localhost:4200/succespay}")
    private String successUrl;

    @Value("${flouci.fail.url:http://localhost:4200/failpay}")
    private String failUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public FlouciService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generate a payment link with Flouci
     * 
     * @param amount Payment amount in millimes
     * @return PaymentResponse containing payment_id and payment link
     * @throws RuntimeException if payment generation fails
     */
    public PaymentResponse generatePayment(Long amount) {
        try {
            HttpHeaders headers = createAuthHeaders();
            Map<String, Object> body = createPaymentBody(amount);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                FLOUCI_GENERATE_URL,
                request,
                String.class
            );

            return parsePaymentResponse(response.getBody());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Flouci payment: " + e.getMessage(), e);
        }
    }

    /**
     * Verify payment status with Flouci
     * 
     * @param paymentId The Flouci payment ID to verify
     * @return PaymentVerificationResponse containing payment status and details
     * @throws RuntimeException if verification fails
     */
    public PaymentVerificationResponse verifyPayment(String paymentId) {
        try {
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                FLOUCI_VERIFY_URL + paymentId,
                HttpMethod.GET,
                request,
                String.class
            );

            return parseVerificationResponse(response.getBody());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify payment: " + e.getMessage(), e);
        }
    }

    /**
     * Create HTTP headers with Flouci authentication
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + publicKey + ":" + privateKey);
        return headers;
    }

    /**
     * Create payment request body
     */
    private Map<String, Object> createPaymentBody(Long amount) {
        Map<String, Object> body = new HashMap<>();
        body.put("amount", amount);
        body.put("success_link", successUrl);
        body.put("fail_link", failUrl);
        body.put("accept_card", "true");
        body.put("developer_tracking_id", UUID.randomUUID().toString());
        return body;
    }

    /**
     * Parse Flouci payment generation response
     */
    private PaymentResponse parsePaymentResponse(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);

        JsonNode result = root.path("result");

        String paymentId = result.path("payment_id").asText();
        String paymentLink = result.path("link").asText();

        if (paymentId == null || paymentId.isEmpty()) {
            throw new RuntimeException("Flouci did not return payment_id: " + jsonResponse);
        }

        return new PaymentResponse(paymentId, paymentLink);
    }


    /**
     * Parse Flouci payment verification response
     */
    private PaymentVerificationResponse parseVerificationResponse(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        JsonNode result = root.path("result");
        
        String status = result.path("status").asText();
        Long amount = result.path("amount").asLong();
        System.out.println(status);
        return new PaymentVerificationResponse(status, amount, jsonResponse);
    }

    // ==================== DTOs ====================

    /**
     * Response from payment generation
     */
    public static class PaymentResponse {
        private final String paymentId;
        private final String paymentLink;

        public PaymentResponse(String paymentId, String paymentLink) {
            this.paymentId = paymentId;
            this.paymentLink = paymentLink;
        }

        public String getPaymentId() {
            return paymentId;
        }

        public String getPaymentLink() {
            return paymentLink;
        }
    }

    /**
     * Response from payment verification
     */
    public static class PaymentVerificationResponse {
        private final String status;  // "SUCCESS" or "FAILED"
        private final Long amount;
        private final String rawResponse;

        public PaymentVerificationResponse(String status, Long amount, String rawResponse) {
            this.status = status;
            this.amount = amount;
            this.rawResponse = rawResponse;
        }

        public String getStatus() {
            return status;
        }

        public Long getAmount() {
            return amount;
        }

        public String getRawResponse() {
            return rawResponse;
        }

        public boolean isSuccessful() {
            return "SUCCESS".equalsIgnoreCase(status);
        }
    }
}
