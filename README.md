# TaskManager API

A secure RESTful task management API built with Java 21 and Spring Boot. Supports JWT authentication, PostgreSQL persistence, and user-specific task management.

## Features

- CRUD operations for tasks
- JWT authentication and BCrypt password hashing
- User-specific task ownership
- Pagination and completion-status filtering
- Input validation and structured error responses
- Swagger / OpenAPI documentation

## Tech Stack

Java 21 · Spring Boot · Spring Security · PostgreSQL · Spring Data JPA · JWT · Maven · JUnit 5 · Mockito

## Prerequisites

- JDK 21
- PostgreSQL
- Git

## Configuration

Create a PostgreSQL database named `taskmanager_db`. Configure the database connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager_db
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
jwt.expiration-ms=86400000
```

Set `DB_PASSWORD` and `JWT_SECRET` as environment variables. The JWT secret must be a secure Base64-encoded key of at least 32 bytes. Never commit credentials or secrets to GitHub.

## Run Locally

```bash
git clone <YOUR_REPOSITORY_URL>
cd <YOUR_REPOSITORY_FOLDER>
```

Windows PowerShell:

```powershell
$env:DB_PASSWORD = "your-database-password"
$env:JWT_SECRET = "your-base64-encoded-secret"
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`.

## Main Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Log in and obtain a JWT |
| GET | `/api/tasks` | List tasks |
| GET | `/api/tasks/{id}` | Retrieve a task |
| POST | `/api/tasks` | Create a task |
| PUT | `/api/tasks/{id}` | Update a task |
| DELETE | `/api/tasks/{id}` | Delete a task |

Task endpoints require a JWT in the `Authorization: Bearer <token>` header. Task access is restricted to the authenticated owner.

The listing endpoint supports `page`, `size`, and `completed` query parameters.

## API Documentation

Start the application and visit:

`http://localhost:8080/swagger-ui/index.html`

## Tests

Run the test suite:

```powershell
.\mvnw.cmd clean test
```

## Status

Implemented and tested locally. Deployment is not yet configured.
