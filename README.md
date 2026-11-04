# Citizen_Registration

## Team members

| No. | Full name | Student number |
|---|---|---|
| 1 | PROUD CHISODA | 2410083 |
| 2 | COMFORT PENYENGWE | 2410098 |
| 3 | LAMLA MWANZA | 2410205 |
| 4 | GIDEON NAMUYA | 2410149 |
| 5 | LUBUTO MATAMBO | 2410171 |

## The problem
Paper-based citizen registration (birth records and National Registration Card applications) is slow to search and easy to lose. This project is a **classroom simulation** that shows how such records could be captured, linked and tracked in a web system. It uses **synthetic data only**, issues no real documents and is not connected to any government system.

## What it does
- Register a birth record (name, date of birth, place of birth).
- Create a simulated NRC application linked to an existing birth record.
- Change an application's status: PENDING, APPROVED or REJECTED.
- List, view and delete records. Data is saved to `data/records.txt` and survives a restart.

## How to run
Requirements: JDK 17 or later and Maven.

```text
mvn compile exec:java
```

Then open **http://localhost:7070** in a browser. (Stop the server with Ctrl+C.)

## REST API (Javalin)
| Method | Path | Purpose |
|---|---|---|
| GET | `/api/records` | List all records |
| GET | `/api/records/{id}` | Get one record (404 if missing) |
| POST | `/api/birth-records` | Create a birth record |
| POST | `/api/applications` | Create an NRC application |
| PUT | `/api/applications/{id}/status` | Update application status |
| DELETE | `/api/records/{id}` | Delete a record (404 if missing) |

## Classes and OOP concepts
| Class | Role |
|---|---|
| `model.CitizenRecord` | Abstract base class: private final fields, validation, abstract `getRecordType()` and `getDetails()` |
| `model.BirthRecord` | Extends `CitizenRecord`; adds place of birth |
| `model.NRCApplication` | Extends `CitizenRecord`; adds linked birth ID and a validated status |
| `repository.RecordRepository` | Interface describing storage |
| `repository.FileRecordRepository` | Persistent implementation (Map + text file) |
| `repository.InMemoryRecordRepository` | Non-persistent implementation (same interface) |
| `service.RecordService` | Business rules; throws `RecordNotFoundException` |
| `exception.RecordNotFoundException` | Custom exception, mapped to HTTP 404 |
| `App` | Javalin server, endpoints and error handlers |
| `resources/public/index.html` | HTML + JavaScript front end |

- **Encapsulation:** all fields are private; `NRCApplication.status` can only change through `setStatus`, which validates it.
- **Inheritance:** `BirthRecord` and `NRCApplication` extend `CitizenRecord`.
- **Interface:** `RecordRepository` has two implementations.
- **Polymorphism:** the API and UI call `getRecordType()` and `getDetails()` on a `CitizenRecord`, and each subclass answers differently.
- **Custom exception:** `RecordNotFoundException` is thrown by `RecordService` and handled in `App`.
- **Collections:** `LinkedHashMap<String, CitizenRecord>` and `List<CitizenRecord>`.
