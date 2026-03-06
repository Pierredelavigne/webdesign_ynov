package com.example.exercice_5_a_10.exo9;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class TaskHandler {

    private final List<Task> tasks = new ArrayList<>();

    public Mono<ServerResponse> getAllTasks(ServerRequest request) {
        return ok().body(Flux.fromIterable(tasks), Task.class);
    }

    public Mono<ServerResponse> getTaskById(ServerRequest request) {
        String id = request.pathVariable("id");
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .map(task -> ok().bodyValue(task))
                .orElse(notFound().build());
    }

    public Mono<ServerResponse> addTask(ServerRequest request) {
        return  request.bodyToMono(Task.class)
                .doOnNext(task -> tasks.add(task))
                .flatMap(task -> ok().bodyValue(task));
    }

    public Mono<ServerResponse> deleteTask(ServerRequest request) {
        String id = request.pathVariable("id");
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        return removed ? noContent().build() : notFound().build();
    }

    public Mono<ServerResponse> updateTask(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(Task.class)
                .flatMap(updateTask -> {
                    Optional<Task> existTask = tasks.stream()
                            .filter(task -> task.getId().equals(id))
                            .findFirst();
                    if (existTask.isPresent()) {
                        existTask.get().setDescription(updateTask.getDescription());
                        existTask.get().setCompleted(updateTask.isCompleted());
                        return ok().bodyValue(existTask.get());
                    }else {
                        return notFound().build();
                    }
                });
    }
}
