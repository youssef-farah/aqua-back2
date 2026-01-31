package com.example.aqua.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.example.aqua.Useraqua.User;
import com.example.aqua.orderItems.OrderItems;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant createdAt = Instant.now();
    private Instant updatedAt;

    public enum OrderState { 
        CREATED,      // Order created, awaiting payment
        CONFIRMED,    // Payment successful
        SHIPPED,      // Order shipped
        DELIVERED,    // Order delivered
        CANCELLED     // Order cancelled or payment failed
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state = OrderState.CREATED;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    // ✅ CRITICAL: Link to Flouci payment
    @Column(name = "payment_id", unique = true)
    private String paymentId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItems> items;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    // ==================== Constructors ====================

    public Order() {
    }

    public Order(Long id, Instant createdAt, Instant updatedAt, OrderState state, 
                 BigDecimal total, List<OrderItems> items, User user) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.state = state;
        this.total = total;
        this.items = items;
        this.user = user;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

	public List<OrderItems> getItems() {
		return items;
	}

	public void setItems(List<OrderItems> items) {
		this.items = items;
	}



	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	
	
	
	
	
}

