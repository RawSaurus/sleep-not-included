# Sleep Not Included — Backend

Microservices-based backend system built as a community platform for the game *Oxygen Not Included*. The architecture is composed of Spring Boot 4 / Java 21 services.

I spent around 200 hours playing ONI and I enjoyed it so much that, that I spent double of that time on building a website for it. I haven't even played the game in the last month.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [API Documentation](#api-documentation)
- [Observability](#observability)
- [Contributing](#contributing)

---

## Architecture Overview

The system follows a microservices architecture where each domain is an independently deployable Spring Boot application with its own PostgreSQL schema. All external traffic enters through a reactive **Spring Cloud Gateway** (WebFlux), which handles authentication, routing, circuit breaking, retries, and rate limiting. Services register with a **Netflix Eureka** discovery server, and centralized configuration is served via **Spring Cloud Config Server** with live refresh events propagated through **RabbitMQ** (Spring Cloud Bus).

```
Client
  │
  ▼
Spring Cloud Gateway (port 8080)
  │   ├── JWT validation (Keycloak / OAuth2 Resource Server)
  │   ├── Resilience4j Circuit Breakers & Retry
  │   └── Rate Limiting
  │
  ├──▶ User Service        (8081)
  ├──▶ Build Service       (8082)
  ├──▶ Comment Service     (8083)
  ├──▶ Tag Service         (8084)
  ├──▶ Image Service       (8085)
  └──▶ Game Resource Svc   (8086)

Supporting infrastructure:
  Eureka Server         (8761)
  Config Server         (8888)
  Keycloak              (8443)
  PostgreSQL            (5432)
  RabbitMQ              (5672)
  MinIO                 (9002)
  Zipkin / Tempo        (9411)
  Prometheus + Grafana  (3000)
```

---

## Services

| Service | Port | Description |
|---|---|---|
| `gateway` | 8080 | Reactive API gateway — routing, auth, circuit breaking, rate limiting |
| `sleep-not-included.user` | 8081 | User management, Keycloak identity sync |
| `sleep-not-included.build` | 8082 | Build CRUD, tagging, pagination |
| `sleep-not-included.comment` | 8083 | Threaded comments with nested replies |
| `sleep-not-included.tag` | 8084 | Tag management |
| `sleep-not-included.image` | 8085 | Image upload/retrieval backed by MinIO |
| `sleep-not-included.gameres` | 8086 | Game resource data (buildings, materials, etc.) |

#### NOTICE

`sleep-not-included.gameres` is fully functional service but is not used anywhere. I was in a flow and sadly only after I built entire service I realized that building inheritance structured entities containing JSON structures with polymorphic DTO mapping
is the **most complex and stupid** way to build this. This service will be deleted in future and instead I will scrape and parse game resources from game files directly into the frontend

---

## Tech Stack

**Core**
- Java 21
- Spring Boot 4.0
- Spring Cloud 2025 (Gateway, Config, Bus, Netflix Eureka, OpenFeign, LoadBalancer)

**Security**
- Keycloak (OAuth2 / OIDC identity provider)
- Spring Security OAuth2 Resource Server (JWT)
- Custom JWT authority extractor mapping Keycloak realm roles to Spring Security `GrantedAuthority`
- Stateless session management across all services

**Data**
- Spring Data JPA + Hibernate (DDL validation in production)
- PostgreSQL (per-service schema isolation)
- MapStruct (DTO mapping)
- Lombok

**Messaging**
- RabbitMQ (async inter-service events, Spring Cloud Bus config refresh)

**Resilience**
- Resilience4j — Circuit Breaker, Retry, Rate Limiter (configured per route in gateway)

**Storage**
- MinIO (S3-compatible object storage for images)

**Observability**
- Micrometer + Micrometer Brave (distributed tracing)
- Zipkin / Grafana Tempo (trace collection)
- Prometheus (metrics scraping)
- Grafana (dashboards)

**Documentation**
- SpringDoc OpenAPI / Swagger UI (per service + aggregated via gateway)

---

## Getting Started

### Prerequisites

- Java 21+
- Spring 4
- Maven 3.9+
- Docker & Docker Compose

### 1. Clone the repository

```bash
git clone https://github.com/your-org/sleep-not-included.git
cd sleep-not-included
```

### 2. Configure environment

Config server pulls configurations from private repo. Ask owner for auth token to access repo. 
Alternatively, each service has `application.yaml` with commented out section, which represents minimal necessary config for dev env with dummy data, so you can create your own repo, provide url to config-management service and copy those values there. Name each config file as `${spring.application.name}-dev.yaml` 
Each service reads secrets from an `env.properties` file (gitignored). Create one per service based on the template below. See [Environment Variables](#environment-variables) for the full list.

### 3. Set up infrastructure

#### :warning: IMPORTANT
Project is in process of dockerization and contains several `docker-compose` files. *DO NOT RUN* files from `/docker` directory.
Set up only docker images mentioned in the next steps. :warning:

1.
- In root rename `env.txt`:
```bash
cp env.txt .env
```
- Then fill in the values in `.env`
- Run the docker compose:
```bash
docker compose up -d
```

2.
- Navigate to `/evaluate-loki`:
```bash
cd evaluate-loki
```
- Observability is set up in dev mode with no auth, no `.env` needed 
- Run the docker compose:
```bash
docker compose up -d
```

### 4. Start the Config Server and Eureka Server first

```bash
cd config-server && mvn spring-boot:run
cd eureka-server && mvn spring-boot:run
```

### 5. Start remaining services

Start each service independently in any order after Config and Eureka are up:

```bash
cd sleep-not-included.user && mvn spring-boot:run
cd sleep-not-included.build && mvn spring-boot:run
cd sleep-not-included.comment && mvn spring-boot:run
cd sleep-not-included.tag && mvn spring-boot:run
cd sleep-not-included.image && mvn spring-boot:run
cd sleep-not-included.gameres && mvn spring-boot:run
cd gateway && mvn spring-boot:run
```

All requests should be made through the gateway at `http://localhost:8080`.

---

## Environment Variables

Each service expects an `env.properties` file at its root (excluded from version control via `.gitignore`). The following variables are required:

| Variable | Description |
|---|---|
| `PSQL_USERNAME` | PostgreSQL username |
| `PSQL_PASSWORD` | PostgreSQL password |
| `RABBITMQ_USER` | RabbitMQ username |
| `RABBITMQ_PASSWORD` | RabbitMQ password |

The gateway and security-enabled services additionally require Keycloak configuration, which is set in the YAML config files under `sni.security.*`:

| Property | Description |
|---|---|
| `sni.security.keycloak-server-url` | Keycloak base URL (e.g. `http://localhost:8443`) |
| `sni.security.realm` | Keycloak realm name |
| `sni.security.client-id-frontend` | Client ID used by the Angular frontend |
| `sni.security.client-uuid-frontend` | UUID of the frontend Keycloak client |
| `sni.minio.url` | MinIO server URL |
| `sni.minio.access-key` | MinIO access key |
| `sni.minio.secret-key` | MinIO secret key |
| `sni.minio.bucket` | MinIO bucket name |

---

## API Documentation

Each service exposes a Swagger UI at `/api/v1/swagger-ui.html` and OpenAPI docs at `/api/v1/v3/api-docs`.

Through the gateway, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

OAuth2 PKCE login is pre-configured in the Swagger UI for authenticated endpoint testing.

---

## Observability

| Tool | Purpose | Default URL |
|---|---|---|
| Eureka Dashboard | Service registry status | `http://localhost:8761` |
| Zipkin | Distributed trace viewer | `http://localhost:9411` |
| Prometheus | Metrics scraping | `http://localhost:9090` |
| Grafana | Dashboards | `http://localhost:3000` |
| Actuator | Per-service health & metrics | `http://localhost:{port}/api/v1/actuator` |

Tracing is enabled at 100% sampling in dev profiles and 10% in production profiles.

---

## Contributing

As of now I run this as a solo project, but for possible future contributions, here is a basic workflow.

### Branching Strategy

```
master        — stable, production-ready
develop       — integration branch for features
feature/*     — individual feature branches
fix/*         — bug fixes
```

### Workflow

1. Fork the repository and create a new branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. Follow existing patterns.
3. Ensure your service correctly registers with Eureka.
4. Test your changes locally with all dependent services running.
5. Open a Pull Request with a clear description of the change.
