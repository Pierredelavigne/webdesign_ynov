package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BookRepository {

    private final Map<String, Book> store = new ConcurrentHashMap<>();

    public BookRepository() {
        save(new Book(null, "Spring in Action", "Craig Walls")).block();
        save(new Book(null, "Reactive Spring", "Josh Long")).block();
    }

    public Flux<Book> findAll() {
        return Flux.fromIterable(store.values());
    }

    public Flux<Book> searchByTitle(String title) {
        String query = title == null ? "" : title.toLowerCase();
        return Flux.fromIterable(store.values())
                .filter(book -> book.getTitle() != null && book.getTitle().toLowerCase().contains(query));
    }

    public Mono<Book> save(Book book) {
        String id = book.getId() != null ? book.getId() : UUID.randomUUID().toString();
        Book saved = new Book(id, book.getTitle(), book.getAuthor());
        store.put(id, saved);
        return Mono.just(saved);
    }

    public Mono<Void> delete(String id) {
        store.remove(id);
        return Mono.empty();
    }
}
