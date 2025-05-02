# ShortLink

ShortLink is a simple URL shortening service built with Java and Spring Boot.

## Features

- `/api/encode`: Accepts a long URL and returns a shortened URL.
- `/api/decode`: Accepts a short URL and returns the original long URL.
- Concurrency limiting on both endpoints using a configurable semaphore.
- In-memory storage (no database).
- Swagger UI for API documentation.
- Unit tests for endpoint functionality and concurrency control.

---

## Tech Stack

- Java 17+
- Spring Boot 3.x
- JUnit 5 (for testing)
- Spring MockMvc
- OpenAPI (Swagger) for documentation

---

## Requirements

- JDK 17 or higher
- Maven 3.6+

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-repo/shortlink.git
cd shortlink
```

### 2. Configuration

In `src/main/resources/application.properties`, set the following:

```
concurrency.limit=5
server.port=8080
logging.level.root=INFO
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

---

## Example Usage

### Encode Request

**POST** `/api/encode`  
**Body:**
```json
{
  "url": "https://example.com/library/react"
}
```

**Response:**
```json
{
  "shortUrl": "http://short.est/fd3376"
}
```

### Decode Request

**POST** `/api/decode`  
**Body:**
```json
{
  "shortUrl": "http://short.est/fd3376"
}
```

**Response:**
```json
{
  "originalUrl": "https://example.com/library/react"
}
```

### When Concurrency Limit is Reached

**Response:**
```json
{
  "error": "Too many concurrent requests"
}
```

**HTTP Status:** `429 Too Many Requests`

---

## API Documentation

After the app starts, Swagger UI is available at:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Running Tests

To run all tests:

```bash
mvn test
```

Tests include:
- Encode and decode functionality
- Concurrency limit enforcement

---


## Author

Created by Busayo Phillips
