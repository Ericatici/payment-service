# Customer Service

## 📋 About the Project

The **Customer Service** is a microservice developed as part of the **Na Comanda** system, a complete solution for restaurant and snack bar management. This service is responsible for managing customer information, including registration, querying, and updating personal data.

## 🎯 Objectives

- **Customer Management**: Register, query, and update customer information
- **Data Validation**: Ensure data integrity and validity of input data
- **RESTful API**: Provide a standardized interface for communication with other services
- **Clean Architecture**: Implement Clean Architecture and Domain-Driven Design principles
- **Testability**: Maintain high test coverage to ensure code quality

## 🏗️ Architecture

The project follows **Clean Architecture** and **Domain-Driven Design (DDD)** principles:

```
src/
├── main/java/com/lanchonete/customer/
│   ├── adapter/
│   │   ├── driver/rest/          # REST Controllers
│   │   └── driven/persistence/   # Repository implementations
│   ├── core/
│   │   ├── application/          # Use Cases and Services
│   │   └── domain/              # Domain entities
│   └── CustomerServiceApplication.java
└── test/java/                   # Unit and integration tests
```

### Application Layers

- **Domain**: Business entities and domain rules
- **Application**: Use cases and application services
- **Adapter/Driver**: REST Controllers and input interfaces
- **Adapter/Driven**: Persistence implementations and output interfaces

## 🚀 Technologies Used

- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven**
- **Docker & Docker Compose**
- **JUnit 5**
- **Mockito**
- **Lombok**
- **SpringDoc OpenAPI** (Swagger)
- **Log4j2**

## 📋 Prerequisites

Before starting the project, make sure you have installed:

- [Java 17](https://openjdk.java.net/projects/jdk/17/) or higher
- [Maven 3.6+](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)

## 🛠️ Setup and Execution

### Option 1: Docker Compose Execution (Recommended)

1. **Clone the repository and navigate to the directory:**
   ```bash
   cd customer-service
   ```

2. **Start the services:**
   ```bash
   docker-compose up --build -d
   ```

3. **Verify that services are running:**
   ```bash
   docker-compose ps
   ```

4. **Access the application:**
   - **API**: http://localhost:8081
   - **Swagger UI**: http://localhost:8081/swagger-ui.html
   - **Health Check**: http://localhost:8081/actuator/health
   - **MySQL**: localhost:3306

### Option 2: Local Execution (Development)

1. **Set up the MySQL database:**
   ```sql
   CREATE DATABASE customer_db;
   CREATE USER 'customer_user'@'localhost' IDENTIFIED BY 'customer_password';
   GRANT ALL PRIVILEGES ON customer_db.* TO 'customer_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Configure environment variables:**
   ```bash
   export DATABASE_HOST=localhost
   export DATABASE_NAME=customer_db
   export DATABASE_USER=customer_user
   export DATABASE_PASSWORD=customer_password
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

## 📚 API Endpoints

### Customers

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/customer/save` | Register a new customer |
| `GET` | `/customer/{cpf}` | Find customer by CPF |
| `PUT` | `/customer/{cpf}` | Update customer data |

### Request/Response Examples

#### Register Customer
```bash
curl -X POST http://localhost:8081/customer/save \
  -H "Content-Type: application/json" \
  -H "X-Request-Trace-Id: optional-trace-id" \
  -d '{
    "cpf": "12345678900",
    "name": "John Doe",
    "email": "john.doe@email.com"
  }'
```

**Response (201 Created):**
```json
{
  "cpf": "12345678900",
  "name": "John Doe",
  "email": "john.doe@email.com"
}
```

#### Find Customer
```bash
curl -X GET http://localhost:8081/customer/12345678900 \
  -H "X-Request-Trace-Id: optional-trace-id"
```

**Response (200 OK):**
```json
{
  "cpf": "12345678900",
  "name": "John Doe",
  "email": "john.doe@email.com"
}
```

#### Update Customer
```bash
curl -X PUT http://localhost:8081/customer/12345678900 \
  -H "Content-Type: application/json" \
  -H "X-Request-Trace-Id: optional-trace-id" \
  -d '{
    "name": "John Doe Santos",
    "email": "john.santos@email.com"
  }'
```

**Response (202 Accepted):**
```json
{
  "cpf": "12345678900",
  "name": "John Doe Santos",
  "email": "john.santos@email.com"
}
```

### API Documentation

The API documentation is available via Swagger UI:
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

## 🧪 Running Tests

### All Tests
```bash
mvn test
```

### Specific Test Suites
```bash
# Unit tests
mvn test -Dtest="*ServiceTest"

# Integration tests
mvn test -Dtest="*IntegrationTest"

# Controller tests
mvn test -Dtest="*ControllerTest"

# Repository tests
mvn test -Dtest="*RepositoryTest"
```

### Test Coverage

The project includes comprehensive test coverage:
- **Unit Tests**: Service layer tests (CreateCustomerService, FindCustomerService, UpdateCustomerService)
- **Integration Tests**: Repository and controller integration tests
- **MVC Tests**: REST endpoint tests with MockMvc
- **Validation Tests**: Input validation and error handling tests
- **Performance Tests**: Load and stress testing

**Total Test Count**: 49 tests covering all major functionality

## 📁 Test Structure

The project has an organized test structure:

```
src/test/java/com/lanchonete/customer/
├── adapter/
│   ├── driver/rest/controllers/mvc/    # MVC controller tests
│   │   ├── CustomerControllerTest.java
│   │   ├── CustomerControllerIntegrationTest.java
│   │   ├── CustomerControllerValidationTest.java
│   │   └── CustomerControllerPerformanceTest.java
│   └── driven/persistence/repositories/ # Repository tests
│       └── CustomerRepositoryImplTest.java
├── core/application/services/          # Service tests
│   ├── CreateCustomerServiceTest.java
│   ├── FindCustomerServiceTest.java
│   └── UpdateCustomerServiceTest.java
└── mocks/                              # Test mocks
    ├── CustomerMock.java
    └── CustomerDTOMock.java
```

### Test Types

- **Unit Tests**: Test isolated components with mocked dependencies
- **Integration Tests**: Test integration between layers
- **MVC Tests**: Test REST endpoints with MockMvc
- **Performance Tests**: Load and stress testing

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_HOST` | Database host | `localhost` |
| `DATABASE_NAME` | Database name | `customer_db` |
| `DATABASE_USER` | Database user | `customer_user` |
| `DATABASE_PASSWORD` | Database password | `customer_password` |
| `SERVER_PORT` | Application port | `8081` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` |

### Profiles

- **default**: Configuration for local development
- **docker**: Configuration for Docker environment
- **test**: Configuration for testing

## 📊 Monitoring

The application includes monitoring endpoints:

- **Health Check**: `/actuator/health`
- **Info**: `/actuator/info`
- **Metrics**: `/actuator/metrics`

## 🔒 Data Validation

The API validates input data according to the following rules:

- **CPF**: Required, exactly 11 digits
- **Name**: Required, not blank, max 255 characters
- **Email**: Required, valid email format

## 🐛 Troubleshooting

### Common Issues

1. **Database connection error:**
   - Verify that MySQL is running
   - Confirm database credentials
   - Check network connectivity if using Docker

2. **Port already in use:**
   - Change the port in `application.yml`
   - Or stop the process using the port: `lsof -ti:8081 | xargs kill -9`

3. **Docker build issues:**
   - Ensure Docker and Docker Compose are properly installed
   - Try rebuilding: `docker-compose down && docker-compose up --build`

4. **Test failures:**
   - Ensure all dependencies are installed: `mvn clean install`
   - Check that the test database is properly configured

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Standards

- Follow Clean Architecture principles
- Write unit tests for new features
- Maintain test coverage above 80%
- Use meaningful commit messages
- Follow Java naming conventions

## 📝 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## 👥 Team

- **Development**: Na Comanda Team
- **Architecture**: Clean Architecture + DDD
- **DevOps**: Docker + Docker Compose

## 📞 Support

For questions or support, please contact through the official Na Comanda project channels.

---

**Customer Service** - Part of the Na Comanda ecosystem 🍽️
