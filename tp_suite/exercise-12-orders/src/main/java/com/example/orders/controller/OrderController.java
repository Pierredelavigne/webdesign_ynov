package com.example.orders.controller;

import com.example.orders.dto.OrderStatusUpdateRequest;
import com.example.orders.model.Order;
import com.example.orders.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Flux<Order> getAllOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Order> getOrderById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> createOrder(@RequestBody Order order) {
        return orderService.create(order);
    }

    @PutMapping("/{id}")
    public Mono<Order> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteOrder(@PathVariable Long id) {
        return orderService.delete(id);
    }

    @GetMapping("/search")
    public Flux<Order> searchByStatus(@RequestParam String status) {
        return orderService.findByStatus(status);
    }

    @GetMapping("/paged")
    public Flux<Order> getPaged(@RequestParam int page, @RequestParam int size) {
        return orderService.findPaged(page, size);
    }

    @GetMapping("/customer/{customerName}")
    public Flux<Order> getByCustomer(@PathVariable String customerName) {
        return orderService.findByCustomerName(customerName);
    }
}
