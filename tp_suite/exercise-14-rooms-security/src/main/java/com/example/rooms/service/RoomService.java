package com.example.rooms.service;

import com.example.rooms.model.Room;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RoomService {
    private final List<Room> rooms = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @PostConstruct
    public void init() {
        rooms.add(new Room(idGenerator.incrementAndGet(), "Salle Alpha", 10));
        rooms.add(new Room(idGenerator.incrementAndGet(), "Salle Beta", 20));
    }

    public Flux<Room> findAll() {
        return Flux.fromIterable(rooms);
    }

    public Mono<Room> add(Room room) {
        room.setId(idGenerator.incrementAndGet());
        rooms.add(room);
        return Mono.just(room);
    }

    public Mono<Void> delete(Long id) {
        rooms.removeIf(room -> room.getId().equals(id));
        return Mono.empty();
    }
}
