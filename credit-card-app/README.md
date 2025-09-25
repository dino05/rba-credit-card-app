# Credit Card Application

A Spring Boot application for managing credit card applications and clients with PostgreSQL database, Kafka integration, and OpenAPI documentation.

## 📋 Project Overview

This application provides a complete solution for:
- Client registration and management
- Credit card application processing
- Real-time status updates via Kafka
- RESTful API with comprehensive documentation
- Database migrations with Flyway

## 🚀 Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.6** or higher
- **Docker** and **Docker Compose** (for database and Kafka)
- **NPM**

### 1. Clone and Setup

```bash
git clone https://github.com/dino05/rba-credit-card-app.git
cd credit-card-app
```

### 2. Start Infrastructure Services

```bash
docker-compose up -d postgres kafka zookeeper
docker ps
```

### 3. Build and Run Backend app

```bash
mvn clean compile
mvn spring-boot:run
```

The application will start on http://localhost:8080

### 4. Build and Run Frontend app

```bash
npm install
npm run dev
```

The application will start on http://localhost:3000

## API Documentation

Once running, access the API documentation:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Database

### Database Schema

The application uses Flyway for database migrations. The schema is created automatically on startup.

### Accessing the Database

Connect to the database using:

```bash
docker exec -it creditcard-postgres psql -U postgres -d creditcarddb
```

## API Endpoints

### Client Management Endpoints

| Method |  Endpoint  | Description |
|:-----|:--------:|------:|
| POST   | `/api/v1/clients`  | 	Register a new client |
| GET   |  `/api/v1/clients/{oib}`  |   Get client by OIB |
| GET   | `/api/v1/clients` |    Get paginated clients list |
| DELETE   | `/api/v1/clients/{oib}` |    Delete client by OIB |
| PATCH   | `/api/v1/clients/{oib}/status` |    Update client status |

### Card Request Endpoints

| Method |  Endpoint  | Description |
|:-----|:--------:|------:|
| POST   | `/api/v1/card-requests`  | 	Create new card request |

### Kafka Testing Endpoints

| Method |  Endpoint  | Description |
|:-----|:--------:|------:|
| POST   | `/api/v1/kafka-test/card-status`  | 	Send test Kafka message |

### Pagination Parameters

- `page` - Page number (0-based, default: 0)
- `size` - Items per page (default: 10)
- `sortBy` - Field to sort by (default: firstName)
- `direction` - Sort direction (asc/desc, default: asc)

#### Example usage:

```http
GET /api/v1/clients?page=0&size=5&sortBy=lastName&direction=asc
```

## OpenAPI Code Generation

### Generating Client Code

#### OpenAPI code is generated automatically during build:

```bash
mvn clean compile
```

### Generated Code Location

```
target/generated-sources/openapi/src/main/java/com/rba/creditcardapp/generated/
```

## Docker Support

### Development with Docker
#### Start all services:


```bash
docker-compose up -d
```

#### View logs:

```bash
docker-compose logs -f
```

#### Stop services:

```bash
docker-compose down
```