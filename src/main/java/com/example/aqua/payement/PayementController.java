package com.example.aqua.payement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payements")
public class PayementController {

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
	     */
	    @GetMapping("/verify/{paymentId}")
	    public ResponseEntity<VerifyPaymentResponse> verifyPayment(
	            @PathVariable String paymentId) {

	        return ResponseEntity.ok(
	                paymentService.verifyPayment(paymentId)
	        );
	    }
	
	
	
}
