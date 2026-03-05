# TP1 Spring WebFlux - Pipeline reactif complet

## Objectif

Ce projet implemente un pipeline reactif complet avec Spring WebFlux et Project Reactor pour simuler le traitement d'une commande dans une boutique en ligne.

Le traitement couvre les etapes suivantes :

1. reception d'une commande contenant une liste d'identifiants produits,
2. validation de la requete,
3. recuperation des produits depuis un repository simule,
4. filtrage des produits invalides ou hors stock,
5. application des reductions selon la categorie,
6. calcul du prix total,
7. creation de la commande finale,
8. gestion des erreurs et des timeouts.

---

## Technologies utilisees

- Java 17
- Spring Boot 3
- Spring WebFlux
- Project Reactor
- JUnit 5
- Reactor Test
- AssertJ

---

## Lancer les tests

Depuis la racine du projet :

```bash
mvn test
```

Pour lancer uniquement la classe de test principale :

```bash
mvn -Dtest=OrderServiceTest test
```

---

## Structure du projet

```text
src/
├─ main/
│  └─ java/com/example/tp1webflux/
│     ├─ exception/
│     │  ├─ InvalidOrderException.java
│     │  └─ ProductNotFoundException.java
│     ├─ model/
│     │  ├─ Product.java
│     │  ├─ ProductWithPrice.java
│     │  ├─ Order.java
│     │  ├─ OrderRequest.java
│     │  └─ OrderStatus.java
│     ├─ repository/
│     │  └─ ProductRepository.java
│     ├─ service/
│     │  └─ OrderService.java
│     └─ Tp1WebfluxApplication.java
└─ test/
   └─ java/com/example/tp1webflux/service/
      └─ OrderServiceTest.java
```

---

## Modeles metier

### Product
Represente un produit disponible dans la boutique.

Champs :
- `id`
- `name`
- `price`
- `stock`
- `category`

### ProductWithPrice
Represente un produit apres application de la reduction.

Champs :
- `product`
- `originalPrice`
- `discountPercentage`
- `finalPrice`

### OrderRequest
Represente la requete d'entree.

Champs :
- `productIds`
- `customerId`

### Order
Represente la commande finale.

Champs :
- `orderId`
- `productIds`
- `products`
- `totalPrice`
- `discountApplied`
- `createdAt`
- `status`

### OrderStatus
Statuts possibles :
- `CREATED`
- `VALIDATED`
- `PROCESSING`
- `COMPLETED`
- `FAILED`

---

## Fonctionnement du pipeline reactif

Le traitement principal est implemente dans `OrderService#processOrder(OrderRequest request)`.

### Etapes du pipeline

1. **Validation de la requete**
   - verifie que la requete n'est pas nulle,
   - verifie que `customerId` est renseigne,
   - verifie que la liste des `productIds` n'est pas vide.

2. **Transformation en flux**
   - conversion de `List<String>` en `Flux<String>` via `Flux.fromIterable(...)`.

3. **Nettoyage des IDs**
   - suppression des IDs nuls ou vides avec `filter(...)`,
   - limitation a 100 produits maximum avec `take(100)`.

4. **Batching**
   - regroupement des IDs par paquets de 10 avec `buffer(10)`.

5. **Recuperation des produits**
   - chaque batch est traite via `productRepository.findByIds(batchIds)`.

6. **Filtrage metier**
   - seuls les produits en stock sont conserves.

7. **Application des reductions**
   - `10%` pour la categorie `ELECTRONICS`,
   - `5%` pour les autres categories.

8. **Creation de la commande**
   - aggregation des produits avec `collectList()`,
   - calcul du prix total,
   - construction de l'objet `Order`.

9. **Gestion des erreurs**
   - timeout global avec `timeout(Duration.ofSeconds(5))`,
   - fallback en commande `FAILED` avec `onErrorResume(...)`.

10. **Logs**
   - journalisation detaillee des etapes avec `doOnNext`, `doOnError` et `doFinally`.

---

## Operateurs Reactor utilises

Le projet met en oeuvre les operateurs reactifs suivants :

- `Flux.fromIterable(...)`
- `filter(...)`
- `take(...)`
- `buffer(...)`
- `flatMap(...)`
- `map(...)`
- `collectList()`
- `timeout(...)`
- `onErrorResume(...)`
- `doOnNext(...)`
- `doOnError(...)`
- `doFinally(...)`

---

## Repository simule

`ProductRepository` simule une base de donnees en memoire avec 5 produits predefinis.

### Comportements simules

- latence artificielle de 100 ms par acces,
- possibilite d'erreurs forcees via `forcedErrorIds`,
- possibilite d'erreurs aleatoires via `randomErrorRate`.

### Produits disponibles

- `PROD001` - Laptop - 1000.00 - stock 5 - `ELECTRONICS`
- `PROD002` - Mouse - 50.00 - stock 10 - `ELECTRONICS`
- `PROD003` - Book - 20.00 - stock 15 - `BOOKS`
- `PROD004` - Chair - 150.00 - stock 0 - `FURNITURE`
- `PROD005` - Bottle - 10.00 - stock 2 - `HOME`

---

## Optimisations bonus implementees

### Bonus B - Cache

Un cache simple base sur `ConcurrentHashMap` est implemente dans le `ProductRepository`.

Objectif :
- eviter les acces redondants a la base simulee,
- reutiliser un produit deja recupere lorsqu'un meme ID apparait plusieurs fois.

Benefices :
- reduction du nombre d'acces simules,
- amelioration des performances,
- logique simple et adaptee au TP.

### Bonus D - Batching

Le traitement des IDs produits est regroupe par lots de 10 avec `buffer(10)`.

Objectif :
- eviter un acces unitaire par produit,
- traiter plusieurs IDs ensemble via `findByIds(...)`.

Benefices :
- reduction du nombre d'acces simules,
- meilleure efficacite sur les grosses commandes,
- code plus proche d'une logique de requetage groupe.

---

## Reductions appliquees

Les remises sont calculees dans `OrderService#applyDiscount(Product product)` :

- categorie `ELECTRONICS` -> **10%**
- toutes les autres categories -> **5%**

### Exemples

- `PROD001` (1000.00) -> 10% -> **900.00**
- `PROD002` (50.00) -> 10% -> **45.00**
- `PROD003` (20.00) -> 5% -> **19.00**
- `PROD005` (10.00) -> 5% -> **9.50**

---

## Strategie de test

Les tests sont regroupes dans un seul fichier : `OrderServiceTest`.

Ce choix est pertinent ici car tous les scenarios testent une seule classe metier principale : `OrderService`.

Les tests utilisent `StepVerifier` pour valider le comportement du `Mono<Order>` retourne par `processOrder(...)`.

### Cas testes

1. **Cas nominal**
   - verifie qu'une commande valide est bien creee,
   - verifie le total,
   - verifie le statut `COMPLETED`.

2. **IDs invalides**
   - melange IDs valides / invalides / vides,
   - verifie que seuls les produits valides sont conserves.

3. **Produits sans stock**
   - verifie que les produits avec stock a `0` sont exclus.

4. **Reductions**
   - verifie les pourcentages appliques,
   - verifie le calcul exact du prix total.

5. **Timeout**
   - simule un repository trop lent,
   - verifie qu'une commande `FAILED` est creee.

6. **Erreurs partielles**
   - simule des erreurs sur certains produits,
   - verifie que le traitement continue pour les autres.

7. **Batching (optionnel)**
   - peut etre ajoute pour demontrer explicitement le traitement de plus de 10 IDs.

---

## Choix de conception

### Pourquoi un repository simule ?
Le but du TP est de se concentrer sur la logique reactive sans dependre d'une base reelle.

### Pourquoi un seul fichier de test ?
Le projet reste de petite taille et toutes les verifications concernent `OrderService`.
Cela rend la lecture plus simple et plus adaptee a un rendu pedagogique.

### Pourquoi deplacer le cache dans le repository ?
Le cache concerne l'acces aux donnees.
Le placer dans le repository rend le service plus lisible et respecte mieux la separation des responsabilites.

---

## Resultat attendu

Le projet fournit :

- un pipeline reactif complet,
- une gestion des erreurs et du timeout,
- des tests unitaires avec `StepVerifier`,
- deux optimisations bonus :
  - **B : cache**
  - **D : batching**

L'ensemble constitue une implementation simple, lisible et coherente d'un traitement reactif de commande avec Spring WebFlux.

---

## Auteur

Projet realise dans le cadre du **TP1 Spring WebFlux - Pipeline reactif complet**.