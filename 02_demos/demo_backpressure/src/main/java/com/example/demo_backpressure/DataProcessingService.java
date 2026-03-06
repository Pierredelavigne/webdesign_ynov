package com.example.demo_backpressure;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
public class DataProcessingService {
    // Simule une source de donnees rapide (producteur plus rapide que le consommateur)
    public Flux<Integer> getFastDataStream() {
        return Flux.range(1, 100)
                .delayElements(Duration.ofMillis(10)); // emet 1 element toutes les 10ms
    }

    // Backpressure : limitRate pour controler le debit de consommation
    public Flux<Integer> getControlledStream() {
        return Flux.range(1, 50)
                .log()
                .limitRate(10); // le consommateur demande 10 elements a la fois
    }

    // Backpressure : les elements en surplus sont abandonnes si le consommateur est trop lent
    public Flux<Integer> getDroppedStream() {
        return Flux.range(1, 30)
                .onBackpressureDrop(dropped -> System.out.println("Element abandonne : " + dropped));
    }

    // Backpressure : les elements sont mis en buffer avec une taille limitee
    public Flux<Integer> getBufferedStream() {
        return Flux.range(1, 30)
                .onBackpressureBuffer(10, overflow ->
                        System.out.println("Buffer plein, element perdu : " + overflow));
    }

    // publishOn : les transformations lourdes sont deplacees sur un thread dedie
    // utile pour ne pas bloquer le thread Netty de reception
    public Flux<String> getPublishOnStream() {
        return Flux.range(1, 5)
                .map(n -> {
                    System.out.println("Source - thread : " + Thread.currentThread().getName());
                    return n;
                })
                .publishOn(Schedulers.boundedElastic()) // a partir d'ici, execution sur un autre thread
                .map(n -> {
                    System.out.println("Traitement lourd - thread : " + Thread.currentThread().getName());
                    // simulation d'un traitement couteux
                    try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    return "Traite-" + n;
                });
    }

    // subscribeOn : toute la production du flux se fait sur un thread dedie
    // utile pour deplacer une source bloquante (ex : lecture fichier, appel JDBC)
    public Flux<String> getSubscribeOnStream() {
        return Flux.range(1, 5)
                .map(n -> {
                    System.out.println("Production - thread : " + Thread.currentThread().getName());
                    return "Item-" + n;
                })
                .subscribeOn(Schedulers.parallel()); // toute la source s'execute sur un thread parallele
    }

    // Combinaison : flatMap + subscribeOn pour paralleliser des traitements independants
    public Flux<String> getParallelProcessingStream() {
        return Flux.range(1, 5)
                .flatMap(n -> Flux.just(n)
                        .subscribeOn(Schedulers.boundedElastic())
                        .map(id -> {
                            System.out.println("Traitement parallele id=" + id + " - thread : " + Thread.currentThread().getName());
                            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                            return "Resultat-" + id;
                        }));
    }
}
