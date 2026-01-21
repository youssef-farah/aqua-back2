package com.example.aqua.payement;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlouciService {

	/*
	public FlouciPaymentResult createPayment(Long paymentId, Long amount) {
        // Call Flouci API
        // Return payment_url + provider_payment_id
    }

    public boolean verifyPayment(String providerPaymentId) {
        // Call Flouci API to verify
        return true; // if paid
    }
}*/
	
	
	
	 @Value("${flouci.app.token}")
	    private String appToken;

	    @Value("${flouci.app.secret}")
	    private String appSecret;

	    @Value("${flouci.base.url}")
	    private String baseUrl;

	    private final RestTemplate restTemplate = new RestTemplate();

	    /**
	     * Create a payment on Flouci
	     */
	    public CreatePaymentResponse createPayment(double amount, String description) {

	        String url = baseUrl + "/generate_payment";

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("app_token", appToken);
	        headers.set("app_secret", appSecret);

	        Map<String, Object> body = new HashMap<>();
	        body.put("amount", (int) (amount * 1000)); // Flouci uses millimes
	        body.put("description", description);
	        body.put("success_link", "https://yourdomain.com/success");
	        body.put("fail_link", "https://yourdomain.com/fail");

	        HttpEntity<Map<String, Object>> entity =
	                new HttpEntity<>(body, headers);

	        ResponseEntity<Map> response =
	                restTemplate.postForEntity(url, entity, Map.class);

	        Map result = (Map) response.getBody().get("result");

	        CreatePaymentResponse paymentResponse = new CreatePaymentResponse();
	        paymentResponse.setPaymentId(result.get("payment_id").toString());
	        paymentResponse.setPaymentUrl(result.get("link").toString());

	        return paymentResponse;
	    }

	    /**
	     * Verify payment status
	     */
	    public VerifyPaymentResponse verifyPayment(String paymentId) {

	        String url = baseUrl + "/verify_payment/" + paymentId;

	        HttpHeaders headers = new HttpHeaders();
	        headers.set("app_token", appToken);
	        headers.set("app_secret", appSecret);

	        HttpEntity<Void> entity = new HttpEntity<>(headers);

	        ResponseEntity<Map> response =
	                restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

	        Map result = (Map) response.getBody().get("result");
	        String status = result.get("status").toString();

	        VerifyPaymentResponse verifyResponse = new VerifyPaymentResponse();
	        verifyResponse.setStatus(status);
	        verifyResponse.setConfirmed("SUCCESS".equalsIgnoreCase(status));

	        return verifyResponse;
	    }
	
	
	
	
}
