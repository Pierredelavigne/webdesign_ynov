package org.example.demo_secu_jwt_webflux;


import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class JwtAuthentificationFilter implements ServerAuthenticationConverter {

    private final JwtService jwtService;


    public JwtAuthentificationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims= jwtService.validateToken(token);
                String username = claims.getSubject();

                return Mono.just(new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));

            }catch (Exception e){
                return Mono.empty();
            }
        }
        return Mono.empty();
    }
}
