package org.example;

import reactor.core.publisher.Flux;

public class Exo1 {
    public static void main(String[] args) {
        Flux.range(1, 10)
            .map(n -> n * 3)
            .filter(n -> n > 15)
            .subscribe(System.out::println);
    }
}
