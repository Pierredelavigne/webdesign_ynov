package com.example.tp1webflux.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.tp1webflux.exception.InvalidOrderException;
import com.example.tp1webflux.model.Order;
import com.example.tp1webflux.model.OrderRequest;
import com.example.tp1webflux.model.OrderStatus;
import com.example.tp1webflux.model.Product;
import com.example.tp1webflux.model.ProductWithPrice;
import com.example.tp1webflux.repository.ProductRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final ProductRepository productRepository;

    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Order> processOrder(OrderRequest request) {
        return validateRequest(request)
                .thenMany(Flux.fromIterable(request.getProductIds()))
                .filter(id -> id != null && !id.isBlank())
                .take(100)
                .doOnNext(id -> logger.info("ID reçu et validé: {}", id))

                // batching 10
                .buffer(10)
                .doOnNext(batch -> logger.info("Batch créé ({} IDs) : {}", batch.size(), batch))

                .flatMap(batchIds ->
                        productRepository.findByIds(batchIds)
                                .doOnNext(product -> logger.info("Produit récupéré depuis batch: {}", product.getId()))
                                .onErrorResume(e -> {
                                    logger.warn("Erreur sur batch {} : {}", batchIds, e.getMessage());
                                    return Flux.empty();
                                })
                )

                .filter(Product::isInStock)
                .doOnNext(product -> logger.info("Produit en stock: {}", product.getId()))

                .map(this::applyDiscount)
                .doOnNext(pwp -> logger.info(
                        "Réduction appliquée pour {} => {}%",
                        pwp.getProduct().getId(),
                        pwp.getDiscountPercentage()
                ))

                .collectList()
                .map(products -> buildCompletedOrder(request, products))

                .timeout(Duration.ofSeconds(5))
                .doOnError(error -> logger.error("Erreur durant le traitement de commande", error))
                .onErrorResume(error -> {
                    logger.warn("Création d'une commande FAILED suite à erreur: {}", error.getMessage());
                    return Mono.just(buildFailedOrder(request));
                })
                .doFinally(signal -> logger.info("Fin du traitement de commande - Signal: {}", signal));
    }

    private Mono<Void> validateRequest(OrderRequest request) {
        if (request == null) {
            return Mono.error(new InvalidOrderException("Request must not be null"));
        }

        if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
            return Mono.error(new InvalidOrderException("Customer ID must not be null or blank"));
        }

        if (request.getProductIds() == null || request.getProductIds().isEmpty()) {
            return Mono.error(new InvalidOrderException("Product IDs must not be empty"));
        }

        logger.info("Requête valide pour customerId={}", request.getCustomerId());
        return Mono.empty();
    }

    private ProductWithPrice applyDiscount(Product product) {
        int discount = "ELECTRONICS".equalsIgnoreCase(product.getCategory()) ? 10 : 5;

        BigDecimal originalPrice = product.getPrice();

        BigDecimal discountFactor = BigDecimal.valueOf(100 - discount)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal finalPrice = originalPrice.multiply(discountFactor)
                .setScale(2, RoundingMode.HALF_UP);

        return new ProductWithPrice(product, originalPrice, discount, finalPrice);
    }

    private Order buildCompletedOrder(OrderRequest request, List<ProductWithPrice> products) {
        BigDecimal totalPrice = products.stream()
                .map(ProductWithPrice::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean discountApplied = products.stream()
                .anyMatch(p -> p.getDiscountPercentage() != null && p.getDiscountPercentage() > 0);

        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setProductIds(request.getProductIds());
        order.setProducts(products);
        order.setTotalPrice(totalPrice);
        order.setDiscountApplied(discountApplied);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETED);

        logger.info("Commande complétée: {} produits, total={}", products.size(), totalPrice);
        return order;
    }

    private Order buildFailedOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setProductIds(request != null ? request.getProductIds() : List.of());
        order.setProducts(List.of());
        order.setTotalPrice(BigDecimal.ZERO);
        order.setDiscountApplied(false);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.FAILED);
        return order;
    }
}