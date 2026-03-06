package com.example.products.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<Map<String, String>> handleInsufficientStock(InsufficientStockException ex) {
        return Mono.just(Map.of("error", "BAD_REQUEST", "message", ex.getMessage()));
    }
}
