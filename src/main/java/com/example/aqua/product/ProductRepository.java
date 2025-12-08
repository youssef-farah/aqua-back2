package com.example.aqua.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product,Long>{

	
	
	@Query("SELECT p FROM Product p WHERE p.category.id_category = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);
}
