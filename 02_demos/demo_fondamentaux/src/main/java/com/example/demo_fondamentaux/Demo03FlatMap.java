package com.example.demo_fondamentaux;

import reactor.core.publisher.Flux;

import java.util.function.Function;

public class Demo03FlatMap {

    public static void main(String[] args) {
        // Simuler une API : User -> Commandes
        // pour un userId done, on retourne un flux de commande
        Function<Integer, Flux<String>> getOrderForUser = userId ->
                Flux.just("Order"+userId+"-1","Order"+userId+"-2");

        // Appliquer flatMap
        Flux.range(1, 3) // generer les userid 1 a 3
                .flatMap(userId -> getOrderForUser.apply(userId))
                .subscribe(System.out::println);

    }
}
