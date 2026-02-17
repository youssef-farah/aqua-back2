package com.example.aqua.product;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "product_options")
public class ProductOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String optionName;
    
    
    @DecimalMin(value = "0.00")
    @Column(precision = 19, scale = 2)
    private BigDecimal optionPrice;
    
    @ManyToOne
    @JoinColumn(name = "product_code", referencedColumnName = "code")
    @JsonBackReference(value = "product-options")  
    private Product product;
    
    // Constructors
    public ProductOption() {}
    
    public ProductOption(String optionName, BigDecimal optionPrice) {
        this.optionName = optionName;
        this.optionPrice = optionPrice;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOptionName() { return optionName; }
    public void setOptionName(String optionName) { this.optionName = optionName; }
    
    public BigDecimal getOptionPrice() { return optionPrice; }
    public void setOptionPrice(BigDecimal optionPrice) { this.optionPrice = optionPrice; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}