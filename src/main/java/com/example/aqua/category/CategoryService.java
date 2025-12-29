package com.example.aqua.category;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aqua.exception.NotFoundException;


@Service
@Transactional
public class CategoryService {

	  @Autowired
	  private  CategoryRepository categoryRepository;
	  
	  public Category createCategory(Category category) {
	        return categoryRepository.save(category);
	    }

	    // Read (get all)
  @Transactional(readOnly = true)
  public List<Category> getAllCategories() {
	  
	        return categoryRepository.findAll();
	    }

	    // Read (get by id)
  @Transactional(readOnly = true)
  public Optional<Category> getCategoryById(Long id) {
	        return categoryRepository.findById(id);
	    }

	    // Update
	    public Category updateCategory(Long id, Category updatedCategory) {
      return categoryRepository.findById(id)
              .map(category -> {
                  category.setNom(updatedCategory.getNom());
                  category.setDescription(updatedCategory.getDescription());
                  System.out.println(updatedCategory.getNom() + updatedCategory.getParentCategory());

                  category.setParentCategory(updatedCategory.getParentCategory());
                 // category.setUser(updatedCategory.getUser());
                  
                  return categoryRepository.save(category);
              })
              .orElseThrow(() -> new NotFoundException("Category not found with id " + id));
	    }

	    // Delete
	    public void deleteCategory(Long id) {
      if (!categoryRepository.existsById(id)) {
          throw new NotFoundException("Category not found with id " + id);
      }
	        categoryRepository.deleteById(id);
	    }
	    
	    
	    
	    // Get all root (main) categories
  @Transactional(readOnly = true)
  public List<Category> getRootCategories() {
	        return categoryRepository.findByParentCategoryIsNull();
	    }

	    // Get all children of a given category
  @Transactional(readOnly = true)
  public List<Category> getChildCategories(Long parentId) {
      Category parent = categoryRepository.findById(parentId)
              .orElseThrow(() -> new NotFoundException("Parent category not found with id " + parentId));
	        return categoryRepository.findByParentCategory(parent);
	    }
	
	

}
