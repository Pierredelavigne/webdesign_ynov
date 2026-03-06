package com.example.demo_backpressure;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class ConsoleBackpressureSchedulerDemo {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== DEMO BACKPRESSURE ===");

        // --- 1. Backpressure avec limitRate ---
        // Le consommateur demande les elements par paquets de 5 au lieu de tout recevoir d'un coup
        System.out.println("\n-- limitRate(5) : consommation par lots de 5 --");
        Flux.range(1, 20)
                .log() // permet de voir les requetes de backpressure dans la console
                .limitRate(5)
                .subscribe(n -> System.out.println("Recu : " + n));

        // --- 2. Backpressure avec onBackpressureDrop ---
        // Si le consommateur est trop lent, les elements en surplus sont abandonnes
        System.out.println("\n-- onBackpressureDrop : les elements non consommes sont abandonnes --");
        Flux.range(1, 10)
                .onBackpressureDrop(dropped -> System.out.println("Abandonne : " + dropped))
                .subscribe(n -> System.out.println("Traite : " + n));

        // --- 3. Backpressure avec onBackpressureBuffer ---
        // Les elements en surplus sont mis en attente dans un buffer
        System.out.println("\n-- onBackpressureBuffer : les elements en surplus sont bufferises --");
        Flux.range(1, 10)
                .onBackpressureBuffer(5, overflow -> System.out.println("Buffer plein, element perdu : " + overflow))
                .subscribe(n -> System.out.println("Traite depuis buffer : " + n));


        System.out.println("\n=== DEMO SCHEDULERS ===");

        // --- 4. publishOn : change le thread pour les operations en aval ---
        // Tout ce qui est APRES publishOn s'execute sur le scheduler donne
        System.out.println("\n-- publishOn(boundedElastic) : le map s'execute sur un autre thread --");
        Flux.range(1, 3)
                .map(n -> {
                    System.out.println("Avant publishOn - thread : " + Thread.currentThread().getName());
                    return n;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(n -> {
                    System.out.println("Apres publishOn - thread : " + Thread.currentThread().getName());
                    return n * 10;
                })
                .subscribe(n -> System.out.println("Valeur : " + n + " - thread : " + Thread.currentThread().getName()));

        Thread.sleep(500); // laisser le temps aux threads async de se terminer

        // --- 5. subscribeOn : change le thread de la SOURCE elle-meme ---
        // Tout le flux (y compris la production) s'execute sur le scheduler donne
        System.out.println("\n-- subscribeOn(parallel) : toute la production s'execute sur un thread parallele --");
        Flux.range(1, 3)
                .map(n -> {
                    System.out.println("Production - thread : " + Thread.currentThread().getName());
                    return n;
                })
                .subscribeOn(Schedulers.parallel())
                .subscribe(n -> System.out.println("Reception : " + n + " - thread : " + Thread.currentThread().getName()));

        Thread.sleep(500);

        // --- 6. Cas pratique : tache bloquante deplacee sur boundedElastic ---
        // boundedElastic est le scheduler recommande pour les appels bloquants (I/O, BDD, etc.)
        System.out.println("\n-- Cas pratique : simulation d'un appel bloquant sur boundedElastic --");
        Flux.range(1, 3)
                .flatMap(n -> Flux.just(n)
                        .subscribeOn(Schedulers.boundedElastic())
                        .map(id -> {
                            System.out.println("Traitement id=" + id + " sur thread : " + Thread.currentThread().getName());
                            // simulation d'un appel bloquant (ex : appel BDD synchrone)
                            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                            return "Resultat-" + id;
                        }))
                .subscribe(result -> System.out.println("Resultat recu : " + result));

        Thread.sleep(1000); // attente fin des traitements paralleles
    }
}
