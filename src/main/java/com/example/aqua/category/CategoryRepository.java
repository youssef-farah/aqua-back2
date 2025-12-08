package com.example.aqua.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long>{

	
	  List<Category> findByParentCategory(Category parentCategory);

	    // Find all root categories (where parentCategory is null)
	//jpa automatically translates method name to sql query => WHERE parentCategory IS NULL
	    List<Category> findByParentCategoryIsNull();
}
