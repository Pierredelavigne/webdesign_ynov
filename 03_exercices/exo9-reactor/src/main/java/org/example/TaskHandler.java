package org.example;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

public class TaskHandler {

    private final TaskRepository repo;

    public TaskHandler(TaskRepository repo) {
        this.repo = repo;
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(repo.findAll(), Task.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String id = request.pathVariable("id");
        return repo.findById(id)
                .flatMap(task -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(task))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(Task.class)
                .flatMap(repo::save)
                .flatMap(saved -> created(request.uriBuilder().path("/{id}").build(saved.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(saved));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(Task.class)
                .flatMap(body -> repo.update(id, body))
                .flatMap(updated -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(updated))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return repo.delete(id).then(noContent().build());
    }
}
