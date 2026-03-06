package com.example.exercice_5_a_10.exo10;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class BookHandler {

    private final List<Book>  books = new ArrayList<>();

    public Mono<ServerResponse> getAllBooks(ServerRequest request) {
        return ok().body(Flux.fromIterable(books), Book.class);
    }

    public Mono<ServerResponse> searchBooks(ServerRequest request) {
        String title = request.queryParam("title").orElse(null);
        System.out.println(title);
        return ok().body(Flux.fromIterable(books)
                        .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase())).log()
                , Book.class);
    }

    public Mono<ServerResponse> addBook(ServerRequest request) {
        return request.bodyToMono(Book.class)
                .doOnNext(books::add)
                .flatMap(book -> ok().body(Mono.just(book), Book.class));
    }

    public Mono<ServerResponse> deleteBook(ServerRequest request) {
        String id = request.pathVariable("id");
        boolean removed = books.removeIf(book -> book.getId().equals(id));
        return removed ? noContent().build() : notFound().build();
    }


}
