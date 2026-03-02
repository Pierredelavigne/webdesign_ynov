package com.example.demo_fondamentaux;

import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

public class Demo4RetryError {
    public static void main(String[] args) {
        // simuler une API qui echoue 3 puis reussit
        AtomicInteger count = new AtomicInteger(0);
        Mono<String> unreliableApi = Mono.fromCallable(() -> {
            if(count.incrementAndGet() < 4){
                throw new RuntimeException("Erreur réseau (tentative "+ count.get()+")");
            }
            return "Succes a la 4eme tentative";
        });

        // retry j'usqu'a 4 fois
        unreliableApi
                .retry(2)
                .subscribe(
                        System.out::println,
                        error -> System.err.println("Echec definitif "+error.getMessage())
                );



    }
}
