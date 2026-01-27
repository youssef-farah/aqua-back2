package com.example.aqua.payement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/payements")
public class PayementController {
	
	
	 private static final String FLOUCI_URL =
	            "https://developers.flouci.com/api/v2/generate_payment";

	    // TODO: Move to ENV later
	    private static final String PUBLIC_KEY = "b8b65140-caf1-400f-8faa-d3b60edc1608";
	    private static final String PRIVATE_KEY = "5f573c17-b76c-485c-9fcb-863494112871";

	    @PostMapping("/sendpay")
	    public ResponseEntity<?> sendPayment(@RequestParam Long amount) {

	        RestTemplate restTemplate = new RestTemplate();

	        // Headers
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        headers.set(
	                "Authorization",
	                "Bearer " + PUBLIC_KEY + ":" + PRIVATE_KEY
	        );

	        // Body
	        Map<String, Object> body = new HashMap<>();

	        body.put("amount", amount);
	        body.put("success_link", "http://localhost:4200/succespay");
	        body.put("fail_link", "http://localhost:4200/failpay");
	        //body.put("webhook", "https://your-website.com/webhook");
	        body.put("accept_card","true");
	        body.put("developer_tracking_id", UUID.randomUUID().toString());

	        HttpEntity<Map<String, Object>> request =
	                new HttpEntity<>(body, headers);

	        // Call Flouci API
	        ResponseEntity<String> response = restTemplate.postForEntity(
	                FLOUCI_URL,
	                request,
	                String.class
	        );

	        return ResponseEntity.ok(response.getBody());
	    }
	    
	    
	    @PostMapping("/verifypay")
	    public ResponseEntity<?> verifyPayment(@RequestParam String paymentId) {

	        RestTemplate restTemplate = new RestTemplate();

	        String url =
	            "https://developers.flouci.com/api/v2/verify_payment/" + paymentId;

	        // Headers
	        HttpHeaders headers = new HttpHeaders();
	        headers.set(
	                "Authorization",
	                "Bearer " + PUBLIC_KEY + ":" + PRIVATE_KEY
	        );

	        HttpEntity<Void> request = new HttpEntity<>(headers);

	        // Call Flouci API (GET)
	        ResponseEntity<String> response = restTemplate.exchange(
	                url,
	                HttpMethod.GET,
	                request,
	                String.class
	        );

	        return ResponseEntity.ok(response.getBody());
	    }


	/*

	
	 private final PaymentService paymentService;
	    public PayementController(PaymentService paymentService) {
	        this.paymentService = paymentService;
	    }

	    // STEP 1: Create payment for an order
	    @PostMapping("/create")
	    public ResponseEntity<PaymentResponseDTO> createPayment(
	            @RequestBody CreatePaymentDTO dto) {

	        PaymentResponseDTO response =
	                paymentService.createPayment(dto.getOrderId());

	        return ResponseEntity.ok(response);
	    }

	    // STEP 2: Verify payment after redirect
	    @GetMapping("/verify/{paymentId}")
	    public ResponseEntity<Void> verifyPayment(
	            @PathVariable Long paymentId) {

	        paymentService.verifyPayment(paymentId);
	        return ResponseEntity.ok().build();
	    }  */
	
	
	 private final FlouciService paymentService;

	    public PayementController(FlouciService paymentService) {
	        this.paymentService = paymentService;
	    }

	    /**
	     * Create payment
	     */
	    @PostMapping("/create")
	    public ResponseEntity<CreatePaymentResponse> createPayment(
	            @RequestBody CreatePaymentDTO request) {

	        return ResponseEntity.ok(
	                paymentService.createPayment(
	                        request.getAmount(),
	                        request.getDescription()
	                )
	        );
	    }

	    /**
	     * Verify payment
	     
	    @GetMapping("/verify/{paymentId}")
	    public ResponseEntity<VerifyPaymentResponse> verifyPayment(
	            @PathVariable String paymentId) {

	        return ResponseEntity.ok(
	                paymentService.verifyPayment(paymentId)
	        );
	    }
	
	*/
	
}
