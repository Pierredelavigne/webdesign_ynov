package com.example.demo_bdd_webflux;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Flux<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Mono<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Mono<Task> createTask(Task task) {
        return taskRepository.save(task);
    }

    public Mono<Task> updateTask(Long id, Task task) {
        return taskRepository.findById(id)
                .flatMap(existingTask -> {
                    existingTask.setDescription(task.getDescription());
                    existingTask.setCompleted(task.isCompleted());
                    return taskRepository.save(existingTask);
                });
    }

    public Mono<Void> deleteTask(Long id) {
        return taskRepository.deleteById(id);
    }
}
