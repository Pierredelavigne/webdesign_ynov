package com.example.exercice_5_a_10.exo7;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class ErrorHandlingController {

    @GetMapping("/error-resume")
    public Flux<String> errorResume() {
        return Flux.just("A", "B", "C","D","E","F")
                .map(value -> {
                    if(value.equals("C")){
                        throw new RuntimeException("Erreur simuler");
                    }
                    return value;
                })
                .onErrorResume(e -> Flux.just("Default1", "Default2"));
    }

    @GetMapping("/error-continue")
    public Flux<Integer> errorResumeMono() {
        return Flux.just(1, 2, 3, 4, 5)
                .map(value -> {
                    if (value == 2){
                        throw new RuntimeException("Erreur simuler");
                    }
                    return value;
                }).onErrorContinue((e,value) -> {
                    System.err.println("Erreur : " + value +" "+e.getMessage());
                });
    }


}
