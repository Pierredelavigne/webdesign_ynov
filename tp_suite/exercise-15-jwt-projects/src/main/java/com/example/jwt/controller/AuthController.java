package com.example.jwt.controller;

import com.example.jwt.dto.LoginRequest;
import com.example.jwt.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Mono<Map<String, String>> login(@RequestBody LoginRequest request) {
        boolean valid = ("pierre".equals(request.getUsername()) && "secret123".equals(request.getPassword()))
                || ("admin".equals(request.getUsername()) && "admin123".equals(request.getPassword()));

        if (!valid) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));
        }

        String token = jwtService.generateToken(request.getUsername());
        return Mono.just(Map.of("token", token));
    }
}
