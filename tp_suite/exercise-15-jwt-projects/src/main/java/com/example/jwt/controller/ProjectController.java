package com.example.jwt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
public class ProjectController {
    @GetMapping("/api/projects")
    public Mono<Map<String, Object>> getProjects(Authentication authentication) {
        return Mono.just(Map.of(
                "user", authentication.getName(),
                "projects", List.of("Projet A", "Projet B")
        ));
    }
}
