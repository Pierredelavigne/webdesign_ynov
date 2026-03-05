package com.example.tp1webflux.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private String orderId;
    private List<String> productIds;
    private List<ProductWithPrice> products;
    private BigDecimal totalPrice;
    private Boolean discountApplied;
    private LocalDateTime createdAt;
    private OrderStatus status;

    public Order() {
    }

    public Order(String orderId,
                 List<String> productIds,
                 List<ProductWithPrice> products,
                 BigDecimal totalPrice,
                 Boolean discountApplied,
                 LocalDateTime createdAt,
                 OrderStatus status) {
        this.orderId = orderId;
        this.productIds = productIds;
        this.products = products;
        this.totalPrice = totalPrice;
        this.discountApplied = discountApplied;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public List<ProductWithPrice> getProducts() {
        return products;
    }

    public void setProducts(List<ProductWithPrice> products) {
        this.products = products;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Boolean getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(Boolean discountApplied) {
        this.discountApplied = discountApplied;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
