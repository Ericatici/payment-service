# Payment Service

## 📋 About the Project

The **Payment Service** is a microservice responsible for managing payment operations in the **Na Comanda** system. This service integrates with the MercadoPago API to process payments via QR Code and receive payment confirmations through webhooks.

## 🎯 Objectives

- **Process Payments**: Generate QR Codes for payments via MercadoPago
- **Check Status**: Verify the current payment status of orders
- **Receive Confirmations**: Process MercadoPago webhooks to update payment statuses
- **Integration**: Communicate with the Production Service to synchronize order data
- **Monitoring**: Provide health check endpoints for monitoring

## 🏗️ Architecture

The project follows **Clean Architecture** and **Hexagonal Architecture** principles:

```
src/main/java/com/lanchonete/payment/
├── adapter/
│   ├── driver/rest/          # Controllers and input DTOs
│   └── driven/rest/         # External clients and repositories
├── core/
│   ├── application/          # Use cases and application services
│   └── domain/              # Entities and business rules
└── PaymentServiceApplication.java
```

### Technologies Used

- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Web MVC**
- **Maven**
- **Docker & Docker Compose**
- **MySQL 8.0**
- **Swagger/OpenAPI 3**
- **Log4j2**
- **JUnit 5** (Testing)
- **Mockito** (Testing)

## 🚀 How to Run Locally

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- MercadoPago account (for test credentials)

### 1. Environment Configuration

#### Clone the repository
```bash
git clone <repository-url>
cd payment-service
```

#### Configure environment variables
```bash
# Copy the environment template
cp env-template .env

# Edit the .env file with your credentials
nano .env
```

**Required variables in the `.env` file:**
```env
# MercadoPago Configuration
MERCADOPAGO_CLIENT_ID=your_client_id_here
MERCADOPAGO_SECRET_ID=your_secret_id_here
MERCADOPAGO_BASE_URL=https://api.mercadopago.com
MERCADOPAGO_EXTERNAL_POS_ID=nacomanda2025
MERCADOPAGO_URL=/oauth/token
MERCADOPAGO_ORDERS_PATH=/v1/orders

# Production Service Configuration
PRODUCTION_SERVICE_URL=http://localhost:8082

# Database Configuration (optional - for future implementations)
DATABASE_NAME=payment_db
DATABASE_HOST=db
DATABASE_USER=payment_user
DATABASE_PASSWORD=payment123
```

### 2. Running with Docker Compose (Recommended)

```bash
# Start all services
docker-compose up -d

# View logs in real-time
docker-compose up

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### 3. Local Execution (Development)

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

## 📡 API Endpoints

### Base URL
```
http://localhost:8084
```

### 1. Get Payment Status
```http
GET /{orderId}/payment-status
```

**Parameters:**
- `orderId` (path): Order ID

**Optional Headers:**
- `requestTraceId`: Request tracking ID

**Success Response (200):**
```json
{
  "orderId": 1,
  "paymentStatus": "APPROVED"
}
```

**Possible Statuses:**
- `PENDING` - Payment pending
- `APPROVED` - Payment approved
- `REJECTED` - Payment rejected

**Error Responses:**
- `404 Not Found` - Order not found
- `500 Internal Server Error` - Payment service error or MercadoPago integration error

### 2. Generate Payment QR Code
```http
POST /paymentData
```

**Headers:**
- `Content-Type: application/json`
- `requestTraceId` (optional): Request tracking ID

**Request Body:**
```json
{
  "id": 1,
  "totalPrice": 50.00
}
```

**Success Response (200):**
```json
{
  "paymentId": "mp-payment-123",
  "qrCode": "00020126580014br.gov.bcb.pix"
}
```

**Error Responses:**
- `404 Not Found` - Order not found
- `500 Internal Server Error` - MercadoPago integration error

### 3. Payment Confirmation Webhook
```http
POST /webhooks/payment-confirmation
```

**Headers:**
- `Content-Type: application/json`
- `requestTraceId` (optional): Request tracking ID

**Request Body Example:**
```json
{
  "id": 12345,
  "live_mode": true,
  "type": "payment",
  "date_created": "2024-01-15T10:30:00Z",
  "user_id": 123456,
  "api_version": "v1",
  "action": "payment.updated",
  "data": {
    "id": "mp-payment-123"
  }
}
```

**Success Response (200):**
- Empty body

**Error Responses:**
- `400 Bad Request` - Invalid payment data
- `404 Not Found` - Order not found for the given payment ID
- `500 Internal Server Error` - Payment processing error or MercadoPago integration error

### 4. Health Check
```http
GET /actuator/health
```

**Success Response (200):**
```json
{
  "status": "UP"
}
```

## 🔧 Configuration

### application.yml
```yaml
server:
  port: 8084

spring:
  application:
    name: payment-service

# Production Service Configuration
production:
  service:
    url: ${production-service-url:http://localhost:8082}

# MercadoPago Configuration
mercadopago:
  baseUrl: ${mercadopago-base-url:https://api.mercadopago.com}
  externalPosId: ${mercadopago-external-pos-id:nacomanda2025}
  path:
    auth: ${mercadopago-url:/oauth/token}
    orders: ${mercadopago-orders-path:/v1/orders}
  access:
    client: ${mercadopago-client-id}
    secret: ${mercadopago-secret-id}

management:
  endpoints:
    web:
      exposure:
        include: health
```

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Tests with Coverage
```bash
mvn test jacoco:report
```

### Run Specific Tests
```bash
# MVC Tests
mvn test -Dtest=PaymentControllerMvcTest

# Service Tests
mvn test -Dtest=*ServiceTest

# Repository Tests
mvn test -Dtest=*RepositoryTest

# Exception Handler Tests
mvn test -Dtest=GlobalExceptionHandlerTest
```

### Test Structure

The project includes comprehensive unit tests covering:

- **Service Tests**: 
  - `ConsultPaymentStatusServiceTest` - Payment status consultation
  - `GeneratePaymentQrCodeServiceTest` - QR code generation
  - `ProcessPaymentWebhookServiceTest` - Webhook processing

- **Controller Tests**:
  - `PaymentControllerTest` - Basic controller tests
  - `PaymentControllerMvcTest` - Comprehensive MVC tests with various scenarios

- **Repository Tests**:
  - `PaymentRepositoryImplTest` - Payment repository implementation
  - `MercadoPagoGatewayRepositoryTest` - MercadoPago gateway integration

- **Mapper Tests**:
  - `MPQrCodePaymentRequestMapperTest` - QR code request mapping

- **Exception Handler Tests**:
  - `GlobalExceptionHandlerTest` - Exception handling for all error types

### Test Coverage

The test suite covers:
- ✅ All service layer methods
- ✅ All controller endpoints
- ✅ Repository implementations
- ✅ Exception handling
- ✅ Error scenarios
- ✅ Integration flows

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:8084/actuator/health
```

### Logs
```bash
# View container logs
docker-compose logs app

# Follow logs in real-time
docker-compose logs -f app
```

## 🐳 Docker

### Build Image
```bash
docker build -t payment-service .
```

### Run Container
```bash
docker run -p 8084:8084 \
  -e MERCADOPAGO_CLIENT_ID=your_client_id \
  -e MERCADOPAGO_SECRET_ID=your_secret_id \
  -e PRODUCTION_SERVICE_URL=http://production-service:8082 \
  payment-service
```

## 🔗 Integrations

### MercadoPago
- **Authentication**: OAuth 2.0 with automatic token refresh
- **QR Code**: Generation of QR codes for payments
- **Webhooks**: Receiving payment confirmations
- **API Version**: v1

### Production Service
- **Communication**: HTTP REST
- **Endpoints**: 
  - `GET /production/orders/{orderId}` - Get order by ID
  - `GET /production/orders/payment/{paymentId}` - Get order by payment ID
  - `PUT /production/orders/{orderId}/payment-status` - Update order payment status
- **URL**: Configurable via `production.service.url`

## 📝 Logging

The project uses **Log4j2** for structured logging:

```xml
<!-- Log configuration -->
<Configuration>
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <Root level="INFO">
      <AppenderRef ref="Console"/>
    </Root>
  </Loggers>
</Configuration>
```

### Log Levels
- **INFO**: General application flow
- **ERROR**: Error conditions and exceptions
- **DEBUG**: Detailed debugging information (when enabled)

## 🚨 Troubleshooting

### Common Issues

1. **MercadoPago Connection Error**
   - Verify credentials in the `.env` file
   - Confirm if credentials are for test or production environment
   - Check network connectivity to MercadoPago API

2. **404 - Order Not Found**
   - Verify that the Production Service is running
   - Confirm that the orderId exists in the system
   - Check the Production Service URL configuration

3. **Port Already in Use**
   - Verify that port 8084 is available
   - Use `lsof -i :8084` to identify processes using the port
   - Change the port in `application.yml` if needed

4. **Docker Issues**
   - Execute `docker-compose down -v` to clean volumes
   - Verify that Docker is running
   - Check Docker logs: `docker-compose logs app`

5. **Token Refresh Errors**
   - Verify MercadoPago credentials are correct
   - Check network connectivity
   - Review MercadoPago API status

### Useful Commands

```bash
# Check container status
docker-compose ps

# View logs for a specific service
docker-compose logs app
docker-compose logs db

# Access the application container
docker-compose exec app sh

# Access the database
docker-compose exec db mysql -u payment_user -p payment_db

# Restart a specific service
docker-compose restart app

# View environment variables
docker-compose config
```

## 📚 Additional Documentation

- **API Documentation**: Available at `http://localhost:8084/swagger-ui.html` when the service is running
- **MercadoPago API**: [Official Documentation](https://www.mercadopago.com.br/developers)
- **Spring Boot Documentation**: [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)

## 🏗️ Project Structure

```
payment-service/
├── src/
│   ├── main/
│   │   ├── java/com/lanchonete/payment/
│   │   │   ├── adapter/
│   │   │   │   ├── driver/rest/          # REST controllers and DTOs
│   │   │   │   └── driven/rest/          # External integrations
│   │   │   ├── core/
│   │   │   │   ├── application/          # Use cases and services
│   │   │   │   └── domain/               # Domain models and exceptions
│   │   │   └── PaymentServiceApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── log4j2.xml
│   └── test/
│       └── java/com/lanchonete/payment/
│           ├── adapter/                  # Adapter tests
│           ├── core/                     # Core tests
│           └── mocks/                    # Test mocks
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow Clean Architecture principles
- Write unit tests for new features
- Maintain code coverage above 80%
- Use meaningful commit messages
- Update documentation for API changes

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

## 👥 Team

- **Development**: Na Comanda Team
- **Architecture**: Clean Architecture + Hexagonal Architecture
- **Technology**: Spring Boot + Java 17

---

**Project Status**: ✅ Active Development

**Last Update**: November 2024
