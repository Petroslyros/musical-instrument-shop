# Musical Instrument Shop API

A comprehensive REST API for managing a musical instrument e-commerce platform, built with Spring Boot 3 and Spring Security with JWT authentication.

## Features

- **User Authentication & Authorization** - JWT-based authentication with role-based access control
- **Brand Management** - Create, read, update, and delete instrument brands
- **Category Management** - Organize instruments by categories
- **Instrument Management** - Full CRUD operations with search and filtering capabilities
- **Order Management** - Create orders, manage order items, and track order status
- **Pagination & Sorting** - Efficient data retrieval with customizable pagination
- **Input Validation** - Comprehensive validation for all DTOs
- **Error Handling** - Centralized exception handling with meaningful error messages
- **API Documentation** - Interactive Swagger UI for API exploration
- **Audit Logging** - Track creation and modification timestamps for all entities
- **Security** - CORS configuration, CSRF protection, and secure password encoding

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Database**: JPA/Hibernate with relational database support
- **Security**: Spring Security + JWT (JJWT)
- **API Documentation**: Springdoc OpenAPI (Swagger UI)
- **Validation**: Jakarta Bean Validation (Hibernate Validator)
- **Logging**: SLF4J with Logback
- **Build Tool**: Gradle
- **Language**: Java 17+

## Project Structure

```
src/main/java/com/musical_instrument_shop/
├── authentication/          # JWT and authentication services
│   ├── AuthenticationService
│   ├── CustomUserDetailsService
│   └── JwtService
├── controller/              # REST controllers
│   ├── AuthRestController
│   ├── BrandRestController
│   ├── CategoryRestController
│   ├── InstrumentRestController
│   └── OrderRestController
├── core/                    # Core configurations and utilities
│   ├── enums/              # Role, OrderStatus
│   ├── exceptions/         # Custom exception classes
│   ├── filters/            # Pagination and filtering utilities
│   ├── specifications/     # JPA specifications for advanced queries
│   ├── ErrorHandler.java   # Global exception handler
│   ├── MDCLoggingFilter    # Logging context filter
│   ├── OpenApiConfig       # Swagger/OpenAPI configuration
│   └── SecurityConfig      # Spring Security configuration
├── dto/                     # Data Transfer Objects
├── mapper/                  # Entity to DTO mappers
├── model/                   # JPA entities
├── repository/              # Spring Data JPA repositories
├── security/                # Authentication filters and handlers
│   ├── CustomAccessDeniedHandler
│   ├── CustomAuthenticationEntryPoint
│   └── JwtAuthenticationFilter
├── service/                 # Business logic services
└── MusicalInstrumentShopApplication.java

src/main/resources/
├── application.properties   # Application configuration
└── application-dev.properties
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 7.x or higher
- Relational database (MySQL, PostgreSQL, etc.)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/musical-instrument-shop.git
   cd musical-instrument-shop
   ```

2. **Configure the database**
   
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/musical_instrument_shop
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Configure JWT Secret**
   
   Add to `application.properties`:
   ```properties
   app.security.secret-key=your-base64-encoded-secret-key-here
   app.security.jwt-expiration=3600000
   ```

4. **Build the project**
   ```bash
   gradle clean build
   ```

5. **Run the application**
   ```bash
   gradle bootRun
   ```

   The API will be available at `http://localhost:8080`

## API Endpoints

### Authentication
- `POST /api/auth/authenticate` - Login with username and password

### Brands
- `GET /api/brands` - Get all brands (paginated)
- `GET /api/brands/{id}` - Get brand by ID
- `POST /api/brands` - Create a new brand
- `PUT /api/brands/{id}` - Update a brand
- `DELETE /api/brands/{id}` - Delete a brand

### Categories
- `GET /api/categories` - Get all categories (paginated)
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create a new category
- `PUT /api/categories/{id}` - Update a category
- `DELETE /api/categories/{id}` - Delete a category

### Instruments
- `GET /api/instruments` - Get all instruments (paginated)
- `GET /api/instruments/{id}` - Get instrument by ID
- `GET /api/instruments/search?name=keyword` - Search instruments by name
- `GET /api/instruments/category/{categoryId}` - Get instruments by category
- `GET /api/instruments/brand/{brandId}` - Get instruments by brand
- `POST /api/instruments` - Create a new instrument
- `PUT /api/instruments/{id}` - Update an instrument
- `DELETE /api/instruments/{id}` - Delete an instrument

### Orders
- `GET /api/orders` - Get all orders (paginated)
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/user/{userId}` - Get orders by user
- `POST /api/orders` - Create a new order
- `PUT /api/orders/{id}` - Update order status
- `DELETE /api/orders/{id}` - Delete an order

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. 

1. **Login** to get a token:
   ```bash
   POST /api/auth/authenticate
   {
     "username": "user@example.com",
     "password": "password123"
   }
   ```

2. **Use the token** in subsequent requests:
   ```bash
   Authorization: Bearer <your_jwt_token>
   ```

## API Documentation

Access the interactive Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

Or view the OpenAPI specification at:
```
http://localhost:8080/v3/api-docs
```

## Request/Response Examples

### Create a Brand
```bash
POST /api/brands
Content-Type: application/json

{
  "name": "Fender",
  "country": "USA"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Fender",
  "country": "USA"
}
```

### Create an Instrument
```bash
POST /api/instruments
Content-Type: application/json

{
  "name": "Stratocaster",
  "description": "Electric Guitar",
  "price": 799.99,
  "stock": 50,
  "categoryId": 1,
  "brandId": 1
}
```

### Create an Order
```bash
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "instrumentId": 1,
      "quantity": 2
    },
    {
      "instrumentId": 2,
      "quantity": 1
    }
  ]
}
```

## Pagination

All list endpoints support pagination with the following query parameters:

- `page` - Page number (0-indexed, default: 0)
- `size` - Page size (default: 10)
- `sortBy` - Column to sort by (default: "id")
- `sortDirection` - Sort direction: ASC or DESC (default: ASC)

Example:
```bash
GET /api/instruments?page=0&size=20&sortBy=price&sortDirection=DESC
```

## Error Handling

The API returns standardized error responses:

```json
{
  "code": "EntityNotFound",
  "description": "Brand not found with id: 123"
}
```

Common HTTP Status Codes:
- `200 OK` - Successful request
- `201 Created` - Resource created
- `204 No Content` - Resource deleted
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Missing or invalid token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `500 Internal Server Error` - Server error

## Validation

All endpoints validate input data. Example validation error response:

```json
{
  "name": "Brand name is required",
  "country": "Country field is optional"
}
```

## Database Schema

The application automatically creates the following tables (with `spring.jpa.hibernate.ddl-auto=update`):

- `users` - Application users
- `brands` - Instrument brands
- `categories` - Instrument categories
- `instruments` - Musical instruments
- `orders` - Customer orders
- `order_items` - Items within orders

All entities include audit fields:
- `created_at` - Timestamp when record was created
- `updated_at` - Timestamp when record was last modified

## Security Configuration

- **CORS**: Configured for React dev server on `http://localhost:5173`
- **CSRF**: Disabled (stateless JWT authentication)
- **Sessions**: Stateless (SessionCreationPolicy.STATELESS)
- **Password Encoding**: BCrypt with strength 12
- **Public Endpoints**: `/api/auth/**` and `/api/email/**`

## Logging

The application uses SLF4J with Logback. Logging is configured with:
- User context (username)
- Client IP address
- Request/response details

## Development Notes

- Use `application-dev.properties` for local development
- Enable debug logging by setting `logging.level.root=DEBUG`
- The app includes an MDC (Mapped Diagnostic Context) filter for enhanced logging

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request



---

**Built with ❤️ using Spring Boot**
