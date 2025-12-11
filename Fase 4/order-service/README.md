# 🍔 Order Service - Na Comanda

## 📋 About the Project

The **Order Service** is a microservice responsible for managing orders and products in the **Na Comanda** system, a restaurant delivery platform. This service provides RESTful APIs for creating, querying, updating, and deleting products and orders.

## 🎯 Objectives

- **Manage Products**: Complete CRUD operations for products (meals, drinks, sides)
- **Manage Orders**: Create and query customer orders
- **Integration**: Communication with other microservices in the Na Comanda ecosystem
- **Scalability**: Architecture prepared for growth and high demand
- **Performance**: NoSQL database (MongoDB) for fast operations

## 🏗️ Architecture

### **Technologies Used**

- **Java 17** - Programming language
- **Spring Boot 3.4.4** - Main framework
- **Spring Data MongoDB** - Data persistence
- **MongoDB 7.0** - NoSQL database
- **Maven** - Dependency management
- **Docker & Docker Compose** - Containerization
- **JUnit 5** - Unit testing
- **Mockito** - Mocking in tests
- **SpringDoc OpenAPI** - API documentation
- **Log4j2** - Logging framework
- **Lombok** - Code generation

### **Architectural Patterns**

- **Clean Architecture** - Clear separation of responsibilities
- **Hexagonal Architecture** - Ports and Adapters
- **Domain-Driven Design (DDD)** - Domain-based modeling
- **SOLID Principles** - Object-oriented design principles

## 📁 Project Structure

```
order-service/
├── src/
│   ├── main/java/com/lanchonete/order/
│   │   ├── adapter/
│   │   │   ├── driver/rest/          # REST Controllers
│   │   │   │   ├── controllers/     # API endpoints
│   │   │   │   ├── requests/        # Request DTOs
│   │   │   │   └── responses/       # Response DTOs
│   │   │   ├── driven/
│   │   │   │   ├── clients/          # External service clients
│   │   │   │   └── persistence/     # Repositories and entities
│   │   ├── core/
│   │   │   ├── application/          # Use Cases and Services
│   │   │   │   ├── config/          # Configuration classes
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── services/        # Business logic services
│   │   │   │   └── usecases/        # Use case interfaces
│   │   │   └── domain/              # Domain entities
│   │   │       ├── exceptions/      # Custom exceptions
│   │   │       ├── model/           # Domain models
│   │   │       └── repositories/    # Repository interfaces
│   │   └── OrderServiceApplication.java
│   └── test/java/                    # Unit and integration tests
├── src/main/resources/
│   ├── application.yml               # Application configuration
│   └── log4j2.xml                    # Logging configuration
├── src/test/resources/
│   └── application-test.yml         # Test configuration
├── docker-compose.yml                # Container orchestration
├── Dockerfile                        # Application image
├── init-mongo.js                     # MongoDB initialization script
├── pom.xml                           # Maven configuration
└── README.md                         # This file
```

## 🚀 How to Run Locally

### **Prerequisites**

- **Java 17** or higher
- **Docker** and **Docker Compose**
- **Maven 3.6+** (optional, if running without Docker)
- **Git**

### **Option 1: Docker (Recommended)**

#### **1. Clone the repository**
```bash
git clone <repository-url>
cd order-service
```

#### **2. Create environment file**
Create a `.env` file with the following variables:
```bash
DATABASE_NAME=order_db
DATABASE_HOST=mongodb
DATABASE_PORT=27017
DATABASE_USER=admin
DATABASE_PASSWORD=password
DATABASE_AUTH=admin
PRODUCTION_SERVICE_URL=http://localhost:8082
MONGODB_ROOT_USERNAME=admin
MONGODB_ROOT_PASSWORD=password
MONGODB_DATABASE=order_db
```

#### **3. Run with Docker Compose**
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

#### **4. Verify it's working**
- **Application**: http://localhost:8083
- **Health Check**: http://localhost:8083/actuator/health
- **API Documentation**: http://localhost:8083/swagger-ui.html
- **MongoDB**: localhost:27017

### **Option 2: Local Execution**

#### **1. Install MongoDB**
```bash
# Ubuntu/Debian
sudo apt-get install mongodb

# macOS (with Homebrew)
brew install mongodb-community

# Windows
# Download from official site: https://www.mongodb.com/try/download/community
```

#### **2. Configure environment variables**
Set the following environment variables or create a `.env` file:
```bash
export DATABASE_NAME=order_db
export DATABASE_HOST=localhost
export DATABASE_PORT=27017
export DATABASE_USER=admin
export DATABASE_PASSWORD=password
export DATABASE_AUTH=admin
export PRODUCTION_SERVICE_URL=http://localhost:8082
```

#### **3. Run the application**
```bash
# Compile and run
mvn clean spring-boot:run

# Or compile and run the JAR
mvn clean package
java -jar target/order-service-0.0.1-SNAPSHOT.jar
```

## 🧪 Running Tests

### **All Tests**
```bash
# Run all tests
mvn test -DskipTests=false

# Run tests with coverage (if configured)
mvn test jacoco:report
```

### **Specific Tests**
```bash
# Run only unit tests
mvn test -Dtest="*Test"

# Run a specific test class
mvn test -Dtest="ProductControllerTest"

# Run tests in a specific package
mvn test -Dtest="com.lanchonete.order.core.application.services.*"
```

### **Test Coverage**
The project includes comprehensive test coverage:
- **Unit Tests**: 79 tests covering services, controllers, repositories, and utilities
- **Integration Tests**: Available in the `integration` package
- **Test Mocks**: Located in `src/test/java/com/lanchonete/order/mocks/`

## 📚 API Documentation

### **Swagger/OpenAPI**
Interactive API documentation is available at:
- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8083/v3/api-docs

### **Product Endpoints**

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/products` | List all products |
| `GET` | `/products/{id}` | Get product by ID |
| `GET` | `/products/category/{category}` | Get products by category |
| `POST` | `/products` | Create new product |
| `PUT` | `/products/{id}` | Update product |
| `DELETE` | `/products/{id}` | Delete product |

**Product Categories**: `LANCHE`, `BEBIDA`, `ACOMPANHAMENTO`, `SOBREMESA`

### **Order Endpoints**

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/order` | List all orders |
| `GET` | `/order/active` | List active orders |
| `GET` | `/order/{id}` | Get order by ID |
| `POST` | `/order` | Create new order |

**Headers**: All order endpoints support optional `requestTraceId` header for request tracing.

### **Request Examples**

#### **Create Product**
```bash
curl -X POST http://localhost:8083/products \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": "PROD-001",
    "name": "Classic Hamburger",
    "description": "Hamburger with beef, lettuce, tomato and cheese",
    "price": 25.00,
    "category": "LANCHE"
  }'
```

#### **Get Product by ID**
```bash
curl -X GET http://localhost:8083/products/PROD-001
```

#### **Get Products by Category**
```bash
curl -X GET http://localhost:8083/products/category/LANCHE
```

#### **Create Order**
```bash
curl -X POST http://localhost:8083/order \
  -H "Content-Type: application/json" \
  -H "requestTraceId: trace-123" \
  -d '{
    "customerCpf": "12345678900",
    "items": [
      {
        "productId": "PROD-001",
        "quantity": 2
      }
    ]
  }'
```

#### **Get Order by ID**
```bash
curl -X GET http://localhost:8083/order/1 \
  -H "requestTraceId: trace-123"
```

## 🗄️ Database

### **MongoDB Collections**

#### **products**
```json
{
  "_id": "ObjectId",
  "id": "PROD-001",
  "name": "Classic Hamburger",
  "description": "Hamburger with beef, lettuce, tomato and cheese",
  "price": 25.00,
  "category": "LANCHE",
  "createdDate": "2024-01-01T10:00:00Z",
  "updatedDate": "2024-01-01T10:00:00Z"
}
```

#### **orders**
Orders are managed through the Production Service. The Order Service acts as a client that delegates order persistence to the Production Service.

### **Database Configuration**
- **Database Name**: `order_db` (configurable via `DATABASE_NAME`)
- **Connection**: MongoDB with authentication
- **Port**: `27017` (default)

## 🔧 Configuration

### **Environment Variables**

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_NAME` | MongoDB database name | `order_db` |
| `DATABASE_HOST` | MongoDB host | `mongodb` |
| `DATABASE_PORT` | MongoDB port | `27017` |
| `DATABASE_USER` | MongoDB username | Required |
| `DATABASE_PASSWORD` | MongoDB password | Required |
| `DATABASE_AUTH` | MongoDB authentication database | `order_db` |
| `SERVER_PORT` | Application port | `8083` |
| `PRODUCTION_SERVICE_URL` | Production service URL | `http://localhost:8082` |

### **Application Profiles**

- **default**: Default configurations
- **test**: Test configurations (uses embedded MongoDB)
- **prod**: Production configurations

## 📊 Monitoring

### **Health Check**
```bash
curl http://localhost:8083/actuator/health
```

### **Metrics**
- **Spring Boot Actuator** enabled
- **Structured logs** with trace IDs
- **MongoDB performance metrics**
- **Request tracing** via `requestTraceId` header

### **Logging**
The application uses **Log4j2** for structured logging with:
- JSON format output
- Request trace ID tracking
- Error stack traces
- Performance metrics

## 🔐 Security

### **Request Tracing**
All order endpoints support the `requestTraceId` header for distributed tracing:
- If provided, the trace ID is used for request correlation
- If not provided, a UUID is automatically generated
- Trace ID is stored in ThreadContext for logging

## 🐛 Troubleshooting

### **Common Issues**

#### **Port Already in Use**
```bash
# Check processes on port 8083
lsof -i :8083

# Kill process
kill -9 <PID>
```

#### **MongoDB Connection Issues**
```bash
# Check if MongoDB is running
docker-compose ps

# View MongoDB logs
docker-compose logs mongodb

# Verify connection string
echo $DATABASE_HOST
```

#### **Application Won't Start**
```bash
# View application logs
docker-compose logs app

# Check configuration
cat .env

# Verify Java version
java -version
```

#### **Tests Failing**
```bash
# Run tests with verbose output
mvn test -X

# Clean and rebuild
mvn clean test

# Check test configuration
cat src/test/resources/application-test.yml
```

## 🏗️ Building

### **Build JAR**
```bash
mvn clean package
```

### **Build Docker Image**
```bash
docker build -t order-service:latest .
```

### **Skip Tests During Build**
```bash
mvn clean package -DskipTests=true
```

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### **Code Style**
- Follow Java naming conventions
- Use Lombok for boilerplate code reduction
- Write unit tests for new features
- Maintain test coverage above 80%

## 📝 License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

## 👥 Team

- **Development**: Na Comanda Team
- **Architecture**: Clean Architecture + DDD
- **DevOps**: Docker + MongoDB

## 📞 Support

For questions or issues:
- **Issues**: Open an issue in the repository
- **Documentation**: Refer to this README
- **Logs**: Check application logs
- **API Docs**: Visit Swagger UI at http://localhost:8083/swagger-ui.html

## 🔗 Related Services

- **Production Service**: Handles order persistence and production workflow
- **Payment Service**: Manages payment processing
- **Notification Service**: Sends notifications to customers

---

**Na Comanda** - Transforming the delivery experience! 🚀
