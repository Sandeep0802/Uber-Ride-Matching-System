# 🚗 Uber Ride Matching System

A **microservices-based ride matching backend** built with Spring Boot, Redis, Kafka, and MySQL — simulating how Uber matches riders with the nearest available driver in real time.

---

## 📦 Project Structure

```
Uber-Ride-Matching-System/
├── location-service/     → Tracks driver locations (Redis)
├── ride-service/         → Manages ride lifecycle (MySQL + Kafka)
├── matching-service/     → Matches drivers to ride requests (Kafka)
└── docker-compose.yml    → Spins up Redis, MySQL, Kafka, Zookeeper
```

---

## 🛠️ Tech Stack

| Technology        | Role                                      |
|-------------------|-------------------------------------------|
| Spring Boot       | Microservices framework                   |
| Redis Geospatial  | Real-time driver location storage         |
| Apache Kafka      | Async event streaming between services    |
| MySQL             | Persistent ride data storage              |
| Docker Compose    | Local infrastructure setup                |

---

## 🧩 Services Overview

| Service             | Port   | Responsibility                                          |
|---------------------|--------|---------------------------------------------------------|
| `location-service`  | `8082` | Stores & queries driver GPS coordinates via Redis       |
| `ride-service`      | `8083` | Creates rides, manages state, produces/consumes events  |
| `matching-service`  | `8084` | Listens for ride requests, scores & assigns best driver |

---

## 🔄 Architecture Flow

```
Driver Phone  →  Location Service  →  Redis (GEOADD)
                                         ↑ stores GPS coords

Rider App  →  Ride Service  →  Kafka Topic: ride.requested
                                      ↓
                          Matching Service (Kafka Consumer)
                                      ↓
                          Location Service (find nearby drivers)
                          [Redis GEORADIUS query within 5km]
                                      ↓
                          Driver Scoring Algorithm
                          [score = distance(85%) + rating(15%)]
                                      ↓
                          Kafka Topic: ride.matched
                                      ↓
                          Ride Service (updates ride → ACCEPTED)
```

---

## 🔢 Ride State Machine

A ride goes through these states from creation to completion:

```
REQUESTED → MATCHING → ACCEPTED → STARTED → COMPLETED
```

| State       | Trigger                                           |
|-------------|---------------------------------------------------|
| `REQUESTED` | Rider submits ride request                        |
| `MATCHING`  | Ride service publishes to Kafka                   |
| `ACCEPTED`  | Matching service assigns a driver                 |
| `STARTED`   | Driver starts the ride (PUT /start)               |
| `COMPLETED` | Driver completes the ride (PUT /complete)         |

---

## 🧠 Driver Scoring Algorithm

When multiple drivers are nearby, the **best driver** is selected using a weighted score:

```
score = (1 / (1 + distanceKm)) × 0.85   +   (rating / 5.0) × 0.15
```

- **85% weight** → Distance (closer = better)
- **15% weight** → Driver rating (higher = better)

The driver with the highest score wins the ride.

---

## 🚀 How To Run

### Prerequisites
- Java 17+
- Maven
- Docker & Docker Compose

---

### Step 1 — Start Infrastructure

```bash
docker-compose up -d
```

This starts **Redis**, **MySQL**, **Zookeeper**, and **Kafka**.

> ⏳ Wait **30 seconds** for Kafka to fully initialize before starting any service.

---

### Step 2 — Start Location Service

```bash
cd location-service
mvn spring-boot:run
```

Runs on → `http://localhost:8082`

---

### Step 3 — Start Ride Service

```bash
cd ride-service
mvn spring-boot:run
```

Runs on → `http://localhost:8083`

---

### Step 4 — Start Matching Service

```bash
cd matching-service
mvn spring-boot:run
```

Runs on → `http://localhost:8084`

---

## 🧪 End-to-End Testing

### Step 1 — Register Driver Locations

Call the Location Service to place drivers on the map:

```http
POST http://localhost:8082/api/v1/locations/drivers/update
Content-Type: application/json

{
    "driverId": "driver:1",
    "latitude": 12.9716,
    "longitude": 77.5946
}
```

Repeat for `driver:2` and `driver:3` with slightly different coordinates to simulate nearby drivers.

---

### Step 2 — Request a Ride

```http
POST http://localhost:8083/api/v1/rides/request
Content-Type: application/json

{
    "riderId": "rider:1",
    "pickupLatitude": 12.9716,
    "pickupLongitude": 77.5946,
    "pickupAddress": "MG Road, Bangalore",
    "dropLatitude": 12.9352,
    "dropLongitude": 77.6245,
    "dropAddress": "Koramangala, Bangalore"
}
```

This publishes a `ride.requested` event to Kafka → Matching Service picks it up → assigns a driver → publishes `ride.matched` → Ride Service updates the ride status to `ACCEPTED`.

---

### Step 3 — Check Ride Status

```http
GET http://localhost:8083/api/v1/rides/{rideId}
```

You should see `driverId` assigned and `status = ACCEPTED`.

---

### Step 4 — Start the Ride

```http
PUT http://localhost:8083/api/v1/rides/{rideId}/start
```

Status changes to `STARTED`.

---

### Step 5 — Complete the Ride

```http
PUT http://localhost:8083/api/v1/rides/{rideId}/complete
```

Status changes to `COMPLETED`.

---

### Step 6 — View Rider History

```http
GET http://localhost:8083/api/v1/rides/rider/rider:1
```

Returns all rides for the given rider.

---

## 🔍 Verify in Redis CLI

```bash
docker exec -it redis-geo redis-cli

# List all stored drivers
ZRANGE drivers:location 0 -1

# Get position of a specific driver
GEOPOS drivers:location "driver:1"

# Distance between two drivers
GEODIST drivers:location "driver:1" "driver:2" km
```

---

## 📚 Key Concepts Covered

| Concept                         | Where Used                                         |
|---------------------------------|----------------------------------------------------|
| Redis Geospatial (GEOADD, GEORADIUS) | Location Service — storing & querying driver GPS |
| Kafka Producer                  | Ride Service — publishes `ride.requested`          |
| Kafka Consumer                  | Matching Service — listens to `ride.requested`     |
| Kafka Producer (again)          | Matching Service — publishes `ride.matched`        |
| Kafka Consumer (again)          | Ride Service — listens to `ride.matched`           |
| Feign Client (inter-service REST) | Matching Service calls Location Service via declarative HTTP client |
| Ride State Machine              | Ride Service — manages status transitions          |
| Driver Scoring Algorithm        | Matching Service — distance + rating weighted score |
| Docker Compose                  | Infrastructure: Redis, MySQL, Kafka, Zookeeper     |

---

## 🌐 Kafka Topics

| Topic           | Producer         | Consumer          | Purpose                        |
|-----------------|------------------|-------------------|--------------------------------|
| `ride.requested`| Ride Service     | Matching Service  | Notify that a ride was created |
| `ride.matched`  | Matching Service | Ride Service      | Notify that a driver was found |

---

## ⚙️ Infrastructure Ports

| Service     | Port   |
|-------------|--------|
| Redis       | `6379` |
| MySQL       | `3306` |
| Zookeeper   | `2181` |
| Kafka       | `9092` |

---

## 👤 Author

Built by **Sandeep** — a hands-on project to learn event-driven microservices with real-world patterns used in ride-sharing platforms.
