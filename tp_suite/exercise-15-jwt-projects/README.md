# Exercice 15 - Authentification JWT

## Comptes de test

- `pierre / secret123`
- `admin / admin123`

## Endpoints

- `POST /api/auth/login`
- `GET /api/projects`

## Login

```bash
curl -X POST http://localhost:8080/api/auth/login   -H "Content-Type: application/json"   -d '{"username":"pierre","password":"secret123"}'
```

## Appel protégé

Remplace `TOKEN` par la valeur retournée.

```bash
curl http://localhost:8080/api/projects   -H "Authorization: Bearer TOKEN"
```
