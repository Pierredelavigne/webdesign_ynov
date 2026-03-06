package com.example.demo_bdd_webflux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TaskRepository extends ReactiveCrudRepository<Task,Long> {
}
