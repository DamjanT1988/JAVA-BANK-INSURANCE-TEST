# GOFIDO Insurance API

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
  - [Create Offer](#create-offer)
  - [Update Offer](#update-offer)
  - [Accept Offer](#accept-offer)
  - [Conversion Statistics](#conversion-statistics)
- [Database](#database)
- [Testing](#testing)
- [Project Structure](#project-structure)

## Overview
GOFIDO is a Spring Boot application for managing mortgage payment protection insurance offers. It supports full CRUD operations for offers, automatic premium calculations, conversion statistics, and GDPR‑compliant data handling.

## Features
- **Offer Management**: Create, update, and accept offers.
- **Premium Calculation**: Automatically calculates premium as 3.8% of total loan amount.
- **Validity**: Configurable offer validity period (default 30 days).
- **Statistics**: Provides conversion metrics via a REST endpoint.
- **GDPR**: Scheduled task anonymizes expired offers.
- **In-Memory Database**: H2 database for rapid development and testing.
- **Testing**: Comprehensive unit and integration tests.

## Technology Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- H2 Database
- Lombok
- Maven
- JUnit & Mockito

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, VS Code, etc.)

## Setup and Installation
```bash
git clone <repository-url>
cd gofido
mvn clean package
mvn spring-boot:run
```
Application runs at `http://localhost:8080`.

## Configuration
Edit `src/main/resources/application.properties`:
```properties
server.port=8080
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update

# Offer validity (days)
offer.valid-days=30
```

## API Endpoints

### Create Offer
**POST** `/offer`
- **Request**:
  ```json
  {
    "personnummer": "19800101-1234",
    "lån": [
      {"bank":"Handelsbanken","belopp":1200000},
      {"bank":"SEB","belopp":800000}
    ],
    "manadskostnad": 9500
  }
  ```
- **Response**: JSON with offer details, including `premie`, `giltigTill`, etc.

### Update Offer
**PUT** `/offer/{id}`
- **Request**: Same payload as create.
- **Response**: Updated offer JSON.

### Accept Offer
**POST** `/offer/{id}/accept`
- **Response**: Offer status `TECKNAD` and `accepteradVid` timestamp.

### Conversion Statistics
**GET** `/stats/conversion`
- **Response**:
  ```json
  {
    "totalaOfferter": 120,
    "accepteradeOfferter": 36,
    "konverteringsgrad": 30.0,
    "tidsintervall": "30 dagar"
  }
  ```

## Database
- Access H2 console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`

## Testing
```bash
mvn test
```
### Unit Tests

We use JUnit 5 and Mockito to verify core business logic in `OfferService`. Key test cases include:

- **`calculatesPremiumAndValidityOnCreate`**  
  Verifies that creating an offer:
  - Sums the loan amounts correctly (e.g. 1 200 000 + 800 000 = 2 000 000)  
  - Calculates the premium as 3.8% of the total (76 000)  
  - Sets `status` to `SKAPAD` and populates `skapad` and `giltigTill` with a 30-day validity window  

- **`acceptOfferBeforeExpirySucceeds`**  
  Confirms that accepting an offer before its expiry date:
  - Changes `status` to `TECKNAD`  
  - Sets `accepteradVid` timestamp  
  - Persists the updated entity  

- **`acceptOfferAfterExpiryThrowsException`**  
  Ensures that attempting to accept an expired offer throws `OfferExpiredException`.

- **`acceptOfferNotFoundThrowsException`**  
  Ensures that accepting a non-existent offer throws `OfferNotFoundException`.

- **`updateOfferRecalculatesFields`**  
  Tests that updating an offer:
  - Recalculates `forsakratBelopp` and `premie` based on new loan data  
  - Updates the `personnummer` field  
  - Maintains `status = SKAPAD`  

- **`updateOfferAlreadyAcceptedThrowsException`**  
  Verifies that updating an offer with status `TECKNAD` throws `OfferAlreadyAcceptedException`.

- **`updateOfferAfterExpiryThrowsException`**  
  Verifies that updating an expired offer throws `OfferExpiredException`.

#### Running Unit Tests

```bash
mvn -Dtest=OfferServiceTest test
```

## Project Structure
```
src/
 ├─ main/
 │   ├─ java/com/example/gofido/
 │   │    ├─ config/          # Scheduling & application config
 │   │    ├─ controller/      # REST controllers
 │   │    ├─ domain/          # JPA entities & enums
 │   │    ├─ dto/             # Data transfer objects
 │   │    ├─ exception/       # Custom exceptions & handlers
 │   │    ├─ repository/      # Spring Data JPA repositories
 │   │    └─ service/         # Business logic
 │   └─ resources/            # application.properties
 └─ test/                     # Unit & integration tests
```
