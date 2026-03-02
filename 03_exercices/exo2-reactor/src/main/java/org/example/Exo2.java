package org.example;

import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

public class Exo2 {
    public static void main(String[] args) {
        AtomicInteger tentatives = new AtomicInteger(0);

        Mono<String> apiCall = Mono.fromCallable(() -> {
            int tentative = tentatives.incrementAndGet();

            if (tentative < 3) {
                System.out.println("Tentative " + tentative + " : Erreur réseau");
                throw new RuntimeException("Erreur réseau");
            }

            String resultat = "Succès!";
            System.out.println("Tentative " + tentative + " : " + resultat);
            return resultat;
        });

        try {
            apiCall
                .retry(2)
                .block();
        } catch (Exception e) {
            System.out.println("Message d'erreur : " + e.getMessage());
        }
    }
}
