package com.example.exercices_1_2_3_4;

import reactor.core.publisher.Mono;

import java.time.Duration;

public class Exercice4 {

    static Mono<String> getFirstName(){
        return Mono.delay(Duration.ofMillis(500)).map(i -> "Jean");
    }

    static Mono<String> getLastName(){
        return Mono.delay(Duration.ofMillis(800)).map(i -> "Dupont");
    }

    public static void main(String[] args) {
        Mono.zip(getFirstName(), getLastName())
                .map(tuple -> tuple.getT1() + " " + tuple.getT2())
                .map(String::toUpperCase)
                .doOnNext(System.out::println)
                .block();


    }
}
