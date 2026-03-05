# exo9-reactor

## Lancer le projet
```bash
mvn spring-boot:run
```

## Endpoints
- GET `/api/tasks`
- GET `/api/tasks/{id}`
- POST `/api/tasks`
- PUT `/api/tasks/{id}`
- DELETE `/api/tasks/{id}`

## Exemple de body JSON
```json
{
  "description": "Finish TP WebFlux",
  "completed": false
}
```

## Notes
- API fonctionnelle avec `RouterFunction`
- stockage en memoire
