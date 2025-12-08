package com.example.aqua.orderItems;

import java.math.BigDecimal;

import com.example.aqua.Order.Order;
import com.example.aqua.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "order_items")
//@NoArgsConstructor
//@AllArgsConstructor
public class OrderItems {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "order_id", referencedColumnName = "id")
	@JsonBackReference
	private Order order;

	@ManyToOne
	@JoinColumn(name = "product_code", referencedColumnName = "code")
	private Product product;

	@Min(1)
	@Column(nullable = false)
	private int quantity;

	@NotNull
	@DecimalMin("0.00")
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal unitPrice;

	@NotNull
	@DecimalMin("0.00")
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal subTotal;



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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public OrderItems(Long id, Order order, Product product, @Min(1) int quantity,
			@NotNull @DecimalMin("0.00") BigDecimal unitPrice, @NotNull @DecimalMin("0.00") BigDecimal subTotal) {
		super();
		this.id = id;
		this.order = order;
		this.product = product;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.subTotal = subTotal;
	}
	
	
	
	public OrderItems()
	{
		
	}
}
