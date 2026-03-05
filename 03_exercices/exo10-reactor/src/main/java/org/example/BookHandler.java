package org.example;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

public class BookHandler {

    private final BookRepository repo;

    public BookHandler(BookRepository repo) {
        this.repo = repo;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(repo.findAll(), Book.class);
    }

    public Mono<ServerResponse> search(ServerRequest request) {
        String title = request.queryParam("title").orElse("");
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(repo.searchByTitle(title), Book.class);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Book.class)
                .flatMap(repo::save)
                .flatMap(saved -> created(request.uriBuilder().path("/{id}").build(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return repo.delete(id).then(noContent().build());
    }
}
