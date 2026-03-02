package com.example.exercices_1_2_3_4;

import reactor.core.publisher.Flux;

public class Exercice1 {
    public static void main(String[] args) {
        Flux.range(1, 10)
                .map(i -> i*3)
                .filter(i -> i > 15)
                .subscribe(System.out::println);
    }
}
