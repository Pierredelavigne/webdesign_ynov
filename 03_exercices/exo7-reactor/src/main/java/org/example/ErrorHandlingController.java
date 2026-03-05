package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ErrorHandlingController {

    @GetMapping("/api/error-resume")
    public Flux<String> errorResume() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Boom after C")))
                .onErrorResume(e -> Flux.just("Default1", "Default2"));
    }

    @GetMapping("/api/error-continue")
    public Flux<Integer> errorContinue() {
        return Flux.range(1, 5)
                .handle((value, sink) -> {
                    if (value == 2) {
                        sink.error(new RuntimeException("Error on 2"));
                    } else {
                        sink.next(value);
                    }
                })
                .cast(Integer.class)
                .onErrorContinue((error, value) -> {
                    // ignore l'erreur et continue
                });
    }
}
