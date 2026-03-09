package com.example.aqua.mail;

import jakarta.validation.constraints.NotBlank;

public class ContactRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String phone;

    private String email;

    // Construction fields
    private String shape;
    private Double surface;
    private String depth;
    private Double volume;
    private String estimatedPrice;

    // Maintenance fields
    private String selectedOffer;

    // ─── Getters & Setters ───────────────────────────────────────────

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getShape() { return shape; }
    public void setShape(String shape) { this.shape = shape; }

    public Double getSurface() { return surface; }
    public void setSurface(Double surface) { this.surface = surface; }

    public String getDepth() { return depth; }
    public void setDepth(String depth) { this.depth = depth; }

    public Double getVolume() { return volume; }
    public void setVolume(Double volume) { this.volume = volume; }

    public String getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(String estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public String getSelectedOffer() { return selectedOffer; }
    public void setSelectedOffer(String selectedOffer) { this.selectedOffer = selectedOffer; }
}
