package com.example.aqua.payement;

import java.time.LocalDateTime;

import com.example.aqua.Order.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;


@Entity
public class Payement {
	
	
		@Id
	    @GeneratedValue
	    private Long id;

	    @OneToOne
	    @JoinColumn(name = "order_id", nullable = false, unique = true)
	    private Order order;

	    private Long amount;

	    @Enumerated(EnumType.STRING)
	    private PaymentStatus status;

	    private String provider; // FLOUCI
	    private String providerPaymentId;

	    private LocalDateTime createdAt;
	    private LocalDateTime paidAt;
	    
	    
	    public Payement()
	    {
	    	
	    }
	    
	    
	    
	   
		public Payement(Long id, Order order, Long amount, PaymentStatus status, String provider,
				String providerPaymentId, LocalDateTime createdAt, LocalDateTime paidAt) {
			this.id = id;
			this.order = order;
			this.amount = amount;
			this.status = status;
			this.provider = provider;
			this.providerPaymentId = providerPaymentId;
			this.createdAt = createdAt;
			this.paidAt = paidAt;
		}






		public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}


		public Order getOrder() {
			return order;
		}


		public void setOrder(Order order) {
			this.order = order;
		}


		public Long getAmount() {
			return amount;
		}


		public void setAmount(Long amount) {
			this.amount = amount;
		}


		public PaymentStatus getStatus() {
			return status;
		}


		public void setStatus(PaymentStatus status) {
			this.status = status;
		}


		public String getProvider() {
			return provider;
		}


		public void setProvider(String provider) {
			this.provider = provider;
		}


		public String getProviderPaymentId() {
			return providerPaymentId;
		}


		public void setProviderPaymentId(String providerPaymentId) {
			this.providerPaymentId = providerPaymentId;
		}


		public LocalDateTime getCreatedAt() {
			return createdAt;
		}


		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}


		public LocalDateTime getPaidAt() {
			return paidAt;
		}


		public void setPaidAt(LocalDateTime paidAt) {
			this.paidAt = paidAt;
		}
	    
	    
	    
	    
	}