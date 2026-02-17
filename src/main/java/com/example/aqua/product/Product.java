package com.example.aqua.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.aqua.category.Category;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="products")
public class Product {

	@Id
    @Column(nullable = false, unique = true)
    private Long code;
	
    @NotBlank
    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String lieuDeProduction;

    @Min(0)
    private int stock;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = true, precision = 19, scale = 2)
    private BigDecimal prix;

    private String image;
    
    
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id_category")
    @JsonBackReference(value = "category-products")
    private Category category;

    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "product_complementary_infos",
        joinColumns = @JoinColumn(name = "product_code", referencedColumnName = "code")
    )
    @MapKeyColumn(name = "info_key")
    @Column(name = "info_value", columnDefinition = "TEXT")
    private Map<String, String> complementaryInfos;

    //Optional add-ons/options for the product (e.g., "turbo engine", "extra storage")
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "product-options")
    private List<ProductOption> options = new ArrayList<>();  // ← Initialize here

    
    
    
    public Product()
    {
    	
    }
    
    
    
	public Product(Long code, String titre, String description, String lieuDeProduction, int stock, BigDecimal prix,
			String image, List<ProductOption> options) {
		
		this.code = code;
		this.titre = titre;
		this.description = description;
		this.lieuDeProduction = lieuDeProduction;
		this.stock = stock;
		this.prix = prix;
		this.image = image;
		this.options = options;
	}
	
	
	
	public Map<String, String> getComplementaryInfos() {
		return complementaryInfos;
	}



	public void setComplementaryInfos(Map<String, String> complementaryInfos) {
		this.complementaryInfos = complementaryInfos;
	}



	public List<ProductOption> getOptions() {
	    return options;
	}

	public void setOptions(List<ProductOption> options) {
	    this.options.clear();
	    if (options != null) {
	        for (ProductOption option : options) {
	            option.setProduct(this);
	            this.options.add(option);
	        }
	    }
	}



	public Category getCategory() {
		return category;
	}



	public void setCategory(Category category) {
		this.category = category;
	}



	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLieuDeProduction() {
		return lieuDeProduction;
	}

	public void setLieuDeProduction(String lieuDeProduction) {
		this.lieuDeProduction = lieuDeProduction;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public BigDecimal getPrix() {
		return prix;
	}

	public void setPrix(BigDecimal prix) {
		this.prix = prix;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	
}
