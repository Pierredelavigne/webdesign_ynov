package com.example.demo_api_rest_webflux;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    public ProductService(){
        // prodduit de test
        products.put("1",new Product("1","Livre", BigDecimal.valueOf(0.99)));
        products.put("2",new Product("2","Tomate", BigDecimal.valueOf(17.45)));
        products.put("3",new Product("2","Banane", BigDecimal.valueOf(17.45)));
        products.put("4",new Product("2","Bonbon", BigDecimal.valueOf(17.45)));
    }

    public Flux<Product> getAllProducts(){
        return Flux.fromIterable(products.values());
    }

    public Flux<Product> getAllProductsDelay() {
        return Flux
                .fromIterable(products.values())
                .delayElements(Duration.ofSeconds(1));
    }


    public Mono<Product> getProductById(String id){
        return Mono.justOrEmpty(products.get(id));
    }

    public Mono<Product> createProduct(Product product){
        String id = UUID.randomUUID().toString();
        product.setId(id);
        products.put(id,product);
        return Mono.just(product);
    }


}
