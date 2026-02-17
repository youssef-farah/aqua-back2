package com.example.aqua.Order;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aqua.Useraqua.User;
import com.example.aqua.Useraqua.UserRepository;
import com.example.aqua.exception.NotFoundException;
import com.example.aqua.orderItems.OrderItems;
import com.example.aqua.orderItems.OrderItemsRepository;
import com.example.aqua.payement.FlouciService;
import com.example.aqua.payement.FlouciService.PaymentResponse;
import com.example.aqua.payement.FlouciService.PaymentVerificationResponse;
import com.example.aqua.payement.PayementController.CartItemDTO;
import com.example.aqua.product.Product;
import com.example.aqua.product.ProductRepository;


@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	 @Autowired
	    private FlouciService flouciService;

	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private ProductRepository productRepository;

	    @Autowired
	    private OrderItemsRepository orderItemRepository;

	    // ==================== NEW FLOW METHODS ====================

	    /**
	     * Create order AFTER successful payment verification
	     * This is the new recommended flow
	     * 
	     * @param userId User making the purchase
	     * @param cartItems List of items to order
	     * @param totalAmount Total order amount
	     * @param paymentId Verified Flouci payment ID
	     * @return Created order with CONFIRMED status
	     */
	    @Transactional
	    public Order createOrderAfterPayment(
	            Long userId, 
	            List<CartItemDTO> cartItems, 
	            Double totalAmount,
	            String paymentId) {
	        
	        // 1. Get user
	        User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

	        // 2. Create order with CONFIRMED status (payment already verified)
	        Order order = new Order();
	        order.setUser(user);
	        order.setState(Order.OrderState.CONFIRMED);
	        order.setTotal(BigDecimal.valueOf(totalAmount));
	        order.setPaymentId(paymentId);
	        order.setCreatedAt(Instant.now());
	        order.setUpdatedAt(Instant.now());

	        Order savedOrder = orderRepository.save(order);

	        // 3. Create order items
	        for (CartItemDTO cartItem : cartItems) {
	            Product product = productRepository.findById(cartItem.getProductId())
	                .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));

	            OrderItems orderItem = new OrderItems();
	            orderItem.setOrder(savedOrder);
	            orderItem.setProduct(product);
	            orderItem.setQuantity(cartItem.getQuantity());
	            orderItem.setUnitPrice(BigDecimal.valueOf(cartItem.getPrice()));
	            orderItem.setSubTotal(
	                BigDecimal.valueOf(cartItem.getPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity()))
	            );

	            orderItemRepository.save(orderItem);
	        }

	        return savedOrder;
	    }

	    // ==================== ORIGINAL METHODS (Keep for backward compatibility) ====================

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
	            o.setState(updated.getState());
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

	    /**
	     * @deprecated Use createOrderAfterPayment instead
	     */
	    @Deprecated
	    @Transactional
	    public PaymentResponse initiatePayment(Long orderId) {
	        Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

	        if (order.getPaymentId() != null) {
	            throw new RuntimeException("Order already has a payment associated");
	        }

	        if (order.getState() != Order.OrderState.CREATED) {
	            throw new RuntimeException("Order must be in CREATED state to initiate payment");
	        }

	        Long amountInMillimes = order.getTotal().multiply(new BigDecimal("1000")).longValue();
	        PaymentResponse paymentResponse = flouciService.generatePayment(amountInMillimes);

	        order.setPaymentId(paymentResponse.getPaymentId());
	        order.setUpdatedAt(Instant.now());
	        orderRepository.save(order);

	        return paymentResponse;
	    }

	    /**
	     * @deprecated Use createOrderAfterPayment instead
	     */
	    @Deprecated
	    @Transactional
	    public Order verifyAndUpdateOrder(String paymentId) {
	        Order order = orderRepository.findByPaymentId(paymentId)
	            .orElseThrow(() -> new RuntimeException("No order found for payment ID: " + paymentId));

	        PaymentVerificationResponse verificationResponse = flouciService.verifyPayment(paymentId);

	        if (verificationResponse.isSuccessful()) {
	            order.setState(Order.OrderState.CONFIRMED);
	        } else {
	            order.setState(Order.OrderState.CANCELLED);
	        }

	        order.setUpdatedAt(Instant.now());
	        return orderRepository.save(order);
	    }

	    public Order getOrderByPaymentId(String paymentId) {
	        return orderRepository.findByPaymentId(paymentId)
	            .orElseThrow(() -> new RuntimeException("Order not found for payment: " + paymentId));
	    }

	    @Transactional
	    public Order createOrder(Order order) {
	        order.setState(Order.OrderState.CREATED);
	        order.setCreatedAt(Instant.now());
	        return orderRepository.save(order);
	    }

	    public Order getOrderById(Long orderId) {
	        return orderRepository.findById(orderId)
	            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
	    }
	}