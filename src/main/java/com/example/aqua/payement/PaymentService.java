package com.example.aqua.payement;

public interface PaymentService {

	
	
	 PaymentResponseDTO createPayment(Long orderId);
	    void verifyPayment(Long paymentId);
}
