package com.example.exercices_1_2_3_4;

import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

public class Exercice2 {
    public static void main(String[] args) {
        AtomicInteger i = new AtomicInteger(1);

        Mono.fromCallable(()->{
            int n = i.getAndIncrement();
            if(n <= 2){
                System.out.println("Tentative "+n+" : erreur reseau");
                throw new RuntimeException("erreur reseau");
            }
            System.out.println("Tentative "+n+" : succes");
            return "succes";
        }).retry().subscribe();
    }
}
