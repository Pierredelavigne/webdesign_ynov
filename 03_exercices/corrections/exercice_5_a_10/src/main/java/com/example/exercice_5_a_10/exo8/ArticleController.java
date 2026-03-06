package com.example.exercice_5_a_10.exo8;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @GetMapping
    public Flux<String> getArticles() {
        return Flux.just(
                new Article(UUID.randomUUID().toString(),"Introduction to Spring WebFlux"),
                new Article(UUID.randomUUID().toString(),"Reactive Programming with Project Reactor"),
                new Article(UUID.randomUUID().toString(),"Building APIs with Spring Boot")
        ).map(Article::getTitle);
    }
}
