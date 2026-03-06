# Exercice 11 - Gestion des utilisateurs

API réactive Spring WebFlux + R2DBC + H2 pour gérer des utilisateurs.

## Lancer le projet

```bash
mvn spring-boot:run
```

## Endpoints

- `GET /api/users`
- `GET /api/users/{id}`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

## Exemple JSON

```json
{
  "name": "Pierre",
  "email": "pierre@example.com",
  "active": true
}
```

## Tests rapides avec curl

```bash
curl http://localhost:8080/api/users
```

```bash
curl -X POST http://localhost:8080/api/users   -H "Content-Type: application/json"   -d '{"name":"Pierre","email":"pierre@example.com","active":true}'
```

```bash
curl http://localhost:8080/api/users/1
```

```bash
curl -X PUT http://localhost:8080/api/users/1   -H "Content-Type: application/json"   -d '{"name":"Pierre MAJ","email":"pierre.maj@example.com","active":false}'
```

```bash
curl -X DELETE http://localhost:8080/api/users/1
```
