# Inventory Management System

> **Note**: This project was created largely with AI assistance as part of a course organized by Brave Courses.

## Overview

A lightweight inventory management system for small to medium organizations. **Primary business function**: automated low stock alerts with CSV export to prevent stockouts.

**Tech Stack**: Spring Boot 3.5.7 (Kotlin) • Thymeleaf • PostgreSQL • Spring Security • Bootstrap 5 • Gradle 8.14.3

## Quick Start

### Prerequisites
- Java 21+
- PostgreSQL 12+
- Gradle 8.14.3 (or use wrapper)

### Database Setup

```sql
CREATE DATABASE inventory;
CREATE USER inventory_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE inventory TO inventory_user;
```

### Environment Variables

```bash
export INVENTORY_DB_USERNAME=inventory_user
export INVENTORY_DB_PASSWORD=your_password
```

### Run Application

```bash
./gradlew bootRun
```

Access at `http://localhost:8321`

### Run Tests

```bash
./gradlew test
```

## Key Features

1. **Product Management** - CRUD operations with soft delete, unique SKU validation
2. **Stock Arrivals** - Register incoming shipments with auto-quantity update
3. **Stock Corrections** - Manual adjustments with mandatory reason (audit compliance)
4. **Low Stock Alerts** - Threshold-based reports with CSV export (primary function)
5. **History & Audit** - Complete trail of all stock movements with timestamps and users
6. **Authentication** - Spring Security with BCrypt, session management

## Critical Business Rules

- **Quantity field**: Never modify directly - only through arrivals or corrections
- **Soft delete**: Products marked as `deleted=true` to preserve history integrity
- **Audit trail**: All operations record `createdBy` (username) and `ZonedDateTime` timestamp
- **Corrections**: Mandatory reason field (max 500 chars) for compliance

## Project Structure

```
src/main/kotlin/ai/brave/inventory/
├── domain/           # Domain-driven design packages
│   ├── product/      # Product CRUD + service
│   ├── arrival/      # Stock arrivals
│   ├── correction/   # Stock corrections
│   ├── alert/        # Low stock alerts (primary function)
│   └── history/      # Unified audit view
├── security/         # Spring Security config + User model
├── exception/        # GlobalExceptionHandler (@ControllerAdvice)
└── health/           # /health endpoint for CI/CD

src/main/resources/
├── templates/        # Thymeleaf views
│   └── fragments/    # Reusable components (nav, links, scripts)
└── static/           # favicon.svg, favicon.ico
```

## Main Endpoints

- `/products` - Product list and CRUD
- `/arrivals/new/{id}` - Register arrival
- `/corrections/new/{id}` - Register correction
- `/history/{id}` - Product history (arrivals + corrections)
- `/alerts/low-stock` - Generate low stock report
- `/alerts/low-stock/export?threshold=X` - Export CSV
- `/login`, `/profile`, `/logout` - Authentication
- `/health` - Health check (returns `{"status": "OK"}`)

## CI/CD Pipeline

GitHub Actions workflow (`.github/workflows/ci-cd.yml`):
1. **Test** - Run JUnit tests with PostgreSQL service
2. **Build** - Create bootJar
3. **Deploy** - Upload to server via SSH, restart app
4. **Health Check** - Verify deployment

Required secrets: `SERVER_HOST`, `SERVER_PORT`, `SERVER_USER`, `SERVER_PASSWORD`, `APP_URL`

## Documentation

Detailed documentation in `.ai/` folder:
- `prd.md` - Product Requirements Document with user stories
- `api-plan.md` - Complete API endpoint documentation
- `db-plan.md` - Database schema and relationships
- `tech-stack.md` - Technology stack details
- `ui-plan.md` - UI architecture and user flows

Development guidelines in `.cursor/rules/`:
- `shared.mdc` - General coding practices and business rules
- `backend.mdc` - Spring Boot, JPA, and security guidelines
- `frontend.mdc` - Thymeleaf and Bootstrap best practices

## Security Notes

- All routes except `/login`, `/error`, `/health`, and static assets require authentication
- CSRF protection enabled for all POST/PUT/DELETE operations
- Passwords hashed with BCrypt
- Session-based authentication
- Environment variables for database credentials (never hardcoded)

## License

Educational project created for Brave Courses AI development training.

---

**Built with ❤️ and AI assistance**
