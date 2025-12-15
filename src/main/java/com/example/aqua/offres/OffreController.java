package com.example.aqua.offres;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/offres")
public class OffreController {

	@Autowired
	private  OffreService offreService;
	
	 @PostMapping
	    public Offre create(@RequestBody Offre offre) {
		 System.out.println(offre.getId() + offre.getTitre());
	        return offreService.createOffre(offre);
	    }
	    @PutMapping("/{id}")
	    public Offre update(@PathVariable Long id, @RequestBody Offre offre) {
	        return offreService.updateOffre(id, offre);
	    }
	    @DeleteMapping("/{id}")
	    public void delete(@PathVariable Long id) {
	        offreService.deleteOffre(id);
	    }

	    @GetMapping("/{id}")
	    public Optional<Offre> getById(@PathVariable Long id) {
	        return offreService.getOffreById(id);
	    }

	    @GetMapping
	    public List<Offre> getAll() {
	        return offreService.getAll();
	    }
	
	    
	    
	    @PostMapping("/upload")
	    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
	        try {
	            String fileName = offreService.saveImage(file);
	            return ResponseEntity.ok(fileName);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
	        }
	    }
	
}
