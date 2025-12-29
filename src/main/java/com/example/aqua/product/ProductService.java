package com.example.aqua.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.aqua.exception.NotFoundException;

import java.io.IOException;
import java.nio.file.Files;

import java.util.UUID;

import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductService {
	
	
	
	@Autowired
	private ProductRepository productRepository;
	
	
	 // Create
    public Product createProduct(Product product) {
        // Since code is manually set, check if it already exists
        if (productRepository.existsById(product.getCode())) {
            //throw new ConflictException("Product with code " + product.getCode() + " already exists");
        }
        return productRepository.save(product);
    }

    // Read all
    //@Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Read by code
    //@Transactional(readOnly = true)
    public Optional<Product> getProductByCode(Long code) {
        return productRepository.findById(code);
    }
    
    

    // Update
    public Product updateProduct(Long code, Product updatedProduct) {
        return productRepository.findById(code)
                .map(product -> {
                    product.setTitre(updatedProduct.getTitre());
                    product.setDescription(updatedProduct.getDescription());
                    product.setLieuDeProduction(updatedProduct.getLieuDeProduction());
                    product.setStock(updatedProduct.getStock());
                    product.setPrix(updatedProduct.getPrix());
                    product.setImage(updatedProduct.getImage());
                    product.setCategory(updatedProduct.getCategory());
                   
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new NotFoundException("Product not found with code " + code));
    }

    public Product updateProductStock(Long code, int stock) {
        return productRepository.findById(code)
                .map(product -> {
                    product.setStock(stock);
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new NotFoundException("Product not found with code " + code));
    }
    
    
    // Delete
    public void deleteProduct(Long code) {
        if (!productRepository.existsById(code)) {
            throw new NotFoundException("Product not found with code " + code);
        }
        productRepository.deleteById(code);
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


    
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findProductsByCategoryId(categoryId);
    }
}
