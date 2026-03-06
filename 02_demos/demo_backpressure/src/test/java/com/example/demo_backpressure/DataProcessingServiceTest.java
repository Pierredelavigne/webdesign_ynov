package com.example.demo_backpressure;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class DataProcessingServiceTest {
    private final DataProcessingService service = new DataProcessingService();

    @Test
    void testControlledStream() {
        StepVerifier.create(service.getControlledStream().take(10))
                .expectSubscription()
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void testPublishOnStream() {
        StepVerifier.create(service.getPublishOnStream())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void testSubscribeOnStream() {
        StepVerifier.create(service.getSubscribeOnStream())
                .expectSubscription()
                .expectNext("Item-1", "Item-2", "Item-3", "Item-4", "Item-5")
                .verifyComplete();
    }

    @Test
    void testParallelProcessingStream() {
        StepVerifier.create(service.getParallelProcessingStream())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }
}
