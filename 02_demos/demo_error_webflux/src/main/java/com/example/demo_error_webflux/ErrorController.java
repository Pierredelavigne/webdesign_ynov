package com.example.demo_error_webflux;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/errors")
public class ErrorController {

    @GetMapping("/{id}")
    public Mono<String> error(@PathVariable String id) {
        return Mono.just(id)
                .map(value -> {
                    if("error".equals(value)) {
                        throw new IllegalArgumentException("ID non valide");
                    }
                    return "ID valide : "+value;
                })
                .onErrorResume(ex -> Mono.just("erreur gerer localement : "+ex.getMessage()));
    }

    @GetMapping("/global")
    public Mono<String> globalError() {
        return Mono.error(new RuntimeException("Global error lancé !!!!"));
    }
}
