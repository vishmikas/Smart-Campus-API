# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W – Client-Server Architectures  
**Author:** S.D.V. Samarathunga  
**UoW ID:** 2151919w  
**IIT ID:** 20241769

---

## API Overview

This RESTful API is built with **JAX-RS** and deployed on **Apache Tomcat**. It provides a backend for managing campus Rooms and IoT Sensors, including a full historical log of sensor readings. Data is stored entirely in-memory using `ConcurrentHashMap` and `ArrayList`.

**Base URL:** `http://localhost:8080/SmartCampusAPI/api/v1`

### Resource Hierarchy

```
/api/v1                              → Discovery (API metadata)
/api/v1/rooms                        → Room collection
/api/v1/rooms/{roomId}               → Single room
/api/v1/sensors                      → Sensor collection
/api/v1/sensors?type={type}          → Filtered sensors
/api/v1/sensors/{sensorId}/readings  → Sensor readings (sub-resource)
```

---

## How to Build and Run

### Prerequisites

- JDK 8 or higher
- Apache Maven
- Apache Tomcat

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/vishmikas/Smart-Campus-API.git
   cd SmartCampusAPI
   ```

2. **Build the WAR file**
   ```bash
   mvn clean package
   ```
   This produces `target/SmartCampusAPI.war`.

3. **Deploy to Tomcat**
    - Copy `target/SmartCampusAPI.war` into your Tomcat `webapps/` folder.
    - Start Tomcat: `./bin/startup.sh` (Linux/Mac) or `bin\startup.bat` (Windows)

4. **Verify the API is running**
   ```bash
   curl http://localhost:8080/SmartCampusAPI/api/v1
   ```

---

## Sample curl Commands

### 1. Discovery — GET /api/v1
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. Get all rooms — GET /api/v1/rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 3. Create a new room — POST /api/v1/rooms
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"CS-305","name":"Cyber Security Lab","capacity":35}'
```

### 4. Register a sensor — POST /api/v1/sensors
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-099","type":"Temperature","status":"ACTIVE","currentValue":21.0,"roomId":"CS-305"}'
```

### 5. Filter sensors by type — GET /api/v1/sensors?type=CO2
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

### 6. Post a sensor reading — POST /api/v1/sensors/{sensorId}/readings
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"id":"READ-001","timestamp":1714000000000,"value":24.7}'
```

### 7. Get all readings for a sensor — GET /api/v1/sensors/{sensorId}/readings
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

### 8. Delete a room — DELETE /api/v1/rooms/{roomId}
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/CS-305
```

### 9. Try to delete a room that still has sensors (expects 409 Conflict)
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/HALL-101
```

### 10. Post reading to MAINTENANCE sensor (expects 403 Forbidden)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"id":"READ-X","timestamp":1714000000000,"value":10.0}'
```

---

## Report — Answers to Coursework Questions

### Part 1.1 — JAX-RS Resource Lifecycle

By default, JAX-RS creates a **new instance** of every resource class for each incoming HTTP request (per-request scope). This means resource classes are not singletons. The consequence is that instance variables on a resource class cannot be used to hold shared state — they are reset with every request. To safely maintain shared in-memory data across requests, state must live **outside** the resource class, in a static, thread-safe structure. This project uses `ConcurrentHashMap` in the `DataStore` class, which is shared by all resource instances without risk of data loss or race conditions.

### Part 1.2 — HATEOAS and Hypermedia

HATEOAS (Hypermedia as the Engine of Application State) means that API responses include links to related resources, allowing clients to navigate the API dynamically without relying on hardcoded URLs. For example, a room response could embed a link to its sensors. This benefits client developers because the API becomes self-describing: the client can discover available actions from the response itself rather than consulting external documentation. It also decouples clients from server URL structures, so server-side changes to paths are less likely to break clients.

### Part 2.1 — Returning Full Objects vs IDs in Lists

Returning only IDs in a list is lightweight — minimal bandwidth — but forces clients to make a second request for each item to retrieve details. Returning full objects increases payload size but eliminates the N+1 request problem, reducing latency and client-side complexity. The right choice depends on context: for a dashboard loading many rooms, returning full objects is better. For a dropdown that only needs names and IDs, returning summaries is more efficient.

### Part 2.2 — Is DELETE Idempotent?

In this implementation, DELETE is **partially idempotent**. The first call removes the room and returns 200 OK. A second call on the same room ID returns 404 Not Found (via `ResourceNotFoundException`). Strictly speaking, HTTP defines idempotent as producing the same **server state** on repeated calls — the resource is gone regardless, so the state is consistent. However, the response code differs between the first and subsequent calls. A fully idempotent implementation would return 204 No Content on all calls, including when the resource is already absent.

### Part 3.1 — @Consumes and Content-Type Mismatch

When a client sends a request with `Content-Type: text/plain` to an endpoint annotated with `@Consumes(MediaType.APPLICATION_JSON)`, JAX-RS cannot match the content type to any registered method. It returns **HTTP 415 Unsupported Media Type** automatically. This is enforced by the framework's content negotiation mechanism before the method body is even entered, protecting the application from malformed input.

### Part 3.2 — Query Param vs Path Segment for Filtering

Using `@QueryParam` (e.g., `GET /sensors?type=CO2`) is considered better practice for filtering because query parameters are semantically optional and do not change the identity of the resource. The path `/sensors` always refers to the sensor collection; the `type` parameter is simply a filter on it. By contrast, embedding the filter in the path (`/sensors/type/CO2`) implies a different resource hierarchy, complicates routing, and makes it awkward to combine multiple filters (e.g., `?type=CO2&status=ACTIVE`). Query parameters are also naturally supported by HTTP caching and standard URL conventions.

### Part 4.1 — Sub-Resource Locator Pattern

The sub-resource locator pattern delegates a URL segment to a dedicated class. Instead of placing reading endpoints inside `SensorResource`, a locator method returns a `SensorReadingResource` instance, which handles all paths under `/{sensorId}/readings`. This improves **separation of concerns** — each class has a focused responsibility. In a large API with dozens of nested resources, putting every endpoint in one controller creates an unmanageable, bloated class. Sub-resource locators allow the API to scale horizontally: new nested resources can be added by creating new classes without modifying existing ones.

### Part 5.2 — HTTP 422 vs 404 for Missing Reference

A `404 Not Found` implies that the requested URL does not correspond to any resource on the server. In this case the URL `/api/v1/sensors` is valid — the problem is that the JSON **body** references a `roomId` that does not exist. `422 Unprocessable Entity` is more accurate because it signals that the request was syntactically correct and the server understood it, but it cannot process it due to semantic validation failure (the referenced entity is missing). Returning 404 would mislead the client into thinking the sensor endpoint itself was not found.

### Part 5.4 — Security Risk of Exposing Stack Traces

Exposing raw Java stack traces to external clients is a significant security risk for several reasons. Stack traces reveal the **internal package structure and class names** of the application, which helps an attacker map out the codebase. They can expose **framework versions and library names**, enabling targeted exploitation of known CVEs. They may reveal **method names, file paths, and line numbers**, giving attackers precise insight into business logic. They can also indicate **which inputs cause failures**, allowing attackers to probe systematically. The global exception mapper in this project prevents all of this by returning only a generic 500 message.

### Part 5.5 — Why Filters for Cross-Cutting Concerns

Cross-cutting concerns such as logging, authentication, and CORS should be handled in filters rather than duplicated inside every resource method. If logging is inserted manually into each method, it is easy to miss endpoints, it couples unrelated logic together, and updating the logging format requires touching every class. A single JAX-RS filter annotated with `@Provider` is automatically applied to every request and response, ensuring **consistent, centralised, and maintainable** behaviour. It also adheres to the Single Responsibility Principle — resource methods focus solely on business logic.

---

## Project Structure

```
SmartCampusAPI/
├── pom.xml
└── src/main/java/lk/samarathunga/smartcampus/
    ├── config/
    │   └── SmartCampusApplication.java
    ├── model/
    │   ├── Room.java
    │   ├── Sensor.java
    │   ├── SensorReading.java
    │   └── ErrorResponse.java
    ├── store/
    │   └── DataStore.java
    ├── resource/
    │   ├── DiscoveryResource.java
    │   ├── RoomResource.java
    │   ├── SensorResource.java
    │   └── SensorReadingResource.java
    ├── exception/
    │   ├── ResourceNotFoundException.java
    │   ├── ResourceNotFoundMapper.java
    │   ├── RoomHasSensorsException.java
    │   ├── RoomHasSensorsMapper.java
    │   ├── InvalidReferenceException.java
    │   ├── InvalidReferenceMapper.java
    │   ├── SensorUnavailableException.java
    │   ├── SensorUnavailableMapper.java
    │   └── GlobalExceptionMapper.java
    └── filter/
        └── LoggingFilter.java
```
