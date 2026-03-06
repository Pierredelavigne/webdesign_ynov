package com.example.demo_bdd_mongodb_webflux;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {
//public interface ItemRepository extends ReactiveCrudRepository<Item, String> {
    Flux<Item> findByName(String name);
}
