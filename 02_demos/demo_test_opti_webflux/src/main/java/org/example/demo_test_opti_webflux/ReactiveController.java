package org.example.demo_test_opti_webflux;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/test")
public class ReactiveController {

    private final WebClient webClient;

    public ReactiveController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/single")
    public Mono<String> getSingle() {
        return Mono.just("Hello World !");
    }

    @GetMapping("/stream")
    public Flux<Integer> getStream() {
        return Flux.just(1, 2, 3,4);
    }

    @GetMapping("/call-single")
    public Mono<String> callSingle() {
        return webClient.get()
                .uri("/api/test/single")
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/call-stream")
    public Flux<Integer> callStream() {
        return webClient.get()
                .uri("/api/test/stream")
                .retrieve()
                .bodyToFlux(Integer.class);
    }
}
