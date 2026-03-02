package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public class Exo3 {

    public static void main(String[] args) {
        List<String> ids = List.of("U1", "U2", "U3", "U4", "U5");

        Flux.fromIterable(ids)
            .flatMap(id -> fetchUser(id)
                .onErrorResume(error -> {
                    System.out.println("Erreur pour " + id);
                    return Mono.empty();
                })
            )
            .doOnNext(System.out::println)
            .doOnComplete(() -> System.out.println("Terminé"))
            .blockLast();
    }

    private static Mono<String> fetchUser(String id) {
        if ("U3".equals(id)) {
            return Mono.delay(Duration.ofMillis(200))
                .then(Mono.error(new RuntimeException("Utilisateur introuvable")));
        }

        return Mono.delay(Duration.ofMillis(200))
            .thenReturn("User-" + id);
    }
}
