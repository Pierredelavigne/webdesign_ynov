package com.example.tp1webflux.service;

import com.example.tp1webflux.model.OrderRequest;
import com.example.tp1webflux.model.OrderStatus;
import com.example.tp1webflux.model.ProductWithPrice;
import com.example.tp1webflux.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceTest {

    private ProductRepository productRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository(Duration.ofMillis(100));
        orderService = new OrderService(productRepository);
    }

    @Test
    void test_processOrderSuccess() {
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD002"),
                "CUST001"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getOrderId()).isNotNull();
                    assertThat(order.getProducts()).hasSize(2);
                    assertThat(order.getTotalPrice()).isEqualByComparingTo("945.00");
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                })
                .verifyComplete();
    }

    @Test
    void test_processOrderWithInvalidIds() {
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "", null, "INVALID", "PROD003"),
                "CUST002"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getProducts()).hasSize(2);
                    assertThat(order.getProducts())
                            .extracting(p -> p.getProduct().getId())
                            .containsExactlyInAnyOrder("PROD001", "PROD003");
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                })
                .verifyComplete();
    }

    @Test
    void test_processOrderWithoutStock() {
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD004", "PROD005"),
                "CUST003"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getProducts()).hasSize(1);
                    assertThat(order.getProducts().get(0).getProduct().getId()).isEqualTo("PROD005");
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                })
                .verifyComplete();
    }

    @Test
    void test_processOrderWithDiscounts() {
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD003", "PROD005"),
                "CUST004"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getProducts()).hasSize(3);

                    ProductWithPrice p1 = order.getProducts().stream()
                            .filter(p -> p.getProduct().getId().equals("PROD001"))
                            .findFirst()
                            .orElseThrow();

                    ProductWithPrice p3 = order.getProducts().stream()
                            .filter(p -> p.getProduct().getId().equals("PROD003"))
                            .findFirst()
                            .orElseThrow();

                    ProductWithPrice p5 = order.getProducts().stream()
                            .filter(p -> p.getProduct().getId().equals("PROD005"))
                            .findFirst()
                            .orElseThrow();

                    assertThat(p1.getDiscountPercentage()).isEqualTo(10);
                    assertThat(p3.getDiscountPercentage()).isEqualTo(5);
                    assertThat(p5.getDiscountPercentage()).isEqualTo(5);

                    assertThat(p1.getFinalPrice()).isEqualByComparingTo("900.00");
                    assertThat(p3.getFinalPrice()).isEqualByComparingTo("19.00");
                    assertThat(p5.getFinalPrice()).isEqualByComparingTo("9.50");

                    assertThat(order.getTotalPrice()).isEqualByComparingTo("928.50");
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                })
                .verifyComplete();
    }

    @Test
    void test_processOrderTimeout() {
        ProductRepository slowRepository = new ProductRepository(Duration.ofSeconds(6));
        OrderService slowOrderService = new OrderService(slowRepository);

        OrderRequest request = new OrderRequest(
                List.of("PROD001", "PROD002"),
                "CUST005"
        );

        StepVerifier.withVirtualTime(() -> slowOrderService.processOrder(request))
                .thenAwait(Duration.ofSeconds(6))
                .assertNext(order -> {
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
                    assertThat(order.getProducts()).isEmpty();
                    assertThat(order.getTotalPrice()).isEqualByComparingTo("0");
                })
                .verifyComplete();
    }

    @Test
    void test_processOrderWithErrors() {
        ProductRepository flakyRepository = new ProductRepository(
                Duration.ofMillis(100),
                0.0,
                Set.of("PROD002", "PROD005")
        );
        OrderService flakyOrderService = new OrderService(flakyRepository);

        OrderRequest request = new OrderRequest(
                List.of("PROD001", "PROD002", "PROD003", "PROD005"),
                "CUST006"
        );

        StepVerifier.create(flakyOrderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                    assertThat(order.getProducts()).hasSize(2);
                    assertThat(order.getProducts())
                            .extracting(p -> p.getProduct().getId())
                            .containsExactlyInAnyOrder("PROD001", "PROD003");
                })
                .verifyComplete();
    }
}
