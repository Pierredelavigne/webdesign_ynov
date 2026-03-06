# Exercice 12 - API de gestion des commandes

## Lancer

```bash
mvn spring-boot:run
```

## Endpoints

- `GET /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders`
- `PUT /api/orders/{id}`
- `DELETE /api/orders/{id}`
- `GET /api/orders/search?status=SHIPPED`
- `GET /api/orders/paged?page=0&size=5`
- `GET /api/orders/customer/{customerName}`

## Exemple JSON création

```json
{
  "customerName": "Pierre",
  "totalAmount": 149.99
}
```

## Exemple JSON mise à jour

```json
{
  "status": "SHIPPED"
}
```

## Tests curl

```bash
curl -X POST http://localhost:8080/api/orders   -H "Content-Type: application/json"   -d '{"customerName":"Pierre","totalAmount":149.99}'
```

```bash
curl "http://localhost:8080/api/orders/search?status=PENDING"
```

```bash
curl "http://localhost:8080/api/orders/paged?page=0&size=5"
```
