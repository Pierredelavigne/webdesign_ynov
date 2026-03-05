package com.example.tp1.service;

import com.example.tp1.exception.InvalidOrderException;
import com.example.tp1.model.*;
import com.example.tp1.repository.ProductRepository;
import java.util.logging.Logger;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;


public class OrderService {
    private static final Logger log = Logger.getLogger(OrderService.class.getName());

    // Timeout global du pipeline (5 secondes)
    private static final Duration PIPELINE_TIMEOUT = Duration.ofSeconds(5);

    // Catégorie bénéficiant du meilleur taux de réduction
    private static final String CATEGORY_ELECTRONIQUE = "Electronique";
    private static final int DISCOUNT_ELECTRONIQUE = 10;
    private static final int DISCOUNT_STANDARD = 5;

    // Nombre maximum de produits par commande
    private static final int MAX_PRODUCTS_PER_ORDER = 100;

    private final ProductRepository productRepository;

    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // =========================================================================
    // Point d'entrée principal
    // =========================================================================

    /**
     * Traite une commande de manière entièrement réactive.
     *
     * @param request la requête de commande (IDs produits + ID client)
     * @return un Mono<Order> représentant la commande créée (ou FAILED si erreur)
     */
    public Mono<Order> processOrder(OrderRequest request) {

        // ---------- Étape 1 : Validation de la requête ----------
        return validateRequest(request)

                // ---------- Étape 2 : Récupération + filtrage des produits ----------
                .flatMap(validRequest ->
                        fetchProducts(validRequest.getProductIds())

                                // ---------- Étape 3 : Vérification du stock ----------
                                .filter(product -> {
                                    boolean inStock = product.getStock() > 0;
                                    if (!inStock) {
                                        log.info("Produit hors stock ignoré : " + product.getId());
                                    }
                                    return inStock;
                                })

                                // ---------- Étape 4 : Application des réductions ----------
                                .map(product -> {
                                    int discount = applyDiscount(product);
                                    ProductWithPrice pwp = new ProductWithPrice(product, discount);
                                    log.info("Réduction appliquée : " + product.getName()
                                            + " => " + discount + "% => " + pwp.getFinalPrice() + "€");
                                    return pwp;
                                })

                                // ---------- Étape 5 : Agrégation et création de la commande ----------
                                .collectList()
                                .map(productList -> buildOrder(validRequest, productList))
                )

                // ---------- Étape 6 : Timeout global ----------
                .timeout(PIPELINE_TIMEOUT)

                // ---------- Gestion d'erreurs : timeout ou exception ----------
                .onErrorResume(Exception.class, ex -> {
                    log.warning("Erreur lors du traitement de la commande : " + ex.getMessage());
                    return Mono.just(buildFailedOrder(request, ex));
                })

                // ---------- Étape 7 : Logging détaillé ----------
                .doOnNext(order ->
                        log.info("Commande créée : " + order))

                .doOnError(ex ->
                        log.severe("Erreur fatale dans le pipeline : " + ex.getMessage()))

                .doFinally(signalType ->
                        log.info("Pipeline terminé avec signal : " + signalType));
    }

    // =========================================================================
    // Méthodes privées du pipeline
    // =========================================================================

    /**
     * Étape 1 — Valide la requête.
     * Lance une InvalidOrderException si les données sont invalides.
     */
    private Mono<OrderRequest> validateRequest(OrderRequest request) {
        if (request == null) {
            return Mono.error(new InvalidOrderException("La requête de commande est null"));
        }
        if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
            return Mono.error(new InvalidOrderException("L'ID client est obligatoire"));
        }
        if (request.getProductIds() == null || request.getProductIds().isEmpty()) {
            return Mono.error(new InvalidOrderException("La liste de produits est vide"));
        }

        log.info("Requête valide pour le client : " + request.getCustomerId()
                + " avec " + request.getProductIds().size() + " produit(s)");

        return Mono.just(request);
    }

    /**
     * Étape 2 — Récupère les produits depuis le repository de manière réactive.
     * Filtre les IDs null/vides, limite à MAX_PRODUCTS_PER_ORDER.
     * Ignore silencieusement les produits inexistants ou les erreurs individuelles.
     */
    private reactor.core.publisher.Flux<Product> fetchProducts(List<String> productIds) {
        return reactor.core.publisher.Flux.fromIterable(productIds)

                // Filtrer les IDs null ou vides
                .filter(id -> id != null && !id.isBlank())

                // Limiter à 100 produits max
                .take(MAX_PRODUCTS_PER_ORDER)

                // Récupérer chaque produit (flatMap = réactif, pas bloquant)
                .flatMap(id -> productRepository.findById(id)

                        // Si produit inexistant (Mono.empty()), on l'ignore
                        .doOnSuccess(p -> {
                            if (p == null) {
                                log.info("Produit non trouvé (ignoré) : " + id);
                            } else {
                                log.info("Produit récupéré : " + p);
                            }
                        })

                        // En cas d'erreur individuelle, on logue et on ignore (on ne fait pas échouer toute la commande)
                        .onErrorResume(ex -> {
                            log.warning("Erreur lors de la récupération du produit "
                                    + id + " : " + ex.getMessage() + " (ignoré)");
                            return Mono.empty();
                        })
                );
    }

    /**
     * Étape 4 — Calcule le taux de réduction selon la catégorie.
     */
    private int applyDiscount(Product product) {
        if (CATEGORY_ELECTRONIQUE.equalsIgnoreCase(product.getCategory())) {
            return DISCOUNT_ELECTRONIQUE;
        }
        return DISCOUNT_STANDARD;
    }

    /**
     * Étape 5 — Construit l'objet Order à partir de la liste de produits traités.
     */
    private Order buildOrder(OrderRequest request, List<ProductWithPrice> productList) {
        BigDecimal totalPrice = productList.stream()
                .map(ProductWithPrice::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean discountApplied = productList.stream()
                .anyMatch(p -> p.getDiscountPercentage() != null && p.getDiscountPercentage() > 0);

        return new Order(
                request.getProductIds(),
                productList,
                totalPrice,
                discountApplied,
                OrderStatus.COMPLETED
        );
    }

    /**
     * Construit une commande FAILED en cas d'erreur non récupérable (timeout, etc.).
     */
    private Order buildFailedOrder(OrderRequest request, Exception ex) {
        List<String> ids = request != null ? request.getProductIds() : List.of();
        log.warning("Création d'une commande FAILED : " + ex.getMessage());
        return Order.failed(ids, ex.getMessage());
    }
}
