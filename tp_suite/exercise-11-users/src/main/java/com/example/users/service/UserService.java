package com.example.users.service;

import com.example.users.model.User;
import com.example.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> create(User user) {
        user.setId(null);
        if (user.getActive() == null) {
            user.setActive(true);
        }
        return userRepository.save(user);
    }

    public Mono<User> update(Long id, User user) {
        return userRepository.findById(id)
                .flatMap(existing -> {
                    existing.setName(user.getName());
                    existing.setEmail(user.getEmail());
                    existing.setActive(user.getActive());
                    return userRepository.save(existing);
                });
    }

    public Mono<Void> delete(Long id) {
        return userRepository.deleteById(id);
    }
}
