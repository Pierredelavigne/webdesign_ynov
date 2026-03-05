package com.example.tp1.repository;

import com.example.tp1.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProductRepository {
    // Base de données en mémoire (5 produits)
    private final Map<String, Product> database;

    // Délai simulant la latence réseau/BDD
    private final Duration latency;

    // Taux d'erreur aléatoire (0.0 = aucune erreur, 0.1 = 10%, 0.5 = 50%...)
    private final double errorRate;

    private final Random random = new Random();

    /**
     * Constructeur par défaut : 100ms de latence, 10% d'erreurs aléatoires.
     */
    public ProductRepository() {
        this(Duration.ofMillis(100), 0.10);
    }

    /**
     * Constructeur configurable (utile pour les tests).
     *
     * @param latency   délai simulé par appel
     * @param errorRate taux d'erreurs aléatoires (0.0 à 1.0)
     */
    public ProductRepository(Duration latency, double errorRate) {
        this.latency = latency;
        this.errorRate = errorRate;
        this.database = initDatabase();
    }

    // -------------------------------------------------------------------------
    // Initialisation de la base de données en mémoire
    // -------------------------------------------------------------------------

    private Map<String, Product> initDatabase() {
        Map<String, Product> db = new HashMap<>();

        db.put("PROD001", new Product("PROD001", "Laptop Pro 15",
                new BigDecimal("1299.99"), 10, "Electronique"));

        db.put("PROD002", new Product("PROD002", "Souris Ergonomique",
                new BigDecimal("49.99"), 50, "Electronique"));

        db.put("PROD003", new Product("PROD003", "Bureau en Chêne",
                new BigDecimal("399.00"), 5, "Mobilier"));

        db.put("PROD004", new Product("PROD004", "Lampe de Bureau LED",
                new BigDecimal("89.90"), 0, "Mobilier")); // Stock à 0 intentionnel

        db.put("PROD005", new Product("PROD005", "Carnet de Notes Premium",
                new BigDecimal("12.50"), 200, "Papeterie"));

        return db;
    }

    // -------------------------------------------------------------------------
    // Méthodes publiques du repository
    // -------------------------------------------------------------------------

    /**
     * Recherche un produit par son ID.
     * Retourne Mono.empty() si le produit n'existe pas.
     * Simule la latence et peut lever une erreur aléatoire.
     */
    public Mono<Product> findById(String id) {
        return Mono.defer(() -> {
                    // Simulation d'erreur aléatoire
                    if (shouldFail()) {
                        return Mono.<Product>error(
                                new RuntimeException("Erreur DB simulée pour le produit : " + id));
                    }

                    Product product = database.get(id);
                    if (product == null) {
                        return Mono.empty();
                    }
                    return Mono.just(product);
                })
                // Simule la latence réseau/BDD
                .delayElement(latency);
    }

    /**
     * Recherche plusieurs produits par une liste d'IDs.
     * Les produits inexistants sont simplement ignorés (pas dans le Flux résultant).
     */
    public Flux<Product> findByIds(List<String> ids) {
        return Flux.fromIterable(ids)
                .flatMap(this::findById);
    }

    /**
     * Retourne le stock disponible d'un produit.
     * Retourne 0 si le produit n'existe pas.
     */
    public Mono<Integer> getStock(String productId) {
        return findById(productId)
                .map(Product::getStock)
                .defaultIfEmpty(0);
    }

    /**
     * Retourne tous les produits disponibles.
     */
    public Flux<Product> findAll() {
        return Flux.fromIterable(database.values())
                .delayElements(latency);
    }

    // -------------------------------------------------------------------------
    // Méthodes utilitaires
    // -------------------------------------------------------------------------

    private boolean shouldFail() {
        return errorRate > 0 && random.nextDouble() < errorRate;
    }

    /**
     * Permet d'ajouter un produit pour les tests.
     */
    public void addProduct(Product product) {
        database.put(product.getId(), product);
    }
}
