package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class OperationController {

    @GetMapping("/api/processed-numbers")
    public Flux<String> processedNumbers() {
        return Flux.range(1, 10)
                .filter(n -> n % 2 == 0)
                .map(n -> n * 10)
                .map(result -> "Processed: " + result);
    }
}
