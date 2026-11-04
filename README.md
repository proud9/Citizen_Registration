# Synthetic Citizen Record Manager

## Team members

| No. | Full name | Student number |
|---|---|---|
| 1 | PROUD CHISODA | 2410083 |
| 2 | COMFORT PENYENGWE | 2410098 |
| 3 | LAMLA MWANZA | 2410205 |
| 4 | GIDEON NAMUYA | 2410149 |
| 5 | LUBUTO MATAMBO | 2410171 |

## The problem

Paper-based citizen registration records can be slow to search and easy to lose. This project is a **classroom simulation** for capturing and managing synthetic birth records and NRC applications. It uses fictional data only, issues no real documents, and is not connected to any government system.

## What it does

- Create, list, view, update, and delete birth records.
- Create, list, view, update, and delete NRC applications.
- Assign generated reference IDs and timestamps to new records.
- Validate required fields, dates of birth, and application statuses.
- Store records in `data/records.json` so they survive a restart.

NRC applications currently store applicant details and an application status; they are not linked to birth records.

## How to run

Requirements: JDK 17 or later and Maven.

```text
mvn compile exec:java
```

Then open **http://localhost:7070** in a browser. Stop the server with Ctrl+C. To use a different port, provide it as the first application argument.

Run the tests with:

```text
mvn test
```

## REST API

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/records` | List all records |
| GET | `/api/records/{id}` | Get one record; returns 404 if missing |
| DELETE | `/api/records/{id}` | Delete one record; returns 404 if missing |
| POST | `/api/birth-records` | Create a birth record |
| PUT | `/api/birth-records/{id}` | Replace/update a birth record |
| DELETE | `/api/birth-records/{id}` | Delete a birth record |
| POST | `/api/nrc-applications` | Create an NRC application |
| PUT | `/api/nrc-applications/{id}` | Replace/update an NRC application |
| DELETE | `/api/nrc-applications/{id}` | Delete an NRC application |

Application statuses are `PENDING`, `PROCESSING`, `COMPLETED`, and `REJECTED`. Updates use the full record body, not a status-only endpoint. The web page supports creating, listing, and deleting records.

## Classes and OOP concepts

| Class | Responsibility |
|---|---|
| `Main` | Creates the JSON repository, service, controller, and Javalin server |
| `model.CitizenRecord` | Abstract base class for common citizen record fields and record type |
| `model.BirthRecord` | Birth record subtype, including place of birth and parent name |
| `model.NRCApplication` | NRC application subtype, including application status |
| `repository.RecordRepository` | Interface for listing, finding, saving, and deleting records |
| `repository.JsonRecordRepository` | Persists records as JSON and restores their concrete record types |
| `service.RecordService` | Validates and manages records; throws `RecordNotFoundException` when a requested record is missing |
| `exception.RecordNotFoundException` | Custom exception for missing records |
| `web.RecordController` | Registers REST endpoints and maps exceptions to HTTP responses |
| `src/main/resources/public/` | Static HTML, JavaScript, and CSS for the web front end |

- **Encapsulation:** model fields are private and accessed through getters and setters. The service validates record data before saving it.
- **Inheritance:** `BirthRecord` and `NRCApplication` extend `CitizenRecord`.
- **Interface:** `JsonRecordRepository` implements `RecordRepository`.
- **Polymorphism:** both record subtypes override `getRecordType()`, and the repository serializes records through the common `CitizenRecord` type.
- **Custom exception:** `RecordService` throws `RecordNotFoundException`, which `RecordController` maps to HTTP 404.
- **Collections:** the repository uses lists to work with sets of records.

## Demo data

The front end includes an option to add fictional demo records. Use synthetic data only; this project is not intended for real citizen information.
