package com.example.demo_api_rest_webflux;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class ProductHandler {

    private final ProductService productService;

    public ProductHandler(ProductService productService) {
        this.productService = productService;
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        return ok().body(productService.getAllProducts(), Product.class);
    }
    public Mono<ServerResponse> getAllProductsDelay(ServerRequest request) {
        return ok().body(productService.getAllProductsDelay(), Product.class);
    }

    public Mono<ServerResponse> getProductById(ServerRequest request) {
        String id = request.pathVariable("id");
        return productService.getProductById(id)
                .flatMap(product -> ok().bodyValue(product))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .flatMap(productService::createProduct)
                .flatMap(product -> created(request.uri()).bodyValue(product));
    }

}
