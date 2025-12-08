package com.example.aqua.orderItems;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aqua.exception.NotFoundException;


@Service
@Transactional
public class OrderItemService {

	@Autowired
	private OrderItemsRepository orderItemRepository;

	public OrderItems create(OrderItems orderItem) {
		return orderItemRepository.save(orderItem);
	}

	@Transactional(readOnly = true)
	public List<OrderItems> getAll() {
		return orderItemRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<OrderItems> getById(Long id) {
		return orderItemRepository.findById(id);
	}

	public OrderItems update(Long id, OrderItems updated) {
		return orderItemRepository.findById(id).map(oi -> {
			oi.setOrder(updated.getOrder());
			oi.setProduct(updated.getProduct());
			oi.setQuantity(updated.getQuantity());
			oi.setUnitPrice(updated.getUnitPrice());
			// subtotal recomputed by entity lifecycle
			return orderItemRepository.save(oi);
		}).orElseThrow(() -> new NotFoundException("OrderItem not found with id " + id));
	}

	public void delete(Long id) {
		if (!orderItemRepository.existsById(id)) {
			throw new NotFoundException("OrderItem not found with id " + id);
		}
		orderItemRepository.deleteById(id);
	}
}


