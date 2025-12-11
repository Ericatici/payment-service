# Production Service - Na Comanda

## 📋 About the Project

The **Production Service** is a microservice responsible for managing the order production flow in the **Na Comanda** system. This service is part of a microservices architecture and is responsible for:

- Managing order status during the production process
- Controlling the order flow from receipt to completion
- Integrating with other microservices (Customer Service, Order Service, and Payment Service)
- Providing REST APIs for querying and updating orders
- Managing payments and QR code generation
- Processing payment confirmations and automatically updating status

## 🎯 Objectives

### Main Features
- **Status Management**: Control the order lifecycle (WAITING_PAYMENT → RECEIVED → IN_PREPARATION → READY → FINISHED)
- **Payment Management**: Integration with payment service for QR code generation and status updates
- **Integration**: Communicate with other microservices (Customer Service, Order Service, Payment Service)
- **Monitoring**: Provide health check endpoints for monitoring
- **Persistence**: Store and retrieve order and item data
- **API Documentation**: Swagger/OpenAPI for interactive API documentation

### Order Status
- `WAITING_PAYMENT` - Waiting for payment
- `RECEIVED` - Order received
- `IN_PREPARATION` - In preparation
- `READY` - Ready for delivery
- `FINISHED` - Finished
- `CANCELLED` - Cancelled

### Payment Status
- `PENDING` - Payment pending
- `APPROVED` - Payment approved
- `REJECTED` - Payment rejected
- `CANCELLED` - Payment cancelled

## 🏗️ Architecture

### Technologies Used
- **Java 17** - Programming language
- **Spring Boot 3.4.4** - Application framework
- **Spring Data JPA** - Data persistence
- **MySQL 8.0** - Relational database
- **Lombok** - Boilerplate reduction
- **Log4j2** - Logging system
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **Spring Actuator** - Monitoring and metrics
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework for tests

### Patterns Used
- **Clean Architecture**: Clear separation between layers (Domain, Application, Infrastructure)
- **Repository Pattern**: Persistence layer abstraction
- **Use Case Pattern**: Business logic encapsulation
- **DTO Pattern**: Data transfer between layers

### Project Structure
```
src/main/java/com/lanchonete/production/
├── adapter/
│   ├── driven/           # Infrastructure layer
│   │   ├── clients/      # REST clients for other microservices
│   │   │   ├── CustomerServiceClient.java
│   │   │   ├── OrderServiceClient.java
│   │   │   └── PaymentServiceClient.java
│   │   └── persistence/   # Data persistence
│   │       ├── entities/  # JPA entities
│   │       └── repositories/ # Repository implementations
│   └── driver/           # Interface layer
│       └── rest/          # REST controllers
│           ├── controllers/
│           ├── requests/
│           └── responses/
├── core/
│   ├── application/      # Application layer
│   │   ├── config/       # Configurations
│   │   ├── dto/          # Data Transfer Objects
│   │   ├── services/     # Application services
│   │   │   ├── CreateOrderService.java
│   │   │   ├── CreateOrderItemService.java
│   │   │   ├── FindOrderService.java
│   │   │   ├── UpdateOrderService.java
│   │   │   ├── UpdateOrderPaymentStatusService.java
│   │   │   └── GeneratePaymentQrCodeService.java
│   │   └── usecases/     # Use cases (interfaces)
│   └── domain/           # Domain layer
│       ├── exceptions/    # Domain exceptions
│       ├── model/         # Domain entities
│       │   ├── enums/     # Enumerators
│       │   ├── Order.java
│       │   └── OrderItem.java
│       └── repositories/ # Repository interfaces
└── ProductionServiceApplication.java
```

## 🚀 How to Run Locally

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose** (for containerized execution)
- **MySQL 8.0** (if running without Docker)

### Option 1: Execution with Docker Compose (Recommended)

1. **Clone the repository and navigate to the directory:**
   ```bash
   cd production-service
   ```

2. **Configure environment variables:**
   
   Create a `.env` file in the project root with the following variables:
   ```bash
   # Database Configuration
   MYSQL_ROOT_PWD=rootpassword
   MYSQL_DB=production_db
   MYSQL_USER=production_user
   MYSQL_PWD=production_password
   
   DATABASE_NAME=production_db
   DATABASE_HOST=db
   DATABASE_USER=root
   DATABASE_PASSWORD=rootpassword
   
   # Service URLs
   CUSTOMER_SERVICE_URL=http://localhost:8081
   ORDER_SERVICE_URL=http://localhost:8083
   PAYMENT_SERVICE_URL=http://localhost:8084
   
   # Mercado Pago (optional)
   MERCADOPAGO_CLIENT_ID=your_client_id
   MERCADOPAGO_SECRET_ID=your_secret_id
   ```

3. **Run the service:**
   ```bash
   docker-compose up --build -d
   ```

4. **Verify the service is running:**
   ```bash
   curl http://localhost:8082/actuator/health
   ```

5. **To stop the service:**
   ```bash
   docker-compose down
   ```

6. **To view logs:**
   ```bash
   docker-compose logs -f app
   ```

### Option 2: Local Execution (Without Docker)

1. **Configure the MySQL database:**
   ```sql
   CREATE DATABASE production_db;
   CREATE USER 'production_user'@'localhost' IDENTIFIED BY 'production_password';
   GRANT ALL PRIVILEGES ON production_db.* TO 'production_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Configure environment variables:**
   ```bash
   export DATABASE_HOST=localhost
   export DATABASE_NAME=production_db
   export DATABASE_USER=production_user
   export DATABASE_PASSWORD=production_password
   export CUSTOMER_SERVICE_URL=http://localhost:8081
   export ORDER_SERVICE_URL=http://localhost:8083
   export PAYMENT_SERVICE_URL=http://localhost:8084
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

## 🧪 Running Tests

### All Tests
```bash
mvn test
```

### Specific Tests
```bash
# Unit tests only
mvn test -Dtest="*Test"

# Integration tests only
mvn test -Dtest="*MvcTest"

# Specific test
mvn test -Dtest="ProductionControllerMvcTest"
```

### Test Structure

The project has unit tests for the following components:

#### Services
- `CreateOrderServiceTest` - Tests for order creation
- `CreateOrderItemServiceTest` - Tests for order item creation
- `FindOrderServiceTest` - Tests for order search
- `UpdateOrderServiceTest` - Tests for order updates
- `UpdateOrderPaymentStatusServiceTest` - Tests for payment status updates
- `GeneratePaymentQrCodeServiceTest` - Tests for payment QR code generation

#### Controllers
- `ProductionControllerTest` - Controller unit tests
- `ProductionControllerMvcTest` - MVC integration tests

#### Repositories
- `OrderRepositoryImplTest` - Order repository tests

#### Exception Handlers
- `GlobalExceptionHandlerTest` - Global exception handler tests

### Test Coverage

To check test coverage, run:
```bash
mvn clean test
```

**Note:** The project currently has 67 unit tests, all passing successfully.

## 📚 API Endpoints

### Base URL
```
http://localhost:8082/production
```

### Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/orders` | List all orders |
| `GET` | `/orders/status/{status}` | List orders by status |
| `GET` | `/orders/{id}` | Get order by ID |
| `GET` | `/orders/active` | List active orders |
| `GET` | `/orders/payment/{paymentId}` | Get order by payment ID |
| `PATCH` | `/orders/{id}/status/{status}` | Update order status |

### Usage Examples

#### List all orders
```bash
curl -H "X-Request-Trace-Id: trace-123" \
     http://localhost:8082/production/orders
```

#### Get orders by status
```bash
curl -H "X-Request-Trace-Id: trace-123" \
     http://localhost:8082/production/orders/status/RECEIVED
```

#### Update order status
```bash
curl -X PATCH \
     -H "X-Request-Trace-Id: trace-123" \
     http://localhost:8082/production/orders/1/status/IN_PREPARATION
```

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_HOST` | Database host | `production-db` |
| `DATABASE_NAME` | Database name | `production_db` |
| `DATABASE_USER` | Database user | `root` |
| `DATABASE_PASSWORD` | Database password | `rootpassword` |
| `CUSTOMER_SERVICE_URL` | Customer Service URL | `http://localhost:8081` |
| `ORDER_SERVICE_URL` | Order Service URL | `http://localhost:8083` |
| `PAYMENT_SERVICE_URL` | Payment Service URL | `http://localhost:8084` |
| `MERCADOPAGO_CLIENT_ID` | Mercado Pago Client ID | - |
| `MERCADOPAGO_SECRET_ID` | Mercado Pago Secret ID | - |

### Ports
- **Application**: 8082
- **MySQL**: 3307 (mapped to 3306 in container)

## 🏥 Health Check

### Health Endpoint
```bash
curl http://localhost:8082/actuator/health
```

### Expected Response
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

## 📖 API Documentation

### Swagger/OpenAPI

The service has interactive API documentation available through Swagger:

```bash
http://localhost:8082/swagger-ui.html
```

Or through the JSON endpoint:
```bash
http://localhost:8082/v3/api-docs
```

The documentation includes:
- All available endpoints
- Request and response parameters
- Data models
- Usage examples

## 📊 Monitoring

### Logs
Logs are configured using Log4j2 and include:
- Timestamp
- Log level
- Message
- Request Trace ID
- Stack trace (for errors)
- Logger name

### Log Format
Logs are structured in JSON to facilitate analysis and integration with monitoring tools.

### Metrics
- Health check endpoint available (`/actuator/health`)
- Structured logs for analysis
- Request tracing for debugging (via `X-Request-Trace-Id` header)
- Swagger UI for API documentation and testing

## 🐛 Troubleshooting

### Common Issues

#### 1. Database Connection Error
```bash
# Check if MySQL is running
docker-compose ps

# Check database logs
docker-compose logs db
```

#### 2. Port Already in Use
```bash
# Check processes using port 8082
lsof -i :8082
# or on Linux
netstat -tulpn | grep 8082

# Stop process if necessary
kill -9 <PID>
```

#### 3. Microservices Connection Error
```bash
# Check if other services are running
curl http://localhost:8081/actuator/health  # Customer Service
curl http://localhost:8083/actuator/health  # Order Service
curl http://localhost:8084/actuator/health  # Payment Service

# Check environment variables
docker-compose config
```

#### 4. Build Issues
```bash
# Clean Maven cache
mvn clean

# Clean Docker cache
docker system prune -f

# Full rebuild
mvn clean install
docker-compose build --no-cache
```

#### 5. Test Issues
```bash
# Run tests with more details
mvn test -X

# Run only failed tests
mvn test -Dtest="*Test" -DfailIfNoTests=false

# View test reports
cat target/surefire-reports/*.txt
```

## 🤝 Contributing

### Code Standards
- Follow Java conventions
- Use descriptive names for variables and methods
- Document public methods
- Write unit tests for new functionality

### Commit Structure
```
feat: add new feature
fix: fix bug
docs: update documentation
test: add or fix tests
refactor: refactor code without changing functionality
```

## 📝 License

This project is part of the **Na Comanda** system and is under proprietary license.

## 📞 Support

For questions or issues, contact the development team.

---

**Developed with ❤️ for the Na Comanda system**
