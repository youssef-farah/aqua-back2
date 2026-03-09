package com.example.aqua.payement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * NEW FLOW: Generate payment link WITHOUT creating order
     * Order will be created only after successful payment verification
     * 
     * @param request Contains: userId, cartItems[], totalAmount
     * @return Payment link and payment ID
     */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePaymentOnly(@RequestBody PaymentInitiateRequest request) {
        try {
            // Validate request
            if (request.getTotalAmount() == null || request.getTotalAmount() <= 0) {
                throw new RuntimeException("Invalid total amount");
            }
            
            if (request.getUserId() == null) {
                throw new RuntimeException("User ID is required");
            }

            // Generate payment with Flouci (amount in millimes)
            Long amountInMillimes = Math.round(request.getTotalAmount() * 1000);
            FlouciService.PaymentResponse paymentResponse = flouciService.generatePayment(amountInMillimes);

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
     * UPDATED: Verify payment and CREATE order only if payment successful
     * 
     * @param request Contains: paymentId, userId, cartItems[], totalAmount
     * @return Created order with CONFIRMED status
     */
    @PostMapping("/verify-and-create-order")
    public ResponseEntity<?> verifyPaymentAndCreateOrder(@RequestBody OrderCreationRequest request) {
        try {
            // 1. Verify payment with Flouci
            FlouciService.PaymentVerificationResponse verification = 
                flouciService.verifyPayment(request.getPaymentId());

            if (!verification.isSuccessful()) {
                return ResponseEntity.badRequest().body(
                    Map.of(
                        "success", false, 
                        "error", "Payment verification failed",
                        "paymentStatus", verification.getStatus()
                    )
                );
            }

            // 2. Create order with CONFIRMED status
            Order createdOrder = orderService.createOrderAfterPayment(
                request.getUserId(),
                request.getCartItems(),
                request.getTotalAmount(),
                request.getPaymentId()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", createdOrder.getId());
            response.put("orderState", createdOrder.getState().toString());
            response.put("paymentId", request.getPaymentId());
            response.put("order", createdOrder);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "error", e.getMessage())
            );
        }
    }

    /**
     * Get payment status without creating order
     * Useful for checking status before order creation
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

    // ==================== OLD ENDPOINTS (Keep for backward compatibility) ====================

    /**
     * @deprecated Use /initiate instead
     */
    @PostMapping("/initiate/{orderId}")
    @Deprecated
    public ResponseEntity<?> initiatePaymentOld(@PathVariable Long orderId) {
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
     * @deprecated Use /verify-and-create-order instead
     */
    @PostMapping("/verifypay")
    @Deprecated
    public ResponseEntity<?> verifyPaymentOld(@RequestParam String paymentId) {
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

    // ==================== DTOs ====================

    public static class PaymentInitiateRequest {
        private Long userId;
        private Double totalAmount;
        private List<CartItemDTO> cartItems;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
        
        public List<CartItemDTO> getCartItems() { return cartItems; }
        public void setCartItems(List<CartItemDTO> cartItems) { this.cartItems = cartItems; }
    }

    public static class OrderCreationRequest {
        private String paymentId;
        private Long userId;
        private Double totalAmount;
        private List<CartItemDTO> cartItems;

        // Getters and setters
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
        
        public List<CartItemDTO> getCartItems() { return cartItems; }
        public void setCartItems(List<CartItemDTO> cartItems) { this.cartItems = cartItems; }
    }

    public static class CartItemDTO {
        private Long productId;
        private Integer quantity;
        private Double price;
        private String productoption; 


        // Getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
		public String getProductoption() {
			return productoption;
		}
		public void setProductoption(String productoption) {
			this.productoption = productoption;
		}
        
        
    }
}