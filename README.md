# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W – Client-Server Architectures
**Author:** S.D.V. Samarathunga
**UoW ID:** 2151919w
**IIT ID:** 20241769

---

## API Overview

This RESTful API is built with **JAX-RS (Jersey 2.32)** and deployed on **Apache Tomcat**. It provides a complete backend for managing campus Rooms and IoT Sensors, including full historical logging of sensor readings. All data is stored entirely in-memory using thread-safe `ConcurrentHashMap` and `ArrayList` — no database is used.

**Base URL:** `http://localhost:8080/SmartCampusAPI/api/v1`

### Resource Hierarchy

```
/api/v1                                    → Discovery (API metadata + navigation links)
/api/v1/rooms                              → Room collection (GET, POST)
/api/v1/rooms/{roomId}                     → Single room (GET, DELETE)
/api/v1/sensors                            → Sensor collection (GET, POST)
/api/v1/sensors?type={type}               → Filtered sensors by type
/api/v1/sensors/{sensorId}/readings       → Sensor readings sub-resource (GET, POST)
```

### Pre-Seeded Data

The system starts with the following data for immediate testing:

| Room ID   | Name                    | Capacity |
|-----------|-------------------------|----------|
| HALL-101  | Main Lecture Hall       | 120      |
| LAB-202   | Network & Security Lab  | 40       |
| LIB-001   | Central Library         | 200      |

| Sensor ID | Type        | Status      | Room     |
|-----------|-------------|-------------|----------|
| TEMP-001  | Temperature | ACTIVE      | HALL-101 |
| HUM-001   | Humidity    | ACTIVE      | HALL-101 |
| CO2-001   | CO2         | ACTIVE      | LAB-202  |
| OCC-001   | Occupancy   | MAINTENANCE | LIB-001  |

---

## Technology Stack

- **Language:** Java 8
- **Framework:** JAX-RS with Jersey 2.32
- **Server:** Apache Tomcat
- **Build Tool:** Apache Maven
- **JSON:** Jackson (via jersey-media-json-jackson)
- **Data Storage:** In-memory ConcurrentHashMap

---

## How to Build and Run

### Prerequisites

- JDK 8 or higher
- Apache Maven 3.x
- Apache Tomcat 9.x

### Step 1 — Clone the Repository

```bash
git clone https://github.com/vishmikas/Smart-Campus-API.git
cd Smart-Campus-API
```

### Step 2 — Build the WAR File

```bash
mvn clean package
```

This produces `target/SmartCampusAPI.war`.

### Step 3 — Deploy to Tomcat

Copy the WAR into your Tomcat `webapps/` directory:

```bash
cp target/SmartCampusAPI.war /path/to/tomcat/webapps/
```

Start Tomcat:

```bash
# Linux / macOS
./bin/startup.sh

# Windows
bin\startup.bat
```

### Step 4 — Verify the API is Running

```bash
curl http://localhost:8080/SmartCampusAPI/api/v1
```

You should receive a JSON response with API metadata and resource links.

---

## API Endpoints Reference

### Discovery
| Method | Path        | Description              |
|--------|-------------|--------------------------|
| GET    | /api/v1     | API metadata and links   |

### Rooms
| Method | Path                    | Description                          | Success Code |
|--------|-------------------------|--------------------------------------|--------------|
| GET    | /api/v1/rooms           | List all rooms                       | 200          |
| POST   | /api/v1/rooms           | Create a new room                    | 201          |
| GET    | /api/v1/rooms/{roomId}  | Get a specific room by ID            | 200          |
| DELETE | /api/v1/rooms/{roomId}  | Delete a room (blocked if has sensors)| 200         |

### Sensors
| Method | Path                    | Description                          | Success Code |
|--------|-------------------------|--------------------------------------|--------------|
| GET    | /api/v1/sensors         | List all sensors (optional ?type=)   | 200          |
| POST   | /api/v1/sensors         | Register a new sensor                | 201          |

### Readings (Sub-Resource)
| Method | Path                                  | Description                     | Success Code |
|--------|---------------------------------------|---------------------------------|--------------|
| GET    | /api/v1/sensors/{sensorId}/readings   | Get all readings for a sensor   | 200          |
| POST   | /api/v1/sensors/{sensorId}/readings   | Post a new reading              | 201          |

### Error Codes
| Code | Scenario |
|------|----------|
| 400  | Missing or invalid request body fields |
| 403  | Posting a reading to a MAINTENANCE sensor |
| 404  | Requested room or sensor not found |
| 409  | Deleting a room that still has sensors |
| 415  | Wrong Content-Type sent to the API |
| 422  | Sensor registered with a non-existent roomId |
| 500  | Unexpected server error (no stack trace exposed) |

---

## Sample curl Commands

### 1. Discovery — GET /api/v1
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. Get All Rooms — GET /api/v1/rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 3. Create a New Room — POST /api/v1/rooms
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"CS-305","name":"Cyber Security Lab","capacity":35}'
```

### 4. Get a Specific Room — GET /api/v1/rooms/{roomId}
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/CS-305
```

### 5. Delete a Room (Success) — DELETE /api/v1/rooms/{roomId}
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/CS-305
```

### 6. Delete a Room With Sensors (409 Conflict) — DELETE /api/v1/rooms/{roomId}
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/HALL-101
```

### 7. Register a Sensor (Valid roomId) — POST /api/v1/sensors
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-099","type":"Temperature","status":"ACTIVE","currentValue":21.0,"roomId":"LAB-202"}'
```

### 8. Register a Sensor (Invalid roomId — 422) — POST /api/v1/sensors
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-999","type":"Temperature","status":"ACTIVE","currentValue":21.0,"roomId":"FAKE-ROOM"}'
```

### 9. Filter Sensors by Type — GET /api/v1/sensors?type=CO2
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

### 10. Post a Sensor Reading — POST /api/v1/sensors/{sensorId}/readings
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":26.5}'
```

### 11. Get All Readings for a Sensor — GET /api/v1/sensors/{sensorId}/readings
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

### 12. Post Reading to MAINTENANCE Sensor (403 Forbidden)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":10.0}'
```

---

## Project Structure

```
SmartCampusAPI/
├── pom.xml
└── src/main/java/lk/samarathunga/smartcampus/
    ├── config/
    │   └── SmartCampusApplication.java        ← @ApplicationPath("/api/v1")
    ├── model/
    │   ├── Room.java
    │   ├── Sensor.java
    │   ├── SensorReading.java
    │   └── ErrorResponse.java
    ├── store/
    │   └── DataStore.java                     ← ConcurrentHashMap in-memory store
    ├── resource/
    │   ├── DiscoveryResource.java             ← GET /api/v1
    │   ├── RoomResource.java                  ← /api/v1/rooms
    │   ├── SensorResource.java                ← /api/v1/sensors
    │   └── SensorReadingResource.java         ← /api/v1/sensors/{id}/readings
    ├── exception/
    │   ├── ResourceNotFoundException.java
    │   ├── ResourceNotFoundMapper.java        ← 404
    │   ├── RoomHasSensorsException.java
    │   ├── RoomHasSensorsMapper.java          ← 409
    │   ├── InvalidReferenceException.java
    │   ├── InvalidReferenceMapper.java        ← 422
    │   ├── SensorUnavailableException.java
    │   ├── SensorUnavailableMapper.java       ← 403
    │   └── GlobalExceptionMapper.java         ← 500 catch-all
    └── filter/
        └── LoggingFilter.java                 ← Request + Response logging
```

---

## Report — Answers to Coursework Questions

### Part 1.1 — JAX-RS Resource Lifecycle

By default, JAX-RS creates a **new instance** of every resource class for each incoming HTTP request (per-request scope). This means resource classes are not singletons — each request gets its own fresh object. The consequence is that instance variables on a resource class cannot hold shared state between requests; they are discarded after each call.

To safely maintain shared in-memory data across all requests, state must live **outside** the resource class, in a static, thread-safe structure. This project addresses this by using `ConcurrentHashMap` in the `DataStore` class, which is a static shared store accessible by all resource instances. `ConcurrentHashMap` handles concurrent reads and writes safely without explicit synchronization, preventing data loss and race conditions that would occur with a regular `HashMap`.

### Part 1.2 — HATEOAS and Hypermedia

HATEOAS (Hypermedia as the Engine of Application State) means that API responses include navigational links to related resources, enabling clients to discover and traverse the API dynamically without relying on hardcoded or externally documented URLs. For example, a response listing rooms could embed a link to each room's sensors endpoint.

This benefits client developers significantly: the API becomes self-describing, so the client can discover available actions directly from the response rather than consulting static documentation. It also decouples clients from server URL structures — if the server changes a path, the client follows the updated link from the response rather than breaking. The discovery endpoint in this project implements this principle by returning a `resources` map with links to all primary collections.

### Part 2.1 — Returning Full Objects vs IDs in Room Lists

Returning only IDs in a list is bandwidth-efficient but forces clients to make a separate GET request for every room they want details on — the classic N+1 request problem. This increases latency and client-side complexity significantly when there are many rooms.

Returning full room objects increases the initial payload size but eliminates follow-up requests, reducing total round-trips and simplifying client code. The right design depends on the use case: for a facilities dashboard that needs to display all room details at once, returning full objects is the better choice. For a simple dropdown that only needs room names and IDs, a lightweight summary response is more appropriate. This API returns full objects for maximum usability.

### Part 2.2 — Is DELETE Idempotent?

In this implementation, DELETE is **partially idempotent** from a server-state perspective but not from a response-code perspective. The first DELETE on a room removes it and returns 200 OK. A second identical DELETE on the same room ID returns 404 Not Found via `ResourceNotFoundException`.

HTTP defines idempotent as producing the same **server state** on repeated calls — the resource is absent after all calls regardless, so the state is consistent. However, the response code changes between the first and subsequent calls. A fully idempotent implementation would return 204 No Content on all calls, including when the resource is already absent. The current design prioritises informing the client accurately: returning 404 on the second call tells the client the resource does not exist, which is the correct, honest response.

### Part 3.1 — @Consumes and Content-Type Mismatch

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells the JAX-RS runtime to only route requests to this method if the incoming `Content-Type` header is `application/json`. If a client sends a request with `Content-Type: text/plain` or `application/xml`, the runtime cannot find a matching method for that media type and automatically returns **HTTP 415 Unsupported Media Type** before the method body is ever entered.

This is handled entirely by JAX-RS's content negotiation mechanism — no manual checking is needed. It protects the application from receiving incorrectly formatted input and provides a clear, standards-compliant error response to misbehaving clients.

### Part 3.2 — Query Parameter vs Path Segment for Filtering

Using `@QueryParam` (e.g., `GET /sensors?type=CO2`) is the superior approach for filtering because query parameters are semantically **optional modifiers** on a collection, not a different resource. The path `/sensors` always refers to the full sensor collection; `?type=CO2` simply constrains the result set. This aligns with REST principles: the URI identifies a resource, and query parameters refine how it is retrieved.

Embedding the filter in the path (`/sensors/type/CO2`) implies a fundamentally different resource hierarchy and complicates routing. It also makes combining multiple filters awkward — `?type=CO2&status=ACTIVE` is natural in query params but becomes `/sensors/type/CO2/status/ACTIVE` in path form, which is fragile and unintuitive. Query parameters are also natively supported by HTTP caching headers and standard URL conventions, making them the correct tool for search and filter operations.

### Part 4.1 — Sub-Resource Locator Pattern

The sub-resource locator pattern delegates a URL subtree to a dedicated class. In `SensorResource`, the method annotated with `@Path("/{sensorId}/readings")` does not handle the request itself — it returns an instance of `SensorReadingResource`, which then handles all GET and POST operations under that path. JAX-RS resolves the remaining path segments against the returned object.

This pattern provides significant architectural benefits. It enforces **separation of concerns** — `SensorResource` manages the sensor collection, while `SensorReadingResource` focuses entirely on reading history. In a large API, placing every nested endpoint in one controller class creates an unmaintainable, bloated file. Sub-resource locators allow the system to scale: new nested resources can be added as independent classes without modifying existing ones, adhering to the Open/Closed Principle.

### Part 5.2 — HTTP 422 vs 404 for Missing Reference

A `404 Not Found` response implies that the requested URL does not correspond to any resource on the server. In this scenario the URL `/api/v1/sensors` is entirely valid — the problem is not the endpoint but the **content of the JSON body**, which references a `roomId` that does not exist.

`422 Unprocessable Entity` is semantically more accurate because it signals that the server understood the request, the URL was correct, the JSON was syntactically valid, but it cannot process the request due to a **semantic validation failure** — the referenced entity does not exist in the system. Returning 404 would mislead the client into thinking the sensors endpoint itself was not found, causing confusion about whether the endpoint exists at all. The `InvalidReferenceException` and its mapper in this project return 422 for precisely this reason.

### Part 5.4 — Security Risk of Exposing Stack Traces

Exposing raw Java stack traces to external API consumers is a significant security vulnerability for several reasons. Stack traces reveal the **internal package structure and class names** of the application, giving attackers a map of the codebase architecture. They expose **framework and library versions** (e.g., Jersey 2.32, Jackson), enabling targeted exploitation of known CVEs for those specific versions. They may reveal **method names, file paths, and exact line numbers**, providing precise insight into business logic flow. They also indicate **which inputs cause which failures**, allowing an attacker to probe the API systematically to identify injection points, null-pointer vulnerabilities, or unvalidated code paths.

The `GlobalExceptionMapper<Throwable>` in this project intercepts all unhandled exceptions and returns only a generic "An unexpected error occurred on the server" message with HTTP 500, ensuring none of this internal information is ever exposed to external clients.

### Part 5.5 — Why Filters for Cross-Cutting Concerns

Cross-cutting concerns such as logging, authentication, and CORS affect every request but have nothing to do with business logic. If logging is inserted manually into each resource method, it is trivially easy to miss new endpoints, it couples unrelated responsibilities together, and updating the log format requires modifying every class in the project.

A single JAX-RS filter implementing both `ContainerRequestFilter` and `ContainerResponseFilter` and annotated with `@Provider` is automatically applied to every request and response by the runtime, with zero changes needed to resource classes. This ensures **consistent, centralised, and maintainable** observability. It also strictly adheres to the Single Responsibility Principle — resource methods focus purely on business logic, and the filter focuses purely on cross-cutting infrastructure. Adding a new resource class automatically inherits logging without any additional code.
