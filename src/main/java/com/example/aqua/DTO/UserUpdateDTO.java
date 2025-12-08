package com.example.aqua.DTO;

import com.example.aqua.Useraqua.Adresse;



public class UserUpdateDTO {
    private String nom;
    private String prenom;
    private String telephone;
    private Adresse adresse;
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

	
	public UserUpdateDTO()
	{
		
	}
    
    
    
}