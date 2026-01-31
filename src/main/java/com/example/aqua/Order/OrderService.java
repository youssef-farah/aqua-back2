package com.example.aqua.Order;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aqua.exception.NotFoundException;
import com.example.aqua.payement.FlouciService;
import com.example.aqua.payement.FlouciService.PaymentResponse;
import com.example.aqua.payement.FlouciService.PaymentVerificationResponse;


@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	

    @Autowired
    private FlouciService flouciService;

	public Order create(Order order) {
		return orderRepository.save(order);
	}

	@Transactional(readOnly = true)
	public List<Order> getAll() {
		return orderRepository.findAll();
	}	

	@Transactional(readOnly = true)
	public Optional<Order> getById(Long id) {
		return orderRepository.findById(id);
	}
	
	
	  @Transactional(readOnly = true)
	    public List<Order> getByUserId(Long userId) {
	        return orderRepository.findOrdersByUserId(userId);
	    }

	public Order update(Long id, Order updated) {
		return orderRepository.findById(id).map(o -> {
			// Update fields that make sense at order level
			o.setState(updated.getState());
			// Optionally replace items if provided (cascade will persist)
			if (updated.getItems() != null) {
				o.setItems(updated.getItems());
			}
			return orderRepository.save(o);
		}).orElseThrow(() -> new NotFoundException("Order not found with id " + id));
	}

	public void delete(Long id) {
		if (!orderRepository.existsById(id)) {
			throw new NotFoundException("Order not found with id " + id);
		}
		orderRepository.deleteById(id);
	}
	
	
	
	 @Transactional
	    public PaymentResponse initiatePayment(Long orderId) {
	        Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

	        // Prevent duplicate payments
	        if (order.getPaymentId() != null) {
	            throw new RuntimeException("Order already has a payment associated");
	        }

	        // Ensure order is in correct state
	        if (order.getState() != Order.OrderState.CREATED) {
	            throw new RuntimeException("Order must be in CREATED state to initiate payment");
	        }

	        // Generate payment with Flouci
	        Long amountInMillimes = order.getTotal().multiply(new java.math.BigDecimal("1000")).longValue();
	        PaymentResponse paymentResponse = flouciService.generatePayment(amountInMillimes);

	        // Save payment ID to order
	        order.setPaymentId(paymentResponse.getPaymentId());
	        order.setUpdatedAt(Instant.now());
	        orderRepository.save(order);

	        return paymentResponse;
	    }

	    /**
	     * Verify payment and update order status
	     * 
	     * @param paymentId The Flouci payment ID
	     * @return Updated order
	     * @throws RuntimeException if payment verification fails
	     */
	    @Transactional
	    public Order verifyAndUpdateOrder(String paymentId) {
	        // Find order by payment ID
	        Order order = orderRepository.findByPaymentId(paymentId)
	            .orElseThrow(() -> new RuntimeException("No order found for payment ID: " + paymentId));

	        // Verify payment with Flouci
	        PaymentVerificationResponse verificationResponse = flouciService.verifyPayment(paymentId);

	        // Update order based on payment status
	        if (verificationResponse.isSuccessful()) {
	            order.setState(Order.OrderState.CONFIRMED);
	        } else {
	            order.setState(Order.OrderState.CANCELLED);
	        }

	        order.setUpdatedAt(Instant.now());
	        return orderRepository.save(order);
	    }

	    /**
	     * Get order by payment ID
	     */
	    public Order getOrderByPaymentId(String paymentId) {
	        return orderRepository.findByPaymentId(paymentId)
	            .orElseThrow(() -> new RuntimeException("Order not found for payment: " + paymentId));
	    }

	    /**
	     * Create a new order (existing logic - add if not already present)
	     */
	    @Transactional
	    public Order createOrder(Order order) {
	        order.setState(Order.OrderState.CREATED);
	        order.setCreatedAt(Instant.now());
	        return orderRepository.save(order);
	    }

	    /**
	     * Get order by ID
	     */
	    public Order getOrderById(Long orderId) {
	        return orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
	    }
}


