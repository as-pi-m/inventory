# Inventory Management System

> **Note**: This project was created largely with AI assistance as part of a course organized by Brave Courses. It serves as a practical example of AI-assisted development and modern web application architecture.

## Overview

A lightweight, secure inventory management system designed for small to medium-sized organizations. The application enables product definition, stock tracking, goods movement registration (arrivals, corrections), and basic reporting capabilities.

## Technology Stack

- **Backend**: Spring Boot 3.5.7 (Kotlin)
- **Frontend**: Thymeleaf templates with Bootstrap 5
- **Database**: PostgreSQL
- **Security**: Spring Security with form-based authentication
- **Testing**: JUnit 5, MockMvc
- **Build Tool**: Gradle 8.14.3

## Features

### 1. Product Management (US-INV-001)
- **Add new products** with comprehensive data:
  - Name, SKU (unique identifier)
  - Description
  - Unit of measure (pcs, kg, liters, etc.)
  - Minimum order level
  - Unit price
  - Initial quantity
- **Edit products** - all fields except quantity (managed through arrivals/corrections)
- **View product details** - complete product information
- **Soft delete** - products are marked as deleted, not removed from database
- **List all products** - searchable and filterable product list

### 2. User Authentication & Security (US-INV-002)
- **User registration** with username and password
- **Secure login** with Spring Security
- **Password encryption** using BCrypt
- **Session management**
- **User profile** - view current user information
- **Logout functionality**
- **Protected routes** - all operations require authentication

### 3. Stock Arrivals (US-INV-003)
- **Register product arrivals** with:
  - Product selection
  - Quantity received
  - Source/supplier name
  - Automatic timestamp and user tracking
- **Automatic stock update** - quantity increases upon arrival
- **Arrival history** - complete audit trail of all arrivals
- **View arrivals by product** - filtered history per product

### 4. Stock Corrections (US-INV-004)
- **Manual stock adjustments** with mandatory:
  - Quantity change (positive or negative)
  - Reason for correction
  - Automatic user tracking
- **Validation** - corrections require valid reason
- **Audit trail** - all corrections logged with timestamp and author
- **Unified history view** - arrivals and corrections in one place
- **Error handling** - comprehensive validation with ControllerAdvice

### 5. Low Stock Alerts (US-INV-005)
- **Dynamic threshold setting** - configurable minimum quantity
- **Low stock report** - list of products below threshold
- **CSV export** with:
  - Product SKU, name
  - Current quantity
  - Minimum order level
  - Unit of measure
  - Calculated deficit
  - Timestamp in filename
- **Special character handling** - proper CSV escaping
- **Visual indicators** - color-coded badges for critical levels
- **Quick actions** - direct links to product details and history

### 6. History & Audit Trail
- **Unified history view** showing:
  - Product arrivals (green, positive quantities)
  - Stock corrections (green for positive, red for negative)
  - Date and time of each operation
  - User who performed the operation
  - Source (for arrivals) or Reason (for corrections)
- **Chronological ordering** - most recent operations first
- **Complete audit** - immutable record of all stock changes

### 7. Error Handling & Validation
- **Global Exception Handler** with custom error pages:
  - 400 - Bad Request
  - 404 - Not Found
  - 500 - Internal Server Error
  - Validation errors
- **Form validation** with user-friendly messages
- **Business rule enforcement**:
  - Unique SKU validation
  - Positive quantity requirements
  - Mandatory fields validation

## User Interface

### Navigation
- **Products** - product list and management
- **Low Stock Alert** - stock monitoring and reports
- **My Profile** - user information
- **Logout** - secure session termination

### Key Pages
1. **Product List** - overview of all products with actions
2. **Product Form** - add/edit products
3. **Product View** - detailed product information
4. **History** - unified view of arrivals and corrections
5. **Arrival Form** - register new stock arrivals
6. **Correction Form** - adjust stock levels
7. **Low Stock Alert** - threshold-based reporting with CSV export
8. **Profile** - user account information

## Data Model

### Product
- ID (auto-generated)
- Name
- SKU (unique)
- Description
- Unit of measure
- Minimum order level
- Unit price
- Current quantity
- Deleted flag (soft delete)
- Created by (username)

### Product Arrival
- ID (auto-generated)
- Product (foreign key)
- Quantity
- Source/supplier
- Arrival date (timestamp)
- Created by (username)

### Stock Correction
- ID (auto-generated)
- Product (foreign key)
- Quantity (can be negative)
- Reason
- Correction date (timestamp)
- Created by (username)

### User
- ID (auto-generated)
- Username (unique)
- Password (encrypted)
- Enabled flag

## Testing

The application includes comprehensive test coverage:

- **ProductControllerTest** - product CRUD operations
- **ProductArrivalControllerTest** - arrival registration and validation
- **StockCorrectionControllerTest** - correction handling and validation
- **LowStockAlertControllerTest** - reporting and CSV export
- **Integration tests** with real database transactions
- **Security tests** - authentication and authorization

### Running Tests

```bash
./gradlew test
```

### Test Coverage
- Unit tests for controllers
- Integration tests with MockMvc
- Transaction rollback for test isolation
- Mock authentication with `@WithMockUser`

## Installation & Setup

### Prerequisites
- Java 21 or higher
- PostgreSQL 12 or higher
- Gradle 8.14.3 (or use included wrapper)

### Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE inventory;
CREATE USER inventory_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE inventory TO inventory_user;
```

2. Configure database connection in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory
spring.datasource.username=inventory_user
spring.datasource.password=your_password
```

### Build & Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The application will be available at `http://localhost:8321`

### First User

Register the first user through the registration form at `/register`

## Security Considerations

- **Password encryption** - BCrypt with strength 10
- **CSRF protection** - enabled for all state-changing operations
- **Session management** - secure session handling
- **SQL injection prevention** - JPA/Hibernate parameterized queries
- **XSS protection** - Thymeleaf automatic escaping
- **Authentication required** - all routes protected except login/register

## Future Enhancements (Post-MVP)

As outlined in the PRD:
- Multi-location/warehouse support
- Batch/lot number tracking
- Barcode scanning integration
- Advanced forecasting and demand planning
- ERP system integration
- FIFO/LIFO cost valuation
- Multi-currency support
- Mobile application
- REST API for external integrations

## Project Structure

```
src/main/kotlin/ai/brave/inventory/
├── domain/
│   ├── alert/          # Low stock alerts
│   ├── arrival/        # Stock arrivals
│   ├── correction/     # Stock corrections
│   ├── history/        # Unified history view
│   └── product/        # Product management
├── security/           # Authentication & authorization
└── exception/          # Global error handling

src/main/resources/
├── templates/          # Thymeleaf templates
│   ├── alert/
│   ├── arrival/
│   ├── auth/
│   ├── correction/
│   ├── error/
│   ├── fragments/
│   ├── history/
│   └── product/
└── static/            # Static assets

src/test/kotlin/       # Test suite
```

## API Endpoints

### Products
- `GET /products` - List all products
- `GET /products/new` - Product creation form
- `POST /products` - Create product
- `GET /products/{id}` - View product details
- `GET /products/{id}/edit` - Edit product form
- `POST /products/{id}` - Update product
- `POST /products/{id}/delete` - Soft delete product

### Arrivals
- `GET /arrivals/new/{productId}` - Arrival registration form
- `POST /arrivals` - Register arrival
- `GET /arrivals/product/{productId}` - List arrivals for product

### Corrections
- `GET /corrections/new/{productId}` - Correction form
- `POST /corrections` - Register correction

### History
- `GET /history/{productId}` - Unified history view

### Alerts
- `GET /alerts/low-stock` - Low stock alert page
- `POST /alerts/low-stock` - Generate report
- `GET /alerts/low-stock/export` - Export CSV

### Authentication
- `GET /login` - Login form
- `POST /login` - Authenticate
- `GET /register` - Registration form
- `POST /register` - Create account
- `POST /logout` - End session
- `GET /profile` - User profile

## Contributing

This is an educational project. Contributions, issues, and feature requests are welcome for learning purposes.

## License

This project is created for educational purposes as part of Brave Courses AI development training.

## Acknowledgments

- [Brave Courses](https://bravecourses.com) for the AI development course
- Spring Boot and Kotlin communities
- AI assistance in project development

---

**Built with ❤️ and AI assistance**

