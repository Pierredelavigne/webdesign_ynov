package com.example.demo_api_rest_webflux;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

@Service
public class NotificationService {

    public Flux<Notification> getNotifications() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(i -> new Notification(
                        UUID.randomUUID().toString(),
                        "Message : "+ i,
                        i % 2 == 0
                ))
                .log(); // observation
    }

    public Flux<Notification> getFilteredNotifications() {
        return getNotifications()
                .filter(Notification::isUrgent)
                .map(notification -> {
                    notification.setMessage(notification.getMessage()+ " URGENT");
                    return notification;
                });
    }
}
