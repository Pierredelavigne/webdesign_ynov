package com.example.rooms.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/rooms").hasRole("ADMIN")
                        .pathMatchers(org.springframework.http.HttpMethod.DELETE, "/api/rooms/**").hasRole("ADMIN")
                        .pathMatchers(org.springframework.http.HttpMethod.GET, "/api/rooms").authenticated()
                        .anyExchange().authenticated()
                )
                .httpBasic(httpBasic -> {})
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> writeJson(exchange.getResponse(),
                                HttpStatus.UNAUTHORIZED,
                                Map.of("error", "UNAUTHORIZED",
                                       "message", "Vous devez vous connecter pour accéder à cette ressource")))
                        .accessDeniedHandler((exchange, ex) -> writeJson(exchange.getResponse(),
                                HttpStatus.FORBIDDEN,
                                Map.of("error", "FORBIDDEN",
                                       "message", "Vous n'avez pas les droits nécessaires pour accéder à cette ressource")))
                )
                .build();
    }

    private Mono<Void> writeJson(org.springframework.http.server.reactive.ServerHttpResponse response,
                                 HttpStatus status,
                                 Map<String, String> body) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    @Bean
    public MapReactiveUserDetailsService users() {
        return new MapReactiveUserDetailsService(
                User.withUsername("user").password("{noop}user123").roles("USER").build(),
                User.withUsername("admin").password("{noop}admin123").roles("ADMIN").build()
        );
    }
}
