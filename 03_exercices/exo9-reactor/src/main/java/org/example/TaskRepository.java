package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRepository {

    private final Map<String, Task> store = new ConcurrentHashMap<>();

    public TaskRepository() {
        save(new Task(null, "Learn WebFlux", false)).block();
        save(new Task(null, "Write tests", true)).block();
    }

    public Flux<Task> findAll() {
        return Flux.fromIterable(store.values());
    }

    public Mono<Task> findById(String id) {
        return Mono.justOrEmpty(store.get(id));
    }

    public Mono<Task> save(Task task) {
        String id = task.getId() != null ? task.getId() : UUID.randomUUID().toString();
        Task saved = new Task(id, task.getDescription(), task.isCompleted());
        store.put(id, saved);
        return Mono.just(saved);
    }

    public Mono<Task> update(String id, Task update) {
        return findById(id)
                .flatMap(existing -> {
                    String desc = update.getDescription() != null ? update.getDescription() : existing.getDescription();
                    Task saved = new Task(id, desc, update.isCompleted());
                    store.put(id, saved);
                    return Mono.just(saved);
                });
    }

    public Mono<Void> delete(String id) {
        store.remove(id);
        return Mono.empty();
    }
}
