package com.example.demo_error_webflux;


import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GloablErrorHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();

        System.err.println("Erreur intercepté : "+ ex.getMessage() + "pour la requete "+ request.getPath());

        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

        String errorMessage = String.format("{\"error\": \"%s\", \"path\": \"%s\"}",
                ex.getMessage(),
                request.getPath());

        byte[] bytes = errorMessage.getBytes();

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
