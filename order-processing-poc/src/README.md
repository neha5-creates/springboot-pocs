# Order Processing System

## Overview

Asynchronous Order Processing System using Spring Boot, PostgreSQL and Amazon SQS.

## Technologies

- Java 21
- Spring Boot
- PostgreSQL
- Spring Data JPA
- Hibernate
- AWS SDK v2
- Amazon SQS
- Dead Letter Queue (DLQ)
- Swagger

## Features

- Create Order
- Order Status Tracking
- Producer Pattern
- Consumer Pattern
- Async Processing
- Queue Based Architecture

## Order Lifecycle

CREATED → QUEUED → PROCESSING → COMPLETED

## Run

```bash
mvn spring-boot:run
```
