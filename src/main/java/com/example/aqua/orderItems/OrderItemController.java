package com.example.aqua.orderItems;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/order-items")
@Tag(name = "Order Items", description = "CRUD operations for order items")
public class OrderItemController {

	@Autowired
	private OrderItemService orderItemService;

	@PostMapping
    @Operation(summary = "Create a new order item")
    public ResponseEntity<OrderItems> create(@RequestBody OrderItems orderItem) {
		OrderItems created = orderItemService.create(orderItem);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping
    @Operation(summary = "List all order items")
    public ResponseEntity<List<OrderItems>> getAll() {
		return ResponseEntity.ok(orderItemService.getAll());
	}

	@GetMapping("/{id}")
    @Operation(summary = "Get order item by id")
    public ResponseEntity<OrderItems> getById(@PathVariable Long id) {
		OrderItems item = orderItemService.getById(id)
				.orElseThrow(() -> new NotFoundException("OrderItem not found with id " + id));
		return ResponseEntity.ok(item);
	}

	@PutMapping("/{id}")
    @Operation(summary = "Update order item by id")
    public ResponseEntity<OrderItems> update(@PathVariable Long id, @RequestBody OrderItems updated) {
		OrderItems saved = orderItemService.update(id, updated);
		return ResponseEntity.ok(saved);
	}

	@DeleteMapping("/{id}")
    @Operation(summary = "Delete order item by id")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		orderItemService.delete(id);
		Map<String, String> body = new HashMap<>();
		body.put("message", "Order item deleted successfully");
		return ResponseEntity.ok(body);
	}
}


