package com.example.exercices_1_2_3_4;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public class Exercice3 {

        static Mono<String> fetchUser(String id) {
            if ("U3".equals(id)) {
                return Mono.delay(Duration.ofMillis(200))
                        .flatMap(t -> Mono.error(new RuntimeException("Erreur réseau")));
            }

            return Mono.delay(Duration.ofMillis(200))
                    .map(t -> "User-" + id);
        }

        public static void main(String[] args) {

            List<String> ids = List.of("U1", "U2", "U3", "U4", "U5");

            Flux.fromIterable(ids)
                    .flatMap(id ->
                            fetchUser(id)
                                    .onErrorResume(e -> {
                                        System.out.println("Erreur pour " + id);
                                        return Mono.empty();
                                    })
                    )
                    .doOnNext(System.out::println)
                    .doOnComplete(() -> System.out.println("Terminé"))
                    .blockLast();
        }
}
