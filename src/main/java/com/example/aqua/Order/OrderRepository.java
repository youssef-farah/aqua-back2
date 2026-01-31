package com.example.aqua.Order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

	
	  @Query("SELECT o FROM Order o WHERE o.user.id_user = :userId")
	    List<Order> findOrdersByUserId(@Param("userId") Long userId);
	  
	  
	  
	    Optional<Order> findByPaymentId(String paymentId);


}