package org.example.demo_test_opti_webflux;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(ReactiveController.class)
public class ReactiveControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void testGetSingleData(){
        webClient.get().uri("/api/test/single")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello World !");
    }

    @Test
    void testGetMultiData(){
        webClient.get().uri("/api/test/stream")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class).contains(1, 2, 3,4);

    }
}
