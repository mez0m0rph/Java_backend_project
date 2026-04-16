# Team Task Tracker API

Backend REST API for team project and task management with JWT authentication, role-based access control, task history, filtering, and PostgreSQL persistence.

## Features

- JWT authentication and authorization
- Roles: ADMIN, MANAGER, USER
- Project creation and membership management
- Task creation, update, assignment, status and priority changes
- Comments for tasks
- Audit history for project and task actions
- Filtering, pagination and sorting for tasks
- Flyway migrations
- PostgreSQL integration
- Docker and Docker Compose support
- Swagger UI
- GitHub Actions CI
- Integration tests with Testcontainers

## Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Lombok
- springdoc OpenAPI
- Testcontainers
- Docker

## Project Structure

```text
src/main/java/com/panin/tasktracker
├── common
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
├── security
└── service
```

## Run locally

### Option 1: Docker Compose

```bash
docker compose up --build
```

### Option 2: Local Maven + PostgreSQL

1. Create PostgreSQL database `task_tracker`
2. Copy `.env.example` values into your environment
3. Start the application

```bash
mvn spring-boot:run
```

## Default seeded users

All seeded users use password `password`.

- `admin`
- `manager`
- `user`

## API Docs

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Main Endpoints

### Auth

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

### Users

- `GET /api/v1/users/me`
- `GET /api/v1/users`
- `GET /api/v1/users/{id}`

### Projects

- `POST /api/v1/projects`
- `GET /api/v1/projects`
- `GET /api/v1/projects/{id}`
- `POST /api/v1/projects/{id}/members`
- `GET /api/v1/projects/{id}/members`

### Tasks

- `POST /api/v1/projects/{projectId}/tasks`
- `PATCH /api/v1/tasks/{taskId}`
- `GET /api/v1/tasks/{taskId}`
- `GET /api/v1/tasks?projectId=1&status=IN_PROGRESS&priority=HIGH&page=0&size=20&sortBy=createdAt&sortDir=desc`
- `POST /api/v1/tasks/{taskId}/comments`
- `GET /api/v1/tasks/{taskId}/comments`
- `GET /api/v1/tasks/{taskId}/history`

## Example workflow

1. Login as `admin`
2. Create a project
3. Add `manager` or `user` as project members
4. Create tasks inside the project
5. Change task status and assignee
6. Review comments and task history

## Notes

- The first user registered in an empty database gets `ROLE_ADMIN`
- Project visibility is restricted to project members and admins
- Task assignee must belong to the same project

## Roadmap

- Refresh tokens
- Email notifications
- File attachments
- Metrics and monitoring
- Redis caching
