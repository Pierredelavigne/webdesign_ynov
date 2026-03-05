# exo10-reactor

## Lancer le projet
```bash
mvn spring-boot:run
```

## Endpoints
- GET `/api/books`
- GET `/api/books/search?title=XYZ`
- POST `/api/books`
- DELETE `/api/books/{id}`

## Exemple de body JSON
```json
{
  "title": "Spring WebFlux Essentials",
  "author": "Pierre Delavigne"
}
```

## Notes
- API fonctionnelle avec `RouterFunction`
- stockage en memoire
- recherche par titre avec query param
