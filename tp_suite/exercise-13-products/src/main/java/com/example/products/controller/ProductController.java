package com.example.products.controller;

import com.example.products.model.Product;
import com.example.products.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Flux<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public Mono<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        return productService.delete(id);
    }

    @GetMapping("/search")
    public Flux<Product> searchProducts(@RequestParam String name) {
        return productService.searchByName(name);
    }

    @PutMapping("/{id}/buy")
    public Mono<Product> buyProduct(@PathVariable Long id, @RequestParam Integer quantity) {
        return productService.buy(id, quantity);
    }
}
