package com.example.aqua.Order;



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
import org.springframework.web.bind.annotation.RestController;

import com.example.aqua.exception.NotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "CRUD operations for orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<Order> create(@RequestBody Order order) {
		Order created = orderService.create(order);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
	
    @PreAuthorize("hasRole('ADMIN')")
	@GetMapping
    @Operation(summary = "List all orders")
    public ResponseEntity<List<Order>> getAll() {
		return ResponseEntity.ok(orderService.getAll());
	}
	
    @PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
		Order order = orderService.getById(id).orElseThrow(() -> new NotFoundException("Order not found with id " + id));
		return ResponseEntity.ok(order);
	}
    @PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
    @Operation(summary = "Update order by id")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order updated) {
		Order saved = orderService.update(id, updated);
		return ResponseEntity.ok(saved);
	}
    
    @PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
    @Operation(summary = "Delete order by id")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		orderService.delete(id);
		Map<String, String> body = new HashMap<>();
		body.put("message", "Order deleted successfully");
		return ResponseEntity.ok(body);
	}
}

