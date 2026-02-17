package com.example.aqua.category;

import java.util.List;

import com.example.aqua.Useraqua.User;
import com.example.aqua.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categories")
public class Category {
	
	
	  	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id_category;

    @NotBlank
    @Column(nullable = false)
    private String nom;

	    private String description;

	    @ManyToOne
	    @JoinColumn(name = "parent_id", nullable = true)
	    @JsonBackReference
	    private Category parentCategory;

	   
	    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
	    @JsonManagedReference
	    private List<Category> childCategories;
	    

	    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	    @JsonManagedReference(value = "category-products")
	    private List<Product> products;


	    @ManyToOne
	    @JoinColumn(name = "user_id", referencedColumnName = "id_user")
	    @JsonBackReference(value = "user-categories")
	    private User user;
	    
	    private String image;

	    
	    
	    
	    
	    
		public String getImage() {
			return image;
		}


		public void setImage(String image) {
			this.image = image;
		}


		public Long getId_category() {
			return id_category;
		}


		public void setId_category(Long id_category) {
			this.id_category = id_category;
		}


		public String getNom() {
			return nom;
		}


		public void setNom(String nom) {
			this.nom = nom;
		}


		public String getDescription() {
			return description;
		}


		public void setDescription(String description) {
			this.description = description;
		}


		public Category getParentCategory() {
			return parentCategory;
		}


		public void setParentCategory(Category parentCategory) {
			this.parentCategory = parentCategory;
		}


		public List<Category> getChildCategories() {
			return childCategories;
		}


		public void setChildCategories(List<Category> childCategories) {
			this.childCategories = childCategories;
		}


		public List<Product> getProducts() {
			return products;
		}


		public void setProducts(List<Product> products) {
			this.products = products;
		}

		
		

		public User getUser() {
			return user;
		}


		public void setUser(User user) {
			this.user = user;
		}


		public Category(Long id_category, @NotBlank String nom, String description, Category parentCategory,
				List<Category> childCategories, List<Product> products, User user, String image) {
			
			this.id_category = id_category;
			this.nom = nom;
			this.description = description;
			this.parentCategory = parentCategory;
			this.childCategories = childCategories;
			this.products = products;
			this.user = user;
			this.image = image;
		}
	    
		public Category()
		{
			
		}
	    
	    
}