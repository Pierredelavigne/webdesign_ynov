package com.example.demo_fondamentaux;

import reactor.core.publisher.Flux;

public class Demo5Zip {
    public static void main(String[] args) {
        Flux<String> users = Flux.just("Toto","Tata","Titi");
        Flux<Integer> orderCounts = Flux.just(5,8,9);
        Flux<String> cities = Flux.just("Paris","Lille","Lyon");

        // Combiner en paire synchronisées
        Flux.zip(users,orderCounts)
                .subscribe(tuple ->
                        System.out.println(tuple.getT1()+ " a "+ tuple.getT2() + " commandes"));


        Flux.zip(users,orderCounts,cities)
                .subscribe(tuple ->
                        System.out.println(tuple.getT1()+ " a "+ tuple.getT2() + " commandes et habite a " +tuple.getT3()));

        Flux<String> users2 = Flux.just("Toto","Tata","Titi");
        Flux<Integer> orderCounts2 = Flux.just(5,8);

        Flux.zip(users2,orderCounts2)
                .subscribe(tuple ->
                        System.out.println(tuple.getT1()+ " a "+ tuple.getT2() + " commandes"));
    }
}
