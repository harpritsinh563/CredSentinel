# 📌 CredSentinel

## 🚀 Overview
The **CredSentinel** is a backend-intensive fintech system designed to process **high-volume micro-loan requests (₹500–₹5,000)** and detect fraudulent patterns **in near real time**.

This project simulates architectures used by modern Indian fintech lenders such as **KreditBee, Slice, Paytm Postpaid, and ZestMoney**, showcasing production-grade concepts like **event-driven microservices, Kafka-based streaming, Redis caching, and cross-language communication using Go + gRPC**.


---

## 🧠 Key Features
- Event-driven microservices using **Apache Kafka**
- High-throughput backend services built with **Java Spring Boot**
- **Go-based fraud detection engine** for performance-critical workloads
- **gRPC** for efficient inter-service communication
- **Redis** for distributed caching, rate limiting, and in-memory graph modeling
- **PostgreSQL** for durable loan request storage
- Production-inspired architectural patterns (async processing, service isolation)

---

## 🏗 System Architecture (High Level)

```text
Client
  ↓
Loan Request Management Service (Spring Boot)
  ↓   (Kafka Event)
loan_request_created
  ↓
Risk Assessment Service (Spring Boot)
  ↓   (gRPC)
Fraud / Anomaly Detection Service (Go)
  ↓
loan_risk_score_generated (Kafka Event)
