package com.example.aqua.payement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.aqua.Order.Order;
import com.example.aqua.Order.OrderService;

@RestController
@RequestMapping("/api/payements")
public class PayementController {
	
	
	 @Autowired
	    private FlouciService flouciService;

	    @Autowired
	    private OrderService orderService;

	    /**
	     * Initiate payment for an order
	     * 
	     * @param orderId The order ID to pay for
	     * @return Payment link and payment ID
	     */
	    @PostMapping("/initiate/{orderId}")
	    public ResponseEntity<?> initiatePayment(@PathVariable Long orderId) {
	        try {
	            FlouciService.PaymentResponse paymentResponse = orderService.initiatePayment(orderId);
	            
	            Map<String, Object> response = new HashMap<>();
	            response.put("success", true);
	            response.put("paymentId", paymentResponse.getPaymentId());
	            response.put("paymentLink", paymentResponse.getPaymentLink());
	            
	            return ResponseEntity.ok(response);
	            
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body(
	                Map.of("success", false, "error", e.getMessage())
	            );
	        }
	    }

	    /**
	     * Legacy endpoint - Generate payment directly (not recommended)
	     * Use /initiate/{orderId} instead for proper order-payment linking
	     * 
	     * @param amount Payment amount in millimes
	     */
	    @PostMapping("/sendpay")
	    @Deprecated
	    public ResponseEntity<?> sendPayment(@RequestParam Long amount) {
	        try {
	            FlouciService.PaymentResponse response = flouciService.generatePayment(amount);
	            
	            Map<String, Object> result = new HashMap<>();
	            result.put("paymentId", response.getPaymentId());
	            result.put("paymentLink", response.getPaymentLink());
	            
	            return ResponseEntity.ok(result);
	            
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body(
	                Map.of("success", false, "error", e.getMessage())
	            );
	        }
	    }

	    /**
	     * Verify payment and update order status
	     * 
	     * @param paymentId Flouci payment ID
	     * @return Updated order with new status
	     */
	    @PostMapping("/verifypay")
	    public ResponseEntity<?> verifyPayment(@RequestParam String paymentId) {
	        try {
	            Order updatedOrder = orderService.verifyAndUpdateOrder(paymentId);
	            
	            Map<String, Object> response = new HashMap<>();
	            response.put("success", true);
	            response.put("orderId", updatedOrder.getId());
	            response.put("orderState", updatedOrder.getState().toString());
	            response.put("paymentId", paymentId);
	            
	            return ResponseEntity.ok(response);
	            
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body(
	                Map.of("success", false, "error", e.getMessage())
	            );
	        }
	    }

	    /**
	     * Get payment status without updating order
	     * Useful for checking status before verification
	     */
	    @GetMapping("/status/{paymentId}")
	    public ResponseEntity<?> getPaymentStatus(@PathVariable String paymentId) {
	        try {
	            FlouciService.PaymentVerificationResponse verification = 
	                flouciService.verifyPayment(paymentId);
	            
	            Map<String, Object> response = new HashMap<>();
	            response.put("success", true);
	            response.put("paymentId", paymentId);
	            response.put("status", verification.getStatus());
	            response.put("amount", verification.getAmount());
	            response.put("isSuccessful", verification.isSuccessful());
	            
	            return ResponseEntity.ok(response);
	            
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body(
	                Map.of("success", false, "error", e.getMessage())
	            );
	        }
	    }
	
}
