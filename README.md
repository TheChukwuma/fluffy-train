# fluffy-train

Multi-module Spring Boot project for a reusable **JWT security starter** (`core-security-starter`) and a **sample application** that consumes it. It demonstrates username/password authentication with BCrypt, signed JWTs, role-based access control, and cross-cutting concerns centralized in the library.

## Prerequisites

- **Java 21** (or 24: set `<java.version>` in the root `pom.xml` and use a matching JDK).
- **Maven 3.9+**

## Stack

| Item | Version |
|------|---------|
| Spring Boot | 3.5.13 |
| Java | 21 |
| JWT | jjwt 0.12.x |

## Build

From the repository root:

```bash
mvn clean verify
```

Build only the starter JAR:

```bash
mvn -pl core-security-starter clean install
```

## Run the sample application

```bash
mvn -pl sample-application spring-boot:run
```

The app listens on **port 8080** by default.

### Seeded users (development)

| Username | Password | Roles |
|----------|----------|--------|
| `alice` | `alice-secret` | `USER` |
| `bob` | `bob-secret` | `USER`, `ADMIN` |

## Example HTTP requests

### Login (obtain JWT)

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"alice-secret"}'
```

Example response:

```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresInSeconds": 3600
}
```

### Public endpoint (no token)

```bash
curl -s http://localhost:8080/api/public/health
```

### Authenticated user profile

```bash
TOKEN="<paste accessToken from login>"

curl -s http://localhost:8080/api/user/me \
  -H "Authorization: Bearer $TOKEN"
```

### Admin-only listing

```bash
curl -s http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $TOKEN"
```

Use `bob` / `bob-secret` to obtain a token with `ROLE_ADMIN`. Requests with only `ROLE_USER` receive **403** with a JSON error body.

## Configuration (starter)

The starter reads `core.security` properties (see `sample-application/src/main/resources/application.yml`):

| Property | Purpose |
|----------|---------|
| `core.security.jwt.secret` | HMAC key for HS256 (minimum 32 bytes when interpreted as UTF-8; use a strong secret in production). |
| `core.security.jwt.expiration-ms` | Token lifetime. |
| `core.security.public-path-patterns` | Ant patterns allowed without authentication (e.g. `/api/public/**`, `/api/auth/**`). |
| `core.security.user-path-patterns` | Patterns requiring an authenticated user. |
| `core.security.admin-path-patterns` | Patterns requiring `ROLE_ADMIN`. |

Override the secret in production, for example:

```bash
export JWT_SECRET="$(openssl rand -base64 48)"
mvn -pl sample-application spring-boot:run
```

(`application.yml` maps `JWT_SECRET` to `core.security.jwt.secret`.)

## Module layout

- **`core-security-starter`**: Auto-configuration (`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`), JWT issuance and validation filter, BCrypt `PasswordEncoder` (if none provided), login endpoint, JSON **401/403** handling, validation error format, authenticated-request logging, and configurable URL security rules.
- **`sample-application`**: Domain model, JPA users, `UserDetailsService`, and controllers that illustrate **public**, **authenticated**, and **admin** routes. No security cross-cutting logic beyond providing users and credentials.

## Design decisions and trade-offs

1. **Stateless JWT in the filter**  
   After login, the JWT carries `userId`, `username`, and roles. The filter validates the signature and builds a `JwtPrincipal` without a database round-trip per request. **Trade-off**: role changes take effect only after re-login (or you would add optional revocation/introspection outside this assessment scope).

2. **`UserDetailsWithId` in the starter**  
   Applications implement `UserDetails` plus `UserDetailsWithId` so the issued token includes a stable `userId` claim. If absent, the starter falls back to using the username as the subject.

3. **URL rules in configuration**  
   Public, user, and admin Ant patterns keep the sample app free of security wiring while remaining flexible for consumers via YAML. The sample also uses `@PreAuthorize("hasRole('ADMIN')")` on the admin controller to demonstrate **method-level** checks in addition to URL rules.

4. **BCrypt and `DaoAuthenticationProvider`**  
   Spring’s default `AuthenticationManager` uses the application’s `UserDetailsService` and the starter’s `PasswordEncoder` for credential checks at login only.

5. **Spring Boot 3.5.x on Java 21**  
   This matches current LTS usage and keeps APIs stable. Newer Spring Boot 4.x is available; migrating would mainly require verifying dependency and Jakarta baseline compatibility.

## Tests

Integration tests in `sample-application` use **MockMvc** and `@SpringBootTest` to cover login, public access, authenticated `/api/user/me`, and admin-only access. Run:

```bash
mvn test
```
