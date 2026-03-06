# Exercice 13 - API produits avec stock

## Lancer

```bash
mvn spring-boot:run
```

## Endpoints

- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`
- `GET /api/products/search?name=phone`
- `PUT /api/products/{id}/buy?quantity=5`

## Exemple JSON

```json
{
  "name": "Phone",
  "price": 799.99,
  "stock": 10
}
```

## Test achat

```bash
curl -X PUT "http://localhost:8080/api/products/1/buy?quantity=5"
```
