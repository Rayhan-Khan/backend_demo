# Healthfix Backend

Spring Boot 3 REST API with Firebase Authentication, PostgreSQL, and Google Cloud Storage.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4 |
| Security | Spring Security + Firebase Admin SDK |
| Database | PostgreSQL |
| Migrations | Flyway |
| ORM | Spring Data JPA + Hibernate |
| Query | QueryDSL |
| Storage | Firebase Cloud Storage |
| Docs | Springdoc OpenAPI (Swagger UI) |
| Mapping | MapStruct |
| Build | Gradle |
| Container | Docker + Docker Compose |

---

## Project Structure

```
src/main/java/com/healthfix/
├── annotation/         # Custom validation annotations (@ValidEmail, @ValidAvatar, @ValidEnum)
├── config/             # Spring beans (FirebaseConfig, AppConfig, OpenApiConfig)
├── constant/           # App-wide constants (messages, response status, validator keys)
├── controller/         # REST controllers (AuthController, UsersController, CurrentUserController)
├── dto/                # Request bodies (LoginRequest, UserRegistrationRequest, etc.)
├── entity/             # JPA entities (User, MediaStorage) + base classes
├── enums/              # Enumerations (UserStatus, ReferenceType)
├── exception/          # Global exception handler + custom exceptions
├── helper/             # Utility helpers
├── projection/         # JPA projections for optimized queries
├── repository/         # Spring Data repositories (UserRepository, MediaStorageRepository)
├── response/           # Response DTOs (LoginResponse, UserResponse, etc.)
├── security/           # Firebase auth filter, JWT generator, security config
├── service/            # Service interfaces + impl/ subpackage for implementations
├── utils/              # Response builder, pagination, common helpers
├── validation/         # Custom Jakarta validators
└── HealthfixApplication.java
```

---

## Authentication Flow

```
Client → sends Firebase ID Token (from Firebase SDK)
  → FirebaseAuthenticationFilter (verifies token with Firebase Admin SDK)
  → Spring SecurityContext (sets authenticated principal)
  → Controller (accesses current user via SecurityContext)
```

1. User signs in on the frontend using Firebase Auth (email/password or social)
2. Firebase returns an **ID Token** (short-lived JWT)
3. Client sends the ID Token in the `Authorization: Bearer <token>` header
4. `FirebaseAuthenticationFilter` calls `FirebaseAuth.verifyIdToken()` on every request
5. On success, the user is set in Spring's `SecurityContextHolder`
6. Protected endpoints are automatically secured — no session, fully stateless

---

## API Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Public | Register new user |
| POST | `/api/v1/auth/login` | Public | Login with Firebase ID token |
| POST | `/api/v1/auth/resend-verification` | Public | Resend email verification |
| GET | `/api/v1/auth/check-email` | Public | Check if email is registered |
| GET | `/api/v1/users/me` | Protected | Get current user profile |
| PUT | `/api/v1/users/me` | Protected | Update current user |

Swagger UI: `http://localhost:8000/swagger-ui/index.html`

---

## Local Setup

### Prerequisites
- Java 17
- Docker + Docker Compose
- Firebase project with service account

### 1. Clone & configure Firebase

Go to [Firebase Console](https://console.firebase.google.com/) → Project Settings → Service Accounts → Generate New Private Key.

Save the downloaded JSON as `firebase-service-account.json` in the project root.

### 2. Create `.env` file

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=healthfix_db
DB_USER=postgres
DB_PASSWORD=password
DB_SCHEMA=public
BUCKET_NAME=your-firebase-storage-bucket
AVATAR_MAX_SIZE=5MB
AVATAR_ALLOWED_FORMATS=jpg,jpeg,png,webp
```

### 3. Run with Docker Compose

```bash
docker compose build
docker compose up -d
```

### 4. Run locally (without Docker)

Start PostgreSQL separately, then:

```bash
./gradlew bootRun
```

---

## Database Migrations

Flyway runs automatically on startup. Migration files are in:

```
src/main/resources/db/migration/
├── V1__create_users_table.sql
└── V2__Create_MediaStorage.sql
```

Add new migrations as `V3__description.sql`, `V4__description.sql`, etc.

---

## Key Design Patterns

### Service Interface + Impl
Every service has an interface + implementation:
```
service/AuthService.java          ← interface (contract)
service/impl/AuthServiceImpl.java ← implementation (logic)
```
This makes unit testing with mocks straightforward.

### Response Builder
All API responses use a uniform structure via `ResponseBuilder`:
```json
{
  "status": 200,
  "message": "User registered successfully.",
  "data": { ... }
}
```

### Base Entities
- `BaseEntity` — `id`, `createdAt`
- `BaseEntityWithUpdate` — adds `updatedAt`
- `BaseEntitySoftDelete` — adds `deletedAt` for soft deletes

### Custom Validation Annotations
```java
@ValidEmail    // validates email format
@ValidAvatar   // validates file type/size for avatar uploads
@ValidEnum     // validates that a string matches an enum value
```

---

## Environment Variables Reference

| Variable | Description |
|---|---|
| `DB_HOST` | PostgreSQL host |
| `DB_PORT` | PostgreSQL port (default 5432) |
| `DB_NAME` | Database name |
| `DB_USER` | Database username |
| `DB_PASSWORD` | Database password |
| `DB_SCHEMA` | Schema name (default public) |
| `BUCKET_NAME` | Firebase Storage bucket name |
| `AVATAR_MAX_SIZE` | Max avatar upload size (e.g. 5MB) |
| `AVATAR_ALLOWED_FORMATS` | Comma-separated allowed image formats |
