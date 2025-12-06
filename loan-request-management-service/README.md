# Loan Request Service

## Overview

The **Loan Request Service** is a backend microservice responsible for managing the lifecycle of loan requests in a fintech system.  
It handles loan creation, retrieval, status tracking, and cleanup, while publishing domain events for asynchronous downstream processing.

The service is designed to be **stateless**, **event-driven**, and **extensible**, making it suitable for high-throughput systems and future integrations (e.g., risk engines, bureau checks, fraud detection).

---

## Core Responsibilities

### ✅ Create Loan Request
- Validate input payload
- Persist loan request data
- Publish `loan_request_created` event to Kafka for async processing

### ✅ Fetch Loan Requests
- User dashboard views
- Admin views
- Debugging & support tooling

### ✅ Loan Status Tracking
- Retrieve lifecycle status using:
    - `Loan_Status_History`
    - `Risk_Score`

### ✅ Cleanup
- Remove expired or stale loan requests

---

## Architecture Highlights

- **API-first design**
- **Event-driven** using Kafka
- **Async downstream processing**
- **Clear separation of responsibilities**
- **Versioned REST APIs**

---

## Technology Stack (Suggested)

| Component | Tech                  |
|--------|-----------------------|
| Language | Java          |
| Framework | Spring Boot |
| Messaging | Kafka                 |
| Database | PostgreSQL / MySQL    |
| Cache (Optional) | Redis                 |
| Communication | REST                  |
| Observability | TBD                   |
| Deployment | TBD                   |

---

## API Versioning Strategy

All APIs are prefixed with a version number to support backward compatibility.

