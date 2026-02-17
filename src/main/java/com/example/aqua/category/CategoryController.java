package com.example.aqua.category;

import java.util.List;
import java.util.HashMap;
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
import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "CRUD operations for categories")
public class CategoryController {
	
	@Autowired
	private  CategoryService categoryService;
	
    
    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
    	System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxx"+category.getId_category());
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

	    @GetMapping
    @Operation(summary = "List all categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

	    @GetMapping("/{id}")
    @Operation(summary = "Get category by id")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id " + id));
        return ResponseEntity.ok(category);
    }

	    
	    @PutMapping("/{id}")
    @Operation(summary = "Update category by id")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updated);
    }
	   
	    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        Map<String, String> body = new HashMap<>();
        body.put("message", "Category deleted successfully");
        return ResponseEntity.ok(body);
    }
	    
	    @GetMapping("/parents")
    @Operation(summary = "List all root categories")
    public ResponseEntity<List<Category>> getAllParentCategories() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }
	    
	    
	    
	    @GetMapping("children/{id}")
    @Operation(summary = "List children of a category")
    public ResponseEntity<List<Category>> getChildren(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getChildCategories(id));
    }
	    
	    
	    @PostMapping("/upload")
	    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
	        try {
	            String fileName = categoryService.saveImage(file);
	            return ResponseEntity.ok(fileName);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
	        }
	    }
	
}
