# exo7-reactor

## Lancer le projet
```bash
mvn spring-boot:run
```

## Endpoints
- GET `/api/error-resume`
- GET `/api/error-continue`

## Comportement
### `/api/error-resume`
- emet `A`, `B`, `C`
- simule une erreur
- remplace par `Default1`, `Default2`

### `/api/error-continue`
- genere les nombres de 1 a 5
- simule une erreur sur la valeur 2
- ignore l'erreur et continue avec les autres valeurs
