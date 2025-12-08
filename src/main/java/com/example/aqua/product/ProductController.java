package com.example.aqua.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.example.aqua.exception.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/products")
//@Tag(name = "Products", description = "CRUD operations for products")
public class ProductController {

	 @Autowired
	 private  ProductService productService;	
	
	 @PostMapping
   // @Operation(summary = "Create a new product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
	 
	    @GetMapping
    //@Operation(summary = "List all products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

	    //  Get product by code
	    @GetMapping("/{code}")
    //@Operation(summary = "Get product by code")
    public ResponseEntity<Product> getProductByCode(@PathVariable Long code) {
        Product product = productService.getProductByCode(code)
                .orElseThrow(() -> new NotFoundException("Product not found with code " + code));
        return ResponseEntity.ok(product);
    }
	    
	    

	    @PutMapping("/{code}")
    @Operation(summary = "Update product by code")
    public ResponseEntity<Product> updateProduct(@PathVariable Long code, @RequestBody Product product) {
        Product updated = productService.updateProduct(code, product);
        return ResponseEntity.ok(updated);
    }

	    @DeleteMapping("/{code}")
    @Operation(summary = "Delete product by code")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long code) {
        productService.deleteProduct(code);
        Map<String, String> body = new HashMap<>();
        body.put("message", "Product deleted successfully");
        return ResponseEntity.ok(body);
    }
	    
	    
	    
	    @GetMapping("/category/{categoryId}")
	    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Long categoryId) {
	        List<Product> products = productService.getProductsByCategoryId(categoryId);
	        return ResponseEntity.ok(products);
	    }
	    
	    @PostMapping("/upload")
	    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
	        try {
	            String fileName = productService.saveImage(file);
	            return ResponseEntity.ok(fileName);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
	        }
	    }
	
}
