package com.example.exercice_5_a_10.exo10;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BookRouter {

    @Bean
    public RouterFunction<ServerResponse> bookRoutes(BookHandler bookHandler) {
        return route(GET("/api/books"),bookHandler::getAllBooks)
                .andRoute(GET("/api/books/search"),bookHandler::searchBooks)
                .andRoute(POST("/api/books"),bookHandler::addBook)
                .andRoute(DELETE("/api/books/{id}"),bookHandler::deleteBook);
    }
}
