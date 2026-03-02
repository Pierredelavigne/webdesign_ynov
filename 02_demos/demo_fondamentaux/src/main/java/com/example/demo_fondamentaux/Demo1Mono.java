package com.example.demo_fondamentaux;

import reactor.core.publisher.Mono;

public class Demo1Mono {
    public static void main(String[] args) {
        // Creer un Mono avec une valeur
        Mono<String> mono = Mono.just("Hello Webflux !!!");

        // S'abonner et traiter le resultat
        mono.subscribe(
                value -> System.out.println("Recu : "+value), // onNext
                error -> System.out.println("Erreur : "+error), // onError
                () -> System.out.println("Terminé !!!!")
        );
    }
}
