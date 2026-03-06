package com.example.orders.repository;

import com.example.orders.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    Flux<Order> findByStatus(String status);
    Flux<Order> findByCustomerNameIgnoreCase(String customerName);
}
