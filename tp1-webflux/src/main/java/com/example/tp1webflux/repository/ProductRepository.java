package com.example.tp1webflux.repository;

import com.example.tp1webflux.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ProductRepository {

    private final Map<String, Product> database = new ConcurrentHashMap<>();
    private final Duration latency;
    private final double randomErrorRate;
    private final Set<String> forcedErrorIds;

    public ProductRepository() {
        this(Duration.ofMillis(100), 0.0, Set.of());
    }

    public ProductRepository(Duration latency) {
        this(latency, 0.0, Set.of());
    }

    public ProductRepository(Duration latency, double randomErrorRate, Set<String> forcedErrorIds) {
        this.latency = latency;
        this.randomErrorRate = randomErrorRate;
        this.forcedErrorIds = forcedErrorIds == null ? Set.of() : forcedErrorIds;
        initData();
    }

    private void initData() {
        database.put("PROD001", new Product("PROD001", "Laptop", new BigDecimal("1000.00"), 5, "ELECTRONICS"));
        database.put("PROD002", new Product("PROD002", "Mouse", new BigDecimal("50.00"), 10, "ELECTRONICS"));
        database.put("PROD003", new Product("PROD003", "Book", new BigDecimal("20.00"), 15, "BOOKS"));
        database.put("PROD004", new Product("PROD004", "Chair", new BigDecimal("150.00"), 0, "FURNITURE"));
        database.put("PROD005", new Product("PROD005", "Bottle", new BigDecimal("10.00"), 2, "HOME"));
    }

    // 1) Mono<Product> findById(String id) : Mono.empty() si n'existe pas
    public Mono<Product> findById(String id) {
        return Mono.delay(latency)
                .flatMap(tick -> maybeFail(id))
                .flatMap(tick -> Mono.justOrEmpty(database.get(id)));
    }

    // 2) Flux<Product> findByIds(List<String> ids)
    public Flux<Product> findByIds(List<String> ids) {
        return Flux.fromIterable(ids)
                .flatMap(this::findById);
    }

    // 3) Mono<Integer> getStock(String productId)
    public Mono<Integer> getStock(String productId) {
        return Mono.delay(latency)
                .flatMap(tick -> maybeFail(productId))
                .map(tick -> {
                    Product product = database.get(productId);
                    return product == null ? 0 : product.getStock();
                });
    }

    // 4) Existence helper
    public Mono<Boolean> existsById(String productId) {
        return findById(productId)
                .map(p -> true)
                .defaultIfEmpty(false);
    }

    // 5) Flux<Product> findAll()
    public Flux<Product> findAll() {
        return Flux.fromIterable(database.values())
                .delayElements(latency);
    }

    private Mono<Long> maybeFail(String productId) {
        if (forcedErrorIds.contains(productId)) {
            return Mono.error(new RuntimeException("Forced repository error for " + productId));
        }
        if (randomErrorRate > 0 && Math.random() < randomErrorRate) {
            return Mono.error(new RuntimeException("Random repository error for " + productId));
        }
        return Mono.just(1L);
    }
}
