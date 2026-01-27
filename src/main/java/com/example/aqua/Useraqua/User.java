package com.example.aqua.Useraqua;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.aqua.Order.Order;
import com.example.aqua.category.Category;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name="users")
@Schema(description = "users")
public class User implements UserDetails{


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id_user;
	@Email
	@NotBlank
	@Column(nullable = false, unique = true)
	private String mail;
	
	@Column(nullable = true)  // CHANGED from nullable = false
	private String password;
	
	
	@NotBlank
	@Column(nullable = false)
	private String role;
	private String nom;
	private String prenom;
	private String telephone;
	@Embedded
	private Adresse adresse;
	
	
	  	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	  	@JsonManagedReference(value = "user-categories")
		@JsonIgnore
	    private List<Category> categories;
	
	  	
	  	
	  	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	    @JsonManagedReference(value = "user-orders")
	    @JsonIgnore
	    private List<Order> orders;
	  	
	 // Add these new fields
	  	@Column(nullable = false)
	  	private boolean enabled = false;  // Default to false

	  	@Column
	  	private String verificationToken;

	  	@Column
	  	private LocalDateTime verificationTokenExpiry;
	  	
	  	
	  	
	  	
	  	
	  	@Enumerated(EnumType.STRING)
	  	@Column(nullable = false)
	  	private AuthProvider authProvider = AuthProvider.LOCAL;

	  	@Column
	  	private String googleId;

	  	
	  	
	  	
	  	
	  	
	  	public User(Long id_user, @Email @NotBlank String mail, String password, 
	            @NotBlank String role, String nom, String prenom, String telephone, 
	            Adresse adresse, List<Category> categories, List<Order> orders, 
	            boolean enabled, String verificationToken, 
	            LocalDateTime verificationTokenExpiry, 
	            AuthProvider authProvider, String googleId) {
	    this.id_user = id_user;
	    this.mail = mail;
	    this.password = password;
	    this.role = role;
	    this.nom = nom;
	    this.prenom = prenom;
	    this.telephone = telephone;
	    this.adresse = adresse;
	    this.categories = categories;
	    this.orders = orders;
	    this.enabled = enabled;
	    this.verificationToken = verificationToken;
	    this.verificationTokenExpiry = verificationTokenExpiry;
	    this.authProvider = authProvider != null ? authProvider : AuthProvider.LOCAL;
	    this.googleId = googleId;
	}
		
	  	
	  	
	  	
	  	
	  	
	  	
	  	
		public User()
		{}

		
		 public List<Order> getOrders() {
		        return orders;
		    }

		    public void setOrders(List<Order> orders) {
		        this.orders = orders;
		    }
		    
		    
		public Long getId_user() {
			return id_user;
		}

		public void setId_user(Long id_user) {
			this.id_user = id_user;
		}

		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public String getNom() {
			return nom;
		}

		public void setNom(String nom) {
			this.nom = nom;
		}

		public String getPrenom() {
			return prenom;
		}

		public void setPrenom(String prenom) {
			this.prenom = prenom;
		}

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		public Adresse getAdresse() {
			return adresse;
		}

		public void setAdresse(Adresse adresse) {
			this.adresse = adresse;
		}

		public List<Category> getCategories() {
			return categories;
		}
		@JsonIgnore
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
		    String springRole = role.startsWith("ROLE_")
		            ? role
		            : "ROLE_" + role.toUpperCase();

		    return List.of(new SimpleGrantedAuthority(springRole));
		}


		@Override
		public String getUsername() {
			// TODO Auto-generated method stub
			 return mail;		}


		
		@Override
		public boolean isEnabled() {
		    return enabled;
		}

		@Override
		public boolean isAccountNonExpired() {
		    return true;	
		}

		@Override
		public boolean isAccountNonLocked() {
		    return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
		    return true;
		}


	

		public AuthProvider getAuthProvider() {
		    return authProvider;
		}

		public void setAuthProvider(AuthProvider authProvider) {
		    this.authProvider = authProvider;
		}

		public String getGoogleId() {
		    return googleId;
		}

		public void setGoogleId(String googleId) {
		    this.googleId = googleId;
		}
		
	
}
