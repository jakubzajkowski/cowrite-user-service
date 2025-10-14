# User Service

User Service is a microservice responsible for managing user authentication, authorization, registration, and login functionalities. It provides secure handling of user accounts and JWT-based session management.

## Features

- User registration and login

- JWT-based authentication and authorization

- Session management via httponly cookies

## Prerequisites

Before running the service, you need to create a configuration file named `secrets.properties` in the root directory with the following content:
```properties
# JWT configuration
jwt.cookie.name=COWRITE_SESSION_ID
jwt.secret=your_jwt_secret_key_here
jwt.expiration=3600000

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/cowrite_user_service_db
spring.datasource.username=db_user
spring.datasource.password=db_password
spring.data.redis.password=redis_password
```
- Make sure to update these values if you use a different database or want a stronger JWT secret.

## Running the Service

1. Ensure PostgreSQL is running and the database cowrite_user_service_db exists.

1. Create the secrets.properties file as described above.

1. Build and run the service using Maven or your preferred IDE:

```js
./mvnw clean install
./mvnw spring-boot:run
```



4. The service will start on http://localhost:8080 by default.

JWT tokens are stored in a cookie named as specified in secrets.properties.
Ensure strong secrets for production environments.
Database migrations are expected to be handled externally or via a tool like Flyway or Liquibase.


## CI/CD (GitHub Actions)

The service has GitHub Actions configured for CI/CD, performing the following steps:

1. Run unit and integration tests (`mvn test`)
2. Build Docker image
3. Push Docker image to Docker Hub


## API Requests

### User registration
**POST** `http://localhost:8080/api/auth/register`  
**Content-Type:** `application/json`

Request Body:

```json
{
  "username": "testuser1",
  "email": "test1@example.com",
  "password": "Test1234"
}
```
### User login
**POST** `http://localhost:8080/api/auth/login`  
**Content-Type:** `application/json`

Request Body:

```json
{
  "email": "test1@example.com",
  "password": "Test1234"
}
```
### Check current user
**GET** `http://localhost:8080/api/auth/me`

**Headers:** Include the HttpOnly JWT cookie automatically

Response Example:

```json
{
  "id": 1,
  "username": "testuser1",
  "email": "test1@example.com"
}
```