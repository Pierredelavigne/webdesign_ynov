package org.example.demo_test_opti_webflux;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        ConnectionProvider provider = ConnectionProvider.builder("custom")
                .maxConnections(50) // nbr max de connexion dans le pool
                .pendingAcquireMaxCount(10) // nbr max de requettes en attente de connexion
                .build();

        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider)))
                .build();


    }
}
