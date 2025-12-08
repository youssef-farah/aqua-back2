package com.example.aqua.offres;

import java.nio.file.Path;
import java.nio.file.Paths;


import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.aqua.exception.ConflictException;
import com.example.aqua.exception.NotFoundException;


@Service
public class OffreService {

	
	 @Autowired
	 private  OffreRepository offreRepository;

	 public Offre createOffre(Offre offre) {
	        // Since code is manually set, check if it already exists
	        if (offreRepository.existsById(offre.getId())) {
	            throw new ConflictException("Product with code " + offre.getId() + " already exists");
	        }
	        return offreRepository.save(offre);
	    }

	    
	    public List<Offre> getAll() {
	        return offreRepository.findAll();
	    }

	    
	    public Optional<Offre> getOffreById(Long code) {
	        return offreRepository.findById(code);
	    }

	    // Update
	    public Offre updateOffre(Long id, Offre updatedOffre) {
	        return offreRepository.findById(id)
	                .map(offre -> {
	                    offre.setId(updatedOffre.getId());
	                    offre.setDescription(updatedOffre.getDescription());
	                    offre.setImageUrl(updatedOffre.getImageUrl());
	                    offre.setPrix(updatedOffre.getPrix());
	                  
	                    return offreRepository.save(offre);
	                })
	                .orElseThrow(() -> new NotFoundException("Product not found with code " + id));
	    }

	    // Delete
	    public void deleteOffre(Long code) {
	        if (!offreRepository.existsById(code)) {
	            throw new NotFoundException("Product not found with code " + code);
	        }
	        offreRepository.deleteById(code);
	    }
	    
	    
	    
	    //private final String uploadDir = "uploads/";

	    public String saveImage(MultipartFile file) throws IOException {
	        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
	        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");

	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        Path filePath = uploadPath.resolve(fileName);
	        Files.write(filePath, file.getBytes());

	        // Return only the filename
	        return fileName;
	    }

	
}
