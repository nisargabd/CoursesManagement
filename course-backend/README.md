Course Management Backend – README
✅ Overview

This is a Spring Boot backend for managing courses, boards, mediums, classes, and subjects.
It exposes REST APIs consumed by your Angular frontend and stores data in PostgreSQL.
The project can run normally or fully containerized using Docker Compose.

✅ Tech Stack

Java 17

Spring Boot 3.x

Spring Data JPA (Hibernate)

PostgreSQL

Lombok

Docker & Docker Compose

Maven

✅ Architecture Flow
Angular Frontend  →  Spring Boot Backend  →  PostgreSQL Database

How requests flow:

Angular calls REST API

Controller receives request

Service layer handles logic

Repository (JPA) interacts with DB

Response returned as JSON

✅ Key Backend Concepts
✔ Entity Relationships

@OneToMany, @ManyToOne

@JsonManagedReference, @JsonBackReference → prevents infinite recursion

@CreationTimestamp, @UpdateTimestamp → auto timestamps

@Convert(StringListConverter.class) → store List<String> fields
