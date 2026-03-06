# Exercice 14 - Sécurisation API de réservation de salles

## Comptes de test

- USER : `user / user123`
- ADMIN : `admin / admin123`

## Endpoints

- `GET /api/rooms` : authentifié
- `POST /api/rooms` : ADMIN
- `DELETE /api/rooms/{id}` : ADMIN

## Lancer

```bash
mvn spring-boot:run
```

## Tests curl

```bash
curl -u user:user123 http://localhost:8080/api/rooms
```

```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/rooms   -H "Content-Type: application/json"   -d '{"name":"Salle Gamma","capacity":12}'
```
