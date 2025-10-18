# Predictive Health Risk Assessment API

A Spring Boot REST API application that provides health risk assessment services based on user demographic and health information.

## Features

- **User Authentication**: JWT-based authentication with user registration and login
- **Health Risk Assessment**: Calculate health risk scores based on multiple factors
- **PostgreSQL Database**: Reliable data storage with JPA/Hibernate
- **Validation**: Comprehensive input validation with detailed error messages
- **CORS Support**: Cross-origin resource sharing enabled

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **Lombok**

## Getting Started

### Prerequisites

- Java 21 or higher
- PostgreSQL 15+
- Maven 3.8+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd predictive-health-risk-assesment
   ```

2. **Set up PostgreSQL Database**
   ```bash
   # Install PostgreSQL (macOS with Homebrew)
   brew install postgresql@15
   brew services start postgresql@15
   
   # Create database
   createdb predictive_health_db
   ```

3. **Configure Database Connection**
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/predictive_health_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build and Run**
   ```bash
   ./mvnw clean compile
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Authentication Endpoints

#### 1. User Registration
**POST** `/auth/register`

**Request Body:**
```json
{
    "username": "johnsmith",
    "email": "john.smith@example.com",
    "password": "securepassword123"
}
```

**Response (201 Created):**
```json
{
    "token_type": "Bearer",
    "user_id": 1,
    "email": "john.smith@example.com",
    "username": "johnsmith",
    "message": "User registered successfully"
}
```

**Validation Rules:**
- `username`: Required, not blank, unique
- `email`: Required, valid email format, unique
- `password`: Required, not blank, securely encrypted with BCrypt

#### 2. User Login
**POST** `/auth/login`

**Request Body:**
```json
{
    "username": "johnsmith",
    "password": "securepassword123"
}
```

**Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiam9obnNtaXRoIiwic3ViIjoiam9obi5zbWl0aEBleGFtcGxlLmNvbSIsImlhdCI6MTc1OTM4OTkyNywiZXhwIjoxNzU5MzkwMDEzfQ...",
    "token_type": "Bearer",
    "user_id": 1,
    "email": "john.smith@example.com",
    "username": "johnsmith",
    "message": "Login successful"
}
```

**Authentication Security:**
- Passwords are encrypted using BCrypt with salt
- JWT tokens are signed with HS512 algorithm
- Invalid credentials return 401 Unauthorized
- Duplicate users (email/username) are rejected during registration

### Health Risk Assessment Endpoints

#### 1. Create Health Risk Assessment
**POST** `/health-risk-assessment`

**Request Body:**
```json
{
    "age": 30,
    "sex": "MALE",
    "weight": 64,
    "height": 1.70,
    "bmi": 25.5,
    "hereditary_diseases": ["None"],
    "number_of_dependents": 2,
    "is_smoker": false,
    "city": "Mumbai",
    "blood_pressure": "NORMAL",
    "has_diabetes": "Yes",
    "userId": 99,
    "regular_exercise": "yes",
    "job_title": "Software Engineer"
}
```

**Response (201 Created):**
```json
{
    "id": 6,
    "age": 30,
    "sex": "MALE",
    "weight": 64.0,
    "height": 1.70,
    "bmi": 25.5,
    "hereditary_diseases": ["None"],
    "number_of_dependents": 2,
    "is_smoker": false,
    "city": "Mumbai",
    "blood_pressure": "NORMAL",
    "has_diabetes": "Yes",
    "regular_exercise": "yes",
    "job_title": "Software Engineer",
    "risk_score": 42.0,
    "risk_category": "MEDIUM",
    "created_at": "2025-10-01T18:13:37.552000",
    "updated_at": "2025-10-01T18:13:37.552000",
    "score": 75,
    "suggestions": [
        "You should jog regularly for better cardiovascular health",
        "Consider walking at least 30 minutes daily",
        "Regular health check-ups are important"
    ]
}
```

**Field Validation Rules:**

| Field | Type | Required | Validation Rules |
|-------|------|----------|------------------|
| `age` | Integer | Yes | 0-150 |
| `sex` | String | Yes | MALE, FEMALE, OTHER |
| `weight` | Double | Yes | 1.0-500.0 kg |
| `height` | Double | Yes | 0.5-3.0 meters |
| `bmi` | Double | Yes | 10.0-50.0 |
| `hereditary_diseases` | Array[String] | No | Array of disease names |
| `number_of_dependents` | Integer | Yes | ≥ 0 |
| `is_smoker` | Boolean | Yes | true/false |
| `city` | String | Yes | Max 100 characters |
| `blood_pressure` | String | Yes | NORMAL, HIGH, LOW, HYPERTENSION_STAGE_1, HYPERTENSION_STAGE_2 |
| `has_diabetes` | String | Yes | Yes, No, yes, no |
| `regular_exercise` | String | Yes | yes, no, Yes, No |
| `job_title` | String | Yes | Max 100 characters |
| `userId` | Long | No | User identifier |

#### 2. Get All Assessments
**GET** `/health-risk-assessment`

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "age": 35,
        "sex": "MALE",
        // ... other fields
    }
]
```

#### 3. Get Assessment by ID
**GET** `/health-risk-assessment/{id}`

**Response (200 OK):**
```json
{
    "id": 1,
    "age": 35,
    "sex": "MALE",
    // ... other fields
}
```

### Risk Score Calculation

The risk score is calculated based on multiple factors:

- **Age Factor** (0-25 points):
  - < 30 years: 5 points
  - 30-49 years: 10 points
  - 50-64 years: 20 points
  - ≥ 65 years: 25 points

- **BMI Factor** (0-20 points):
  - Underweight (< 18.5) or Obese (> 30): 20 points
  - Overweight (25-30): 10 points
  - Normal (18.5-25): 5 points

- **Smoking** (0-25 points):
  - Smoker: 25 points
  - Non-smoker: 0 points

- **Diabetes** (0-20 points):
  - Has diabetes: 20 points
  - No diabetes: 0 points

- **Blood Pressure** (0-15 points):
  - HYPERTENSION_STAGE_2: 15 points
  - HYPERTENSION_STAGE_1: 12 points
  - HIGH: 8 points
  - LOW: 5 points
  - NORMAL: 2 points

- **Exercise** (0-10 points):
  - No regular exercise: 10 points
  - Regular exercise: 0 points

- **Hereditary Diseases** (0-10 points):
  - Has hereditary diseases: 10 points
  - No hereditary diseases: 0 points

- **Dependents** (0-5 points):
  - More than 3 dependents: 5 points
  - 3 or fewer: 0 points

**Risk Categories:**
- **LOW**: 0-29 points
- **MEDIUM**: 30-69 points
- **HIGH**: 70-100 points

### Error Responses

**Validation Error (400 Bad Request):**
```json
{
    "timestamp": "2025-10-01T18:13:37.533157",
    "status": 400,
    "error": "Validation Failed",
    "message": "Invalid input data",
    "path": "/api/v1/health-risk-assessment"
}
```

**Resource Not Found (404 Not Found):**
```json
{
    "timestamp": "2025-10-01T18:13:37.533157",
    "status": 404,
    "error": "Resource Not Found",
    "message": "Assessment not found with id: 999",
    "path": "/api/v1/health-risk-assessment/999"
}
```

### Sample curl Commands

**Register User:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johnsmith",
    "email": "john.smith@example.com",
    "password": "securepassword123"
  }'
```

**Login User:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johnsmith",
    "password": "securepassword123"
  }'
```

**Create Health Assessment:**
```bash
curl -X POST http://localhost:8080/api/v1/health-risk-assessment \
  -H "Content-Type: application/json" \
  -d '{
    "age": 30,
    "sex": "MALE",
    "weight": 64,
    "height": 1.70,
    "bmi": 25.5,
    "hereditary_diseases": ["None"],
    "number_of_dependents": 2,
    "is_smoker": false,
    "city": "Mumbai",
    "blood_pressure": "NORMAL",
    "has_diabetes": "Yes",
    "userId": 99,
    "regular_exercise": "yes",
    "job_title": "Software Engineer"
  }'
```

## Build and Test

**Build the project:**
```bash
./mvnw clean compile
```

**Run tests:**
```bash
./mvnw test
```

**Package the application:**
```bash
./mvnw clean package
```

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/predictive_health_db
spring.datasource.username=${DB_USERNAME:your_username}
spring.datasource.password=${DB_PASSWORD:your_password}

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=your-secret-key
jwt.expiration=86400
```

## CI/CD Pipeline

This project includes a comprehensive CI/CD pipeline using GitHub Actions for automated building, testing, and deployment across multiple environments.

### Workflows

- **CI Build and Test**: Automatically runs on pull requests and pushes to validate code changes
- **Deploy to Dev**: Automatically deploys to development environment on pushes to `develop` branch
- **Deploy to Staging**: Deploys to staging environment on pushes to `release/*` branches
- **Deploy to Production**: Deploys to production on GitHub releases

### Environments

- **Development**: Automatic deployment from `develop` branch
- **Staging**: Deployment from `release/*` branches for pre-production testing
- **Production**: Deployment from GitHub releases with approval workflow

For detailed information about the CI/CD pipeline, see [.github/workflows/README.md](.github/workflows/README.md).

### Quick Start with CI/CD

1. **Development**: Push to `develop` branch to deploy to dev environment
2. **Staging**: Create a release branch (`release/v1.0.0`) to deploy to staging
3. **Production**: Create and publish a GitHub release to deploy to production

All workflows include:
- PostgreSQL database for testing
- Maven build and test execution
- Artifact generation and storage
- Deployment notifications

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request (triggers CI workflow automatically)

## License

This project is licensed under the MIT License - see the LICENSE file for details.