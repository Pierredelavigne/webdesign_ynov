package com.example.demo_fondamentaux;

import reactor.core.publisher.Flux;

public class Demo2FluxMapFilter {
    public static void main(String[] args) {
        // Creer un Flux de nombres
        Flux<Integer> numbers = Flux.range(1, 10);

        // Transformer et filtrer
        numbers.map(i -> i * 2)
                .filter(i -> i > 5 )
                .subscribe(System.out::println);
    }
}
