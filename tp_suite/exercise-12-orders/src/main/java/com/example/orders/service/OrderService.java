package com.example.orders.service;

import com.example.orders.dto.OrderStatusUpdateRequest;
import com.example.orders.model.Order;
import com.example.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Flux<Order> findAll() {
        return orderRepository.findAll();
    }

    public Mono<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Mono<Order> create(Order order) {
        order.setId(null);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Mono<Order> updateStatus(Long id, OrderStatusUpdateRequest request) {
        return orderRepository.findById(id)
                .flatMap(order -> {
                    order.setStatus(request.getStatus());
                    return orderRepository.save(order);
                });
    }

    public Mono<Void> delete(Long id) {
        return orderRepository.deleteById(id);
    }

    public Flux<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public Flux<Order> findByCustomerName(String customerName) {
        return orderRepository.findByCustomerNameIgnoreCase(customerName);
    }

    public Flux<Order> findPaged(int page, int size) {
        return orderRepository.findAll()
                .skip((long) page * size)
                .take(size);
    }
}
