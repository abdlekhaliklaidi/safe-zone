# 01E-COM - E-Commerce Microservices Platform

01E-COM is a modern e-commerce platform built using a microservices architecture. The application provides secure authentication, product management, image uploading, and API gateway routing while demonstrating scalable backend development using Spring Boot and Angular.

---

# Architecture

```
                        +------------------+
                        |   Angular 19     |
                        |      Client      |
                        +--------+---------+
                                 |
                                 |
                          HTTPS (SSL)
                                 |
                          Nginx Reverse Proxy
                                 |
                          HTTPS (SSL)
                                 |
                        Spring Cloud Gateway
                                 |
        --------------------------------------------------
        |                     |                          |
        |                     |                          |
  User Service         Product Service          Media Service
        |                     |                          |
     MongoDB/redis             MongoDB                 File Storage
        |               
      Kafka  <----------------------->  Product Service
```

---
# 🚀 CI/CD Pipeline Stages

1. Checkout

Jenkins downloads the latest source code from Git.

2. Backend Tests

Backend services are tested in parallel:

User Service
Product Service
Gateway Service
Media Service

Technologies:

Java
Spring Boot
Maven

Example:

./mvnw test

3. Frontend Tests

Frontend tests are executed using Angular and Chrome Headless.

Environment:

Node.js 20
Chromium
Angular CLI

Command:

npm ci

CI=true npx ng test \
 --watch=false \
 --browsers=ChromeHeadlessCI

# 🐳 Docker Build

The pipeline generates environment variables and builds all Docker images:

docker compose --env-file .env build
💾 Backup Before Deployment

Before every deployment Jenkins creates a backup containing:

Environment file .env
Running Docker images
MongoDB database dump


MongoDB backup:

mongodump --archive --gzip
🚀 Deployment

The deployment process:

docker compose down

docker compose up -d

After deployment Jenkins verifies running containers.

# 🔄 Automatic Rollback

If deployment fails Jenkins automatically:

Stops the failed deployment
Loads previous Docker images
Restores MongoDB backup
Restores environment variables
Starts the previous version

Rollback example:

docker load -i backup/image.tar

mongorestore \
 --archive=mongo_backup.archive \
 --gzip \
 --drop


# Features

- User Registration
- User Login using JWT Authentication
- Role-based Authorization
- Product Management
- Product Image Upload
- User Profile Management
- Secure API Gateway
- HTTPS Support
- Kafka Event Communication
- Dockerized Services
- Responsive Angular Frontend

---

# Technologies

## Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Cloud Gateway
- Spring Data MongoDB
- Apache Kafka
- Maven

## Frontend

- Angular 19
- TypeScript
- Tailwind CSS
- RxJS

## Database

- MongoDB

## DevOps

- Docker
- Docker Compose
- Nginx
- SSL Certificates

---

# Project Structure

```
01E-COM/

│
├── gateway-service/
│
├── user-service/
│
├── product-service/
│
├── media-service/
│
├── client/
│
├── docker-compose.yml
|
|__ Dockerfile.jenkins
|
|__ Jenkinsfile
│
└── README.md
```

---

# Microservices

## Gateway Service

Responsibilities

- Single entry point
- JWT validation
- Request routing
- HTTPS termination
- CORS configuration

Routes

```
api/auth/**
api/products/**
api/media/**
api/uploads/**
```

---

## User Service

Responsibilities

- Registration
- Login
- JWT generation
- User Profile
- Kafka Producer

Database

MongoDB

redis

---

## Product Service

Responsibilities

- CRUD Products
- Product Search
- Product Images
- Kafka Consumer

Database

MongoDB

---

## Media Service

Responsibilities

- Upload Images
- Store Files
- Serve Uploaded Files

---

# Authentication

JWT is generated after successful login.

The Angular application stores the JWT and attaches it to every authenticated request.

Gateway validates every incoming token before forwarding requests to internal services.

---

# Kafka Communication

Current Event

```
UserDeletedEvent
```

Flow

```
User Service
      |
      | Publish Event
      |
    Kafka
      |
      | Consume Event
      |
Product Service
```

This allows services to communicate asynchronously without direct dependencies.

---
# Cache 
- redis
---
# Security

- JWT Authentication
- HTTPS
- Spring Security
- Gateway Authorization
- CORS Protection

---

# Image Upload Flow

```
Angular

    |
nginx
    |
POST api/media/upload

    |

Gateway

    |

Media Service

    |

uploads/

    |

Image URL

    |

Angular displays image
```

---

# Running the Project

## Clone

```bash
git clone https://github.com/louhabali/buy01.git

cd 01E-COM
```

---

## Start Docker

```bash
docker compose up --build
```

---

## Frontend

```bash
cd frontend

npm install

ng serve --ssl --proxy-config proxy.conf.json
```

---

# Services

| Service | Port |
|----------|------|
| Angular/nginx | 8433 |
| Gateway | 8089 |
| User Service | 8081 |
| Product Service | 8082 |
| Media Service | 8083 |
| MongoDB | 27017 |
| Kafka | 9092 |

---

# API Overview

## Authentication

```
POST api/auth/register

POST api/auth/login

GET api/auth/profile
```

---

## Products

```
GET api/products

GET api/products/{id}

POST api/products

PUT api/products/{id}

DELETE api/products/{id}
```

---

## Media

```
POST api/media/upload

GET api/uploads/{filename}
```

---

# Folder Description

## frontend

Angular application.

## gateway-service

Spring Cloud Gateway responsible for routing requests.

## user-service

Authentication and user management.

## product-service

Handles products and consumes Kafka events.

## media-service

Stores uploaded images.

---

# Future Improvements

- Order Service
- Payment Service
- Shopping Cart
- Wishlist
- Reviews
- Search Service
- Email Notifications
- Kubernetes Deployment
- CI/CD Pipeline
- Monitoring with Prometheus & Grafana

---

# Author

Laidi Abdelkhalik

---

# License

This project is developed for educational purposes and demonstrates a complete microservices-based e-commerce platform using Spring Boot, Angular, Kafka, MongoDB, Docker, Nginx, and Spring Cloud Gateway.