package com.example.exercice_5_a_10.exo6;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class OperationController {

    @GetMapping("/processed-numbers")
    public Flux<String> processedNumbers(){
        return Flux.range(1,10)
                .filter(n -> n % 2 == 0)
                .map(i -> i * 10)
                .map(i -> "Processed " + i);
    }
}
