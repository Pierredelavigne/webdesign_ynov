package org.example;

import reactor.core.publisher.Mono;

import java.time.Duration;

public class Exo4 {

    public static Mono<String> getFirstName() {
        return Mono.just("Jean").delayElement(Duration.ofMillis(500));
    }

    public static Mono<String> getLastName() {
        return Mono.just("Dupont").delayElement(Duration.ofMillis(800));
    }

    public static void main(String[] args) {
        Mono.zip(getFirstName(), getLastName())
            .map(tuple -> tuple.getT1() + " " + tuple.getT2())
            .map(String::toUpperCase)
            .subscribe(System.out::println);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
