# TP1 Spring WebFlux — Pipeline réactif complet

## Lancer les tests
```bash
mvn test
```

## Contenu
- Modèles : Product, ProductWithPrice, Order, OrderRequest, OrderStatus
- Repository simulé : latence (100ms par appel), erreurs forcées + option erreurs aléatoires
- Service : pipeline réactif complet (filter/take/flatMap/map/collectList/timeout/onErrorResume/doOn*)
- Tests : 6 tests StepVerifier
- Bonus : parallélisation + cache produits
