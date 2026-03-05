package com.example.demo_api_rest_webflux;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public Flux<Notification> getNotifications() {
        return notificationService.getNotifications();
    }

    @GetMapping("/urgence")
    public Flux<Notification> getUrgences() {
        return notificationService.getFilteredNotifications();
    }

    @GetMapping(value = "/notiftexte",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getNotiftexte() {
        return notificationService.getFilteredNotifications()
                .map(notification -> "Notification : "+notification.toString());
    }




}
