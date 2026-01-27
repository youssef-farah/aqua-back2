package com.example.aqua.Order;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aqua.exception.NotFoundException;


@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	public Order create(Order order) {
		return orderRepository.save(order);
	}

	@Transactional(readOnly = true)
	public List<Order> getAll() {
		return orderRepository.findAll();
	}	

	@Transactional(readOnly = true)
	public Optional<Order> getById(Long id) {
		return orderRepository.findById(id);
	}
	
	
	  @Transactional(readOnly = true)
	    public List<Order> getByUserId(Long userId) {
	        return orderRepository.findOrdersByUserId(userId);
	    }

	public Order update(Long id, Order updated) {
		return orderRepository.findById(id).map(o -> {
			// Update fields that make sense at order level
			o.setState(updated.getState());
			// Optionally replace items if provided (cascade will persist)
			if (updated.getItems() != null) {
				o.setItems(updated.getItems());
			}
			return orderRepository.save(o);
		}).orElseThrow(() -> new NotFoundException("Order not found with id " + id));
	}

	public void delete(Long id) {
		if (!orderRepository.existsById(id)) {
			throw new NotFoundException("Order not found with id " + id);
		}
		orderRepository.deleteById(id);
	}
}


