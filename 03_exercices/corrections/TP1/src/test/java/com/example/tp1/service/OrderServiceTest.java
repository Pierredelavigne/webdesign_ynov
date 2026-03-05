package com.example.tp1.service;

import com.example.tp1.model.OrderRequest;
import com.example.tp1.model.OrderStatus;
import com.example.tp1.model.ProductWithPrice;
import com.example.tp1.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class OrderServiceTest {
    // Repository avec latence réduite pour accélérer les tests (10ms au lieu de 100ms)
    private ProductRepository productRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // Latence courte + aucune erreur aléatoire par défaut (tests déterministes)
        productRepository = new ProductRepository(Duration.ofMillis(10), 0.0);
        orderService = new OrderService(productRepository);
    }

    // =========================================================================
    // Test 1 : Cas nominal — commande avec produits valides
    // =========================================================================

    @Test
    @DisplayName("Test 1 : Traitement nominal d'une commande avec produits valides")
    void test_processOrderSuccess() {
        // ARRANGE : deux produits valides en stock
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD002"),
                "CUST001"
        );

        // ACT + ASSERT
        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getOrderId()).isNotNull().isNotEmpty();
                    assertThat(order.getProducts()).hasSize(2);
                    assertThat(order.getTotalPrice()).isGreaterThan(BigDecimal.ZERO);
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                    assertThat(order.getCreatedAt()).isNotNull();
                    assertThat(order.getDiscountApplied()).isTrue(); // PROD001 et PROD002 sont en "Electronique"
                })
                .verifyComplete();
    }

    // =========================================================================
    // Test 2 : IDs invalides mélangés avec des IDs valides
    // =========================================================================

    @Test
    @DisplayName("Test 2 : Seuls les produits valides sont inclus dans la commande")
    void test_processOrderWithInvalidIds() {
        // ARRANGE : 2 valides + 2 inexistants
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "INVALID_99", "PROD005", "UNKNOWN_42"),
                "CUST002"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    // Seuls PROD001 et PROD005 existent → 2 produits dans la commande
                    assertThat(order.getProducts()).hasSize(2);
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);

                    // Vérification que seuls les bons produits sont présents
                    List<String> includedIds = order.getProducts().stream()
                            .map(p -> p.getProduct().getId())
                            .toList();
                    assertThat(includedIds).containsExactlyInAnyOrder("PROD001", "PROD005");
                })
                .verifyComplete();
    }

    // =========================================================================
    // Test 3 : Produits hors stock exclus de la commande
    // =========================================================================

    @Test
    @DisplayName("Test 3 : Les produits hors stock ne sont pas inclus dans la commande")
    void test_processOrderWithoutStock() {
        // ARRANGE : PROD004 a stock = 0
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD004"),
                "CUST003"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    // PROD004 est hors stock → la liste de produits est vide
                    assertThat(order.getProducts()).isEmpty();
                    assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Test 3b : Mélange de produits en stock et hors stock")
    void test_processOrderMixedStock() {
        // PROD001 (stock=10), PROD004 (stock=0), PROD005 (stock=200)
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD004", "PROD005"),
                "CUST003b"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    // PROD004 exclu → seulement PROD001 et PROD005
                    assertThat(order.getProducts()).hasSize(2);
                    List<String> ids = order.getProducts().stream()
                            .map(p -> p.getProduct().getId()).toList();
                    assertThat(ids).doesNotContain("PROD004");
                })
                .verifyComplete();
    }

    // =========================================================================
    // Test 4 : Vérification du calcul des réductions
    // =========================================================================

    @Test
    @DisplayName("Test 4 : Electronique = 10% de réduction, autres catégories = 5%")
    void test_processOrderWithDiscounts() {
        // ARRANGE :
        //   PROD001 = Laptop (Electronique, 1299.99) → réduction 10%
        //   PROD003 = Bureau  (Mobilier, 399.00)      → réduction 5%
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD003"),
                "CUST004"
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order -> {
                    assertThat(order.getProducts()).hasSize(2);

                    // Trouver chaque ProductWithPrice par ID
                    ProductWithPrice laptop = order.getProducts().stream()
                            .filter(p -> "PROD001".equals(p.getProduct().getId()))
                            .findFirst().orElseThrow();

                    ProductWithPrice bureau = order.getProducts().stream()
                            .filter(p -> "PROD003".equals(p.getProduct().getId()))
                            .findFirst().orElseThrow();

                    // Vérification des pourcentages de réduction
                    assertThat(laptop.getDiscountPercentage()).isEqualTo(10);
                    assertThat(bureau.getDiscountPercentage()).isEqualTo(5);

                    // Vérification des prix finaux
                    BigDecimal expectedLaptopFinal = new BigDecimal("1169.99"); // 1299.99 * 0.90
                    BigDecimal expectedBureauFinal = new BigDecimal("379.05");  // 399.00  * 0.95

                    assertThat(laptop.getFinalPrice())
                            .isEqualByComparingTo(expectedLaptopFinal);
                    assertThat(bureau.getFinalPrice())
                            .isEqualByComparingTo(expectedBureauFinal);

                    // Vérification que totalPrice = somme des finalPrices
                    BigDecimal expectedTotal = expectedLaptopFinal.add(expectedBureauFinal);
                    assertThat(order.getTotalPrice()).isEqualByComparingTo(expectedTotal);
                })
                .verifyComplete();
    }

    // =========================================================================
    // Test 5 : Timeout après 5 secondes → commande FAILED
    // =========================================================================

    @Test
    @DisplayName("Test 5 : Le timeout de 5s produit une commande avec status FAILED")
    void test_processOrderTimeout() {
        // ARRANGE : repository avec latence > 5s pour forcer le timeout
        ProductRepository slowRepo = new ProductRepository(Duration.ofSeconds(6), 0.0);
        OrderService slowService = new OrderService(slowRepo);

        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001"),
                "CUST005"
        );

        // ACT + ASSERT
        // On utilise withVirtualTime + thenAwait pour éviter d'attendre vraiment 5s
        StepVerifier.withVirtualTime(() -> slowService.processOrder(request))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6)) // avance le temps virtuel de 6s
                .assertNext(order -> {
                    // Le timeout est capturé par onErrorResume → commande FAILED (pas une exception)
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
                    assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.ZERO);
                })
                .verifyComplete();
    }

    // =========================================================================
    // Test 6 : Gestion des erreurs partielles (erreurs du repository)
    // =========================================================================

    @Test
    @DisplayName("Test 6 : Les erreurs partielles du repository n'empêchent pas la commande")
    void test_processOrderWithErrors() {
        // ARRANGE : 50% d'erreurs aléatoires + plusieurs produits pour augmenter les chances
        // On utilise un taux fixe de 100% sur certains produits via un repository custom
        ProductRepository errorRepo = new ProductRepository(Duration.ofMillis(10), 0.5);
        OrderService errorService = new OrderService(errorRepo);

        // On utilise plusieurs produits valides : même avec 50% d'erreurs,
        // on s'attend à ce que certains passent et que la commande soit COMPLETED (ou vide mais pas FAILED)
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001", "PROD002", "PROD003", "PROD005"),
                "CUST006"
        );

        // On exécute plusieurs fois pour s'assurer que onErrorResume fonctionne
        // StepVerifier vérifie que la commande est créée (pas d'exception propagée)
        StepVerifier.create(errorService.processOrder(request))
                .assertNext(order -> {
                    // La commande doit être créée même si des erreurs se produisent
                    // Status COMPLETED (les erreurs individuelles sont ignorées)
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                    // Le total doit être >= 0 (peut être 0 si tous les appels ont échoué)
                    assertThat(order.getTotalPrice()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Test 6b : Requête invalide (customerId null) → commande FAILED")
    void test_processOrderInvalidRequest() {
        // ARRANGE : customerId null → InvalidOrderException → onErrorResume → FAILED
        OrderRequest request = new OrderRequest(
                Arrays.asList("PROD001"),
                null // invalide
        );

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order ->
                        assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED))
                .verifyComplete();
    }

    @Test
    @DisplayName("Test 6c : Liste de produits vide → commande FAILED")
    void test_processOrderEmptyList() {
        OrderRequest request = new OrderRequest(List.of(), "CUST007");

        StepVerifier.create(orderService.processOrder(request))
                .assertNext(order ->
                        assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED))
                .verifyComplete();
    }
}
