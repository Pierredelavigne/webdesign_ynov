package com.example.products.service;

import com.example.products.exception.InsufficientStockException;
import com.example.products.model.Product;
import com.example.products.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    public Mono<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Mono<Product> create(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    public Mono<Product> update(Long id, Product product) {
        return productRepository.findById(id)
                .flatMap(existing -> {
                    existing.setName(product.getName());
                    existing.setPrice(product.getPrice());
                    existing.setStock(product.getStock());
                    return productRepository.save(existing);
                });
    }

    public Mono<Void> delete(Long id) {
        return productRepository.deleteById(id);
    }

    public Flux<Product> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Mono<Product> buy(Long id, Integer quantity) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Produit introuvable")))
                .flatMap(product -> {
                    if (quantity <= 0) {
                        return Mono.error(new IllegalArgumentException("La quantité doit être positive"));
                    }
                    if (product.getStock() < quantity) {
                        return Mono.error(new InsufficientStockException("Stock insuffisant pour " + product.getName()));
                    }
                    product.setStock(product.getStock() - quantity);
                    return productRepository.save(product);
                });
    }
}
