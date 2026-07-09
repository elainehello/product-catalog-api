# Product Catalog API

A simple REST API for managing a product catalog, built with Spring Boot 3, Spring Data JPA, and MySQL. Database schema is managed with Flyway migrations.

## Stack

- Java 17 + Spring Boot 3.5
- Maven (with `mvnw` wrapper)
- MySQL 8
- Flyway
- JUnit 5 + Mockito
- Lombok
- springdoc-openapi (Swagger UI)

## Running the database

The project uses Spring Boot's Docker Compose integration: `compose.yaml` at the project root defines a MySQL 8 container, and Spring Boot will start it automatically (and configure the datasource) when you run the app — no manual `docker compose up` needed.

If you'd rather start it yourself:

```bash
docker compose up -d
```

This starts MySQL on `localhost:3306` with database `product_catalog`, user `catalog_user` / `secret`.

## Running the application

```bash
./mvnw spring-boot:run
```

On startup, Flyway runs the migrations in `src/main/resources/db/migration` against the database, creating the `product` table from scratch.

The API is available at `http://localhost:8080`.

### API documentation (Swagger)

Interactive API docs are available once the app is running:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Running the tests

```bash
./mvnw test
```

Unit tests cover the service layer (`ProductServiceTest`), mocking the repository with Mockito.

## Static analysis (SonarQube)

SonarQube runs locally via a separate Docker Compose file (kept out of `compose.yaml` so it doesn't start alongside the app or tests):

```bash
docker compose -f docker-compose.sonar.yml up -d
```

Wait for it to come up at `http://localhost:9000` (default login `admin` / `admin`, you'll be asked to change it), then generate a token under **My Account > Security** and run the analysis, which also produces JaCoCo coverage data:

```bash
./mvnw clean verify sonar:sonar -Dsonar.token=<your token>
```

Results appear in the SonarQube UI under the `product-catalog-api` project (see `sonar.host.url` / `sonar.projectKey` in `pom.xml` if you need to point at a different server).

## API

Base path: `/api/products`

| Method | Path                | Description                          |
|--------|---------------------|---------------------------------------|
| POST   | `/api/products`     | Create a product                     |
| GET    | `/api/products`     | List products (paginated)            |
| GET    | `/api/products/{id}`| Get a product by id                  |
| PUT    | `/api/products/{id}`| Update an existing product           |
| DELETE | `/api/products/{id}`| Delete a product                     |

Pagination on the list endpoint: `GET /api/products?page=0&size=20`.

### Product fields

| Field       | Type       | Notes                          |
|-------------|------------|---------------------------------|
| id          | Long       | auto-generated                  |
| name        | String     | required, max 150 characters    |
| description | String     | optional                        |
| price       | BigDecimal | required, > 0                   |
| stock       | Integer    | required, >= 0                  |
| createdAt   | DateTime   | set automatically on create     |
| updatedAt   | DateTime   | set automatically on update     |

### Example requests

Create a product:

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Widget", "description": "A useful widget", "price": 9.99, "stock": 100}'
```

List products:

```bash
curl "http://localhost:8080/api/products?page=0&size=20"
```

Get a product:

```bash
curl http://localhost:8080/api/products/1
```

Update a product:

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Widget v2", "description": "An even better widget", "price": 12.50, "stock": 80}'
```

Delete a product:

```bash
curl -X DELETE http://localhost:8080/api/products/1
```

### Error responses

Validation errors (missing/invalid fields) return `400 Bad Request`; a nonexistent product id on GET/PUT/DELETE returns `404 Not Found`. Both are handled centrally by a `@RestControllerAdvice`, with a consistent JSON body:

```json
{
  "timestamp": "2026-07-09T12:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 1",
  "details": []
}
```
