package com.example.aqua.offres;


import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "offres")
public class Offre {

	
	  	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String titre;

	    @Column(length = 2000)
	    private String description;

	    @Nullable
	    private String imageUrl;

	    private Double prix;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
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

		public String getImageUrl() {
			return imageUrl;
		}

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public Double getPrix() {
			return prix;
		}

		public void setPrix(Double prix) {
			this.prix = prix;
		}

		public Offre(Long id, String titre, String description, String imageUrl, Double prix) {
			
			this.id = id;
			this.titre = titre;
			this.description = description;
			this.imageUrl = imageUrl;
			this.prix = prix;
		}
	    
	    
	    public Offre()
	    {
	    	
	    }
	    
	    /*test*/
	
}
