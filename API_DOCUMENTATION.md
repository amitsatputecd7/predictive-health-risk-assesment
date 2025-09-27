# Predictive Health Risk Assessment API

A REST API for assessing health risks based on personal and lifestyle factors.

## Features

- Create, read, update, and delete health risk assessments
- Automatic risk score calculation (0-100)
- Risk categorization (LOW, MEDIUM, HIGH)
- Query assessments by city, risk category, and other filters
- Input validation and error handling
- H2 in-memory database for development
- Sample data for testing

## API Endpoints

### Base URL
```
http://localhost:8080/api/v1/health-risk-assessment
```

### 1. Create Health Risk Assessment
**POST** `/api/v1/health-risk-assessment`

**Request Body:**
```json
{
  "age": 35,
  "sex": "MALE",
  "bmi": 25.5,
  "hereditaryDiseases": "Hypertension, Diabetes",
  "numberOfDependents": 2,
  "isSmoker": false,
  "city": "New York",
  "bloodPressure": "NORMAL",
  "hasDiabetes": false,
  "regularExercise": true,
  "jobTitle": "Software Engineer"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "age": 35,
  "sex": "MALE",
  "bmi": 25.5,
  "hereditaryDiseases": "Hypertension, Diabetes",
  "numberOfDependents": 2,
  "isSmoker": false,
  "city": "New York",
  "bloodPressure": "NORMAL",
  "hasDiabetes": false,
  "regularExercise": true,
  "jobTitle": "Software Engineer",
  "riskScore": 25.0,
  "riskCategory": "LOW",
  "createdAt": "2025-09-27T10:30:00",
  "updatedAt": "2025-09-27T10:30:00"
}
```

### 2. Get All Assessments
**GET** `/api/v1/health-risk-assessment`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "age": 35,
    "sex": "MALE",
    // ... other fields
    "riskScore": 25.0,
    "riskCategory": "LOW"
  }
]
```

### 3. Get Assessment by ID
**GET** `/api/v1/health-risk-assessment/{id}`

**Response (200 OK):** Same as single assessment object
**Response (404 Not Found):** If assessment doesn't exist

### 4. Update Assessment
**PUT** `/api/v1/health-risk-assessment/{id}`

**Request Body:** Same as create request
**Response (200 OK):** Updated assessment object
**Response (404 Not Found):** If assessment doesn't exist

### 5. Delete Assessment
**DELETE** `/api/v1/health-risk-assessment/{id}`

**Response (204 No Content):** Successfully deleted
**Response (404 Not Found):** If assessment doesn't exist

### 6. Get Assessments by City
**GET** `/api/v1/health-risk-assessment/city/{city}`

### 7. Get Assessments by Risk Category
**GET** `/api/v1/health-risk-assessment/risk-category/{riskCategory}`

Categories: `LOW`, `MEDIUM`, `HIGH`

### 8. Get High-Risk Assessments
**GET** `/api/v1/health-risk-assessment/high-risk`

Returns assessments with risk score > 70

### 9. Health Check
**GET** `/api/v1/health-risk-assessment/health`

**Response:** `"Health Risk Assessment API is running!"`

## Field Validations

| Field | Type | Validation Rules |
|-------|------|------------------|
| age | Integer | Required, 0-150 |
| sex | String | Required, MALE/FEMALE/OTHER |
| bmi | Double | Required, 10.0-50.0 |
| hereditaryDiseases | String | Optional |
| numberOfDependents | Integer | Required, ≥ 0 |
| isSmoker | Boolean | Required |
| city | String | Required, max 100 chars |
| bloodPressure | String | Required, NORMAL/HIGH/LOW/HYPERTENSION_STAGE_1/HYPERTENSION_STAGE_2 |
| hasDiabetes | Boolean | Required |
| regularExercise | Boolean | Required |
| jobTitle | String | Required, max 100 chars |

## Risk Score Calculation

The system calculates a risk score (0-100) based on:

- **Age (0-25 points)**
  - < 30: 5 points
  - 30-49: 10 points
  - 50-64: 20 points
  - ≥ 65: 25 points

- **BMI (0-20 points)**
  - < 18.5 or > 30: 20 points
  - 25-30: 10 points
  - 18.5-25: 5 points

- **Smoking (0-25 points)**
  - Smoker: 25 points
  - Non-smoker: 0 points

- **Diabetes (0-20 points)**
  - Has diabetes: 20 points
  - No diabetes: 0 points

- **Blood Pressure (0-15 points)**
  - HYPERTENSION_STAGE_2: 15 points
  - HYPERTENSION_STAGE_1: 12 points
  - HIGH: 8 points
  - LOW: 5 points
  - NORMAL: 2 points

- **Exercise (0-10 points)**
  - No regular exercise: 10 points
  - Regular exercise: 0 points

- **Hereditary Diseases (0-10 points)**
  - Has hereditary diseases: 10 points
  - No hereditary diseases: 0 points

- **Dependents (0-5 points)**
  - > 3 dependents: 5 points (stress factor)
  - ≤ 3 dependents: 0 points

## Risk Categories

- **LOW**: Risk score < 30
- **MEDIUM**: Risk score 30-69
- **HIGH**: Risk score ≥ 70

## Error Responses

**Validation Error (400 Bad Request):**
```json
{
  "timestamp": "2025-09-27T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input parameters",
  "path": "/api/v1/health-risk-assessment",
  "validationErrors": {
    "age": "Age is required",
    "bmi": "BMI must be at least 10.0"
  }
}
```

**Not Found Error (404 Not Found):**
```json
{
  "timestamp": "2025-09-27T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Assessment not found with id: 999",
  "path": "/api/v1/health-risk-assessment/999"
}
```

## Development Tools

### H2 Database Console
Access the database console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:healthrisk`
- Username: `sa`
- Password: (empty)

### Sample cURL Commands

**Create Assessment:**
```bash
curl -X POST http://localhost:8080/api/v1/health-risk-assessment \
  -H "Content-Type: application/json" \
  -d '{
    "age": 35,
    "sex": "MALE",
    "bmi": 25.5,
    "hereditaryDiseases": "Hypertension",
    "numberOfDependents": 2,
    "isSmoker": false,
    "city": "New York",
    "bloodPressure": "NORMAL",
    "hasDiabetes": false,
    "regularExercise": true,
    "jobTitle": "Software Engineer"
  }'
```

**Get All Assessments:**
```bash
curl http://localhost:8080/api/v1/health-risk-assessment
```

**Get High-Risk Assessments:**
```bash
curl http://localhost:8080/api/v1/health-risk-assessment/high-risk
```

## Running the Application

1. Ensure Java 21 is installed
2. Run: `mvn spring-boot:run`
3. The API will be available at `http://localhost:8080`
4. Sample data will be automatically loaded for testing