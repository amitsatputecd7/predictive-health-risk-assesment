# Health Risk Assessment API Documentation

## Overview
The Health Risk Assessment API is a Spring Boot application that provides endpoints for managing health risk assessments. It calculates risk scores based on various health and demographic factors.

## Base Configuration
- **Base URL**: `http://localhost:8080`
- **API Base Path**: `/api/v1/health-risk-assessment`
- **Port**: 8080
- **Database**: H2 (In-memory)
- **JSON Property Naming**: SNAKE_CASE

## CORS Configuration
The API is configured with CORS support for frontend integration:

### Current Configuration (Development)
```java
@CrossOrigin(origins = "*") // Allows all origins
```

### Production Configuration (Recommended)
```java
@CrossOrigin(origins = {"http://localhost:3000", "https://yourdomain.com"})
```

### Global CORS Configuration
For better control, a `CorsConfig` class has been created with the following settings:
- **Allowed Origins**: localhost:3000, localhost:3001, localhost:4200, yourdomain.com
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Allowed Headers**: All (*)
- **Credentials**: Enabled
- **Max Age**: 3600 seconds

## API Endpoints

### 1. Health Check
- **Endpoint**: `GET /api/v1/health-risk-assessment/health`
- **Description**: Check if the API is running
- **Response**: `"Health Risk Assessment API is running!"`

**Example:**
```bash
curl -X GET http://localhost:8080/api/v1/health-risk-assessment/health
```

### 2. Create Assessment
- **Endpoint**: `POST /api/v1/health-risk-assessment`
- **Description**: Create a new health risk assessment
- **Request Body**: HealthRiskAssessmentRequest (JSON)
- **Response**: HealthRiskAssessmentResponse with calculated risk score

**Request Format (snake_case):**
```json
{
  "age": 30,
  "sex": "MALE",
  "bmi": 25.5,
  "hereditary_diseases": "None",
  "number_of_dependents": 2,
  "is_smoker": false,
  "city": "Mumbai",
  "blood_pressure": "NORMAL",
  "has_diabetes": false,
  "regular_exercise": true,
  "job_title": "Software Engineer"
}
```

**Response Example:**
```json
{
  "id": 1,
  "age": 30,
  "sex": "MALE",
  "bmi": 25.5,
  "hereditary_diseases": "None",
  "number_of_dependents": 2,
  "is_smoker": false,
  "city": "Mumbai",
  "blood_pressure": "NORMAL",
  "has_diabetes": false,
  "regular_exercise": true,
  "job_title": "Software Engineer",
  "risk_score": 32.0,
  "risk_category": "MEDIUM",
  "created_at": "2025-09-27T10:54:23.828296",
  "updated_at": "2025-09-27T10:54:23.828333"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/health-risk-assessment \
  -H "Content-Type: application/json" \
  -d '{
    "age": 30,
    "sex": "MALE",
    "bmi": 25.5,
    "hereditary_diseases": "None",
    "number_of_dependents": 2,
    "is_smoker": false,
    "city": "Mumbai",
    "blood_pressure": "NORMAL",
    "has_diabetes": false,
    "regular_exercise": true,
    "job_title": "Software Engineer"
  }'
```

### 3. Get All Assessments
- **Endpoint**: `GET /api/v1/health-risk-assessment`
- **Description**: Retrieve all health risk assessments
- **Response**: Array of HealthRiskAssessmentResponse

**Example:**
```bash
curl -X GET http://localhost:8080/api/v1/health-risk-assessment
```

### 4. Get Assessment by ID
- **Endpoint**: `GET /api/v1/health-risk-assessment/{id}`
- **Description**: Retrieve a specific assessment by ID
- **Path Parameter**: `id` (Long)
- **Response**: HealthRiskAssessmentResponse or 404 if not found

**Example:**
```bash
curl -X GET http://localhost:8080/api/v1/health-risk-assessment/1
```

### 5. Update Assessment
- **Endpoint**: `PUT /api/v1/health-risk-assessment/{id}`
- **Description**: Update an existing assessment
- **Path Parameter**: `id` (Long)
- **Request Body**: HealthRiskAssessmentRequest (JSON)
- **Response**: Updated HealthRiskAssessmentResponse or 404 if not found

**Example:**
```bash
curl -X PUT http://localhost:8080/api/v1/health-risk-assessment/1 \
  -H "Content-Type: application/json" \
  -d '{
    "age": 31,
    "sex": "MALE",
    "bmi": 24.8,
    "hereditary_diseases": "None",
    "number_of_dependents": 2,
    "is_smoker": false,
    "city": "Mumbai",
    "blood_pressure": "NORMAL",
    "has_diabetes": false,
    "regular_exercise": true,
    "job_title": "Senior Software Engineer"
  }'
```

### 6. Delete Assessment
- **Endpoint**: `DELETE /api/v1/health-risk-assessment/{id}`
- **Description**: Delete an assessment by ID
- **Path Parameter**: `id` (Long)
- **Response**: 204 No Content or 404 if not found

**Example:**
```bash
curl -X DELETE http://localhost:8080/api/v1/health-risk-assessment/1
```

### 7. Get Assessments by City
- **Endpoint**: `GET /api/v1/health-risk-assessment/city/{city}`
- **Description**: Retrieve assessments for a specific city
- **Path Parameter**: `city` (String)
- **Response**: Array of HealthRiskAssessmentResponse

**Example:**
```bash
curl -X GET http://localhost:8080/api/v1/health-risk-assessment/city/Mumbai
```

### 8. Get Assessments by Risk Category
- **Endpoint**: `GET /api/v1/health-risk-assessment/risk-category/{riskCategory}`
- **Description**: Retrieve assessments for a specific risk category
- **Path Parameter**: `riskCategory` (String - LOW, MEDIUM, HIGH)
- **Response**: Array of HealthRiskAssessmentResponse

**Example:**
```bash
curl -X GET http://localhost:8080/api/v1/health-risk-assessment/risk-category/HIGH
```

### 9. Get High-Risk Assessments
- **Endpoint**: `GET /api/v1/health-risk-assessment/high-risk`
- **Description**: Retrieve all high-risk assessments
- **Response**: Array of HealthRiskAssessmentResponse

**Example:**
```bash
curl -X GET http://localhost:8080/api/v1/health-risk-assessment/high-risk
```

## Request Validation Rules

### Required Fields with Constraints:
- **age**: Integer, required, 0-150
- **sex**: String, required, must be "MALE", "FEMALE", or "OTHER"
- **bmi**: Double, required, 10.0-50.0
- **number_of_dependents**: Integer, required, >= 0
- **is_smoker**: Boolean, required
- **city**: String, required, max 100 characters
- **blood_pressure**: String, required, must be "NORMAL", "HIGH", "LOW", "HYPERTENSION_STAGE_1", or "HYPERTENSION_STAGE_2"
- **has_diabetes**: Boolean, required
- **regular_exercise**: Boolean, required
- **job_title**: String, required, max 100 characters

### Optional Fields:
- **hereditary_diseases**: String, optional

## Field Name Mapping (camelCase → snake_case)
Due to `spring.jackson.property-naming-strategy=SNAKE_CASE` configuration:

| Java Field (camelCase) | JSON Field (snake_case) |
|------------------------|-------------------------|
| hereditaryDiseases     | hereditary_diseases     |
| numberOfDependents     | number_of_dependents    |
| isSmoker              | is_smoker               |
| bloodPressure         | blood_pressure          |
| hasDiabetes           | has_diabetes            |
| regularExercise       | regular_exercise        |
| jobTitle              | job_title               |
| riskScore             | risk_score              |
| riskCategory          | risk_category           |
| createdAt             | created_at              |
| updatedAt             | updated_at              |

## Frontend Integration Examples

### React/JavaScript with Fetch API
```javascript
// API base URL
const API_BASE_URL = 'http://localhost:8080/api/v1/health-risk-assessment';

// Create assessment
const createAssessment = async (assessmentData) => {
  try {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        age: assessmentData.age,
        sex: assessmentData.sex,
        bmi: assessmentData.bmi,
        hereditary_diseases: assessmentData.hereditaryDiseases,
        number_of_dependents: assessmentData.numberOfDependents,
        is_smoker: assessmentData.isSmoker,
        city: assessmentData.city,
        blood_pressure: assessmentData.bloodPressure,
        has_diabetes: assessmentData.hasDiabetes,
        regular_exercise: assessmentData.regularExercise,
        job_title: assessmentData.jobTitle
      })
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Error creating assessment:', error);
    throw error;
  }
};

// Get all assessments
const getAllAssessments = async () => {
  try {
    const response = await fetch(API_BASE_URL);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching assessments:', error);
    throw error;
  }
};

// Get assessment by ID
const getAssessmentById = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/${id}`);
    if (!response.ok) {
      if (response.status === 404) {
        return null;
      }
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Error fetching assessment:', error);
    throw error;
  }
};

// Update assessment
const updateAssessment = async (id, assessmentData) => {
  try {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        age: assessmentData.age,
        sex: assessmentData.sex,
        bmi: assessmentData.bmi,
        hereditary_diseases: assessmentData.hereditaryDiseases,
        number_of_dependents: assessmentData.numberOfDependents,
        is_smoker: assessmentData.isSmoker,
        city: assessmentData.city,
        blood_pressure: assessmentData.bloodPressure,
        has_diabetes: assessmentData.hasDiabetes,
        regular_exercise: assessmentData.regularExercise,
        job_title: assessmentData.jobTitle
      })
    });
    
    if (!response.ok) {
      if (response.status === 404) {
        return null;
      }
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error updating assessment:', error);
    throw error;
  }
};

// Delete assessment
const deleteAssessment = async (id) => {
  try {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'DELETE',
    });
    
    if (!response.ok) {
      if (response.status === 404) {
        return false;
      }
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return true;
  } catch (error) {
    console.error('Error deleting assessment:', error);
    throw error;
  }
};
```

### Vue.js with Axios
```javascript
import axios from 'axios';

// Create axios instance
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1/health-risk-assessment',
  headers: {
    'Content-Type': 'application/json',
  }
});

// API service
export const healthRiskAssessmentService = {
  // Create assessment
  async createAssessment(data) {
    const response = await apiClient.post('/', {
      age: data.age,
      sex: data.sex,
      bmi: data.bmi,
      hereditary_diseases: data.hereditaryDiseases,
      number_of_dependents: data.numberOfDependents,
      is_smoker: data.isSmoker,
      city: data.city,
      blood_pressure: data.bloodPressure,
      has_diabetes: data.hasDiabetes,
      regular_exercise: data.regularExercise,
      job_title: data.jobTitle
    });
    return response.data;
  },

  // Get all assessments
  async getAllAssessments() {
    const response = await apiClient.get('/');
    return response.data;
  },

  // Get assessment by ID
  async getAssessmentById(id) {
    try {
      const response = await apiClient.get(`/${id}`);
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        return null;
      }
      throw error;
    }
  },

  // Update assessment
  async updateAssessment(id, data) {
    try {
      const response = await apiClient.put(`/${id}`, {
        age: data.age,
        sex: data.sex,
        bmi: data.bmi,
        hereditary_diseases: data.hereditaryDiseases,
        number_of_dependents: data.numberOfDependents,
        is_smoker: data.isSmoker,
        city: data.city,
        blood_pressure: data.bloodPressure,
        has_diabetes: data.hasDiabetes,
        regular_exercise: data.regularExercise,
        job_title: data.jobTitle
      });
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        return null;
      }
      throw error;
    }
  },

  // Delete assessment
  async deleteAssessment(id) {
    try {
      await apiClient.delete(`/${id}`);
      return true;
    } catch (error) {
      if (error.response?.status === 404) {
        return false;
      }
      throw error;
    }
  },

  // Get assessments by city
  async getAssessmentsByCity(city) {
    const response = await apiClient.get(`/city/${encodeURIComponent(city)}`);
    return response.data;
  },

  // Get assessments by risk category
  async getAssessmentsByRiskCategory(riskCategory) {
    const response = await apiClient.get(`/risk-category/${riskCategory}`);
    return response.data;
  },

  // Get high-risk assessments
  async getHighRiskAssessments() {
    const response = await apiClient.get('/high-risk');
    return response.data;
  }
};
```

### Angular TypeScript Service
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface HealthRiskAssessmentRequest {
  age: number;
  sex: string;
  bmi: number;
  hereditaryDiseases?: string;
  numberOfDependents: number;
  isSmoker: boolean;
  city: string;
  bloodPressure: string;
  hasDiabetes: boolean;
  regularExercise: boolean;
  jobTitle: string;
}

export interface HealthRiskAssessmentResponse {
  id: number;
  age: number;
  sex: string;
  bmi: number;
  hereditary_diseases?: string;
  number_of_dependents: number;
  is_smoker: boolean;
  city: string;
  blood_pressure: string;
  has_diabetes: boolean;
  regular_exercise: boolean;
  job_title: string;
  risk_score: number;
  risk_category: string;
  created_at: string;
  updated_at: string;
}

@Injectable({
  providedIn: 'root'
})
export class HealthRiskAssessmentService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/health-risk-assessment';

  constructor(private http: HttpClient) {}

  // Convert frontend model to API model
  private toApiModel(data: HealthRiskAssessmentRequest): any {
    return {
      age: data.age,
      sex: data.sex,
      bmi: data.bmi,
      hereditary_diseases: data.hereditaryDiseases,
      number_of_dependents: data.numberOfDependents,
      is_smoker: data.isSmoker,
      city: data.city,
      blood_pressure: data.bloodPressure,
      has_diabetes: data.hasDiabetes,
      regular_exercise: data.regularExercise,
      job_title: data.jobTitle
    };
  }

  createAssessment(data: HealthRiskAssessmentRequest): Observable<HealthRiskAssessmentResponse> {
    return this.http.post<HealthRiskAssessmentResponse>(this.apiUrl, this.toApiModel(data))
      .pipe(catchError(this.handleError));
  }

  getAllAssessments(): Observable<HealthRiskAssessmentResponse[]> {
    return this.http.get<HealthRiskAssessmentResponse[]>(this.apiUrl)
      .pipe(catchError(this.handleError));
  }

  getAssessmentById(id: number): Observable<HealthRiskAssessmentResponse> {
    return this.http.get<HealthRiskAssessmentResponse>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  updateAssessment(id: number, data: HealthRiskAssessmentRequest): Observable<HealthRiskAssessmentResponse> {
    return this.http.put<HealthRiskAssessmentResponse>(`${this.apiUrl}/${id}`, this.toApiModel(data))
      .pipe(catchError(this.handleError));
  }

  deleteAssessment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  getAssessmentsByCity(city: string): Observable<HealthRiskAssessmentResponse[]> {
    return this.http.get<HealthRiskAssessmentResponse[]>(`${this.apiUrl}/city/${encodeURIComponent(city)}`)
      .pipe(catchError(this.handleError));
  }

  getAssessmentsByRiskCategory(riskCategory: string): Observable<HealthRiskAssessmentResponse[]> {
    return this.http.get<HealthRiskAssessmentResponse[]>(`${this.apiUrl}/risk-category/${riskCategory}`)
      .pipe(catchError(this.handleError));
  }

  getHighRiskAssessments(): Observable<HealthRiskAssessmentResponse[]> {
    return this.http.get<HealthRiskAssessmentResponse[]>(`${this.apiUrl}/high-risk`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    return throwError(() => new Error(errorMessage));
  }
}
```

## Error Handling

### Common HTTP Status Codes:
- **200 OK**: Successful GET request
- **201 Created**: Successful POST request
- **204 No Content**: Successful DELETE request
- **400 Bad Request**: Validation errors
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

### Validation Error Response Format:
```json
{
  "timestamp": "2025-09-27T10:53:18.944654",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input parameters",
  "path": "/api/v1/health-risk-assessment",
  "validation_errors": {
    "bloodPressure": "Blood pressure status is required",
    "numberOfDependents": "Number of dependents is required",
    "isSmoker": "Smoker status is required"
  }
}
```

## Database Configuration (H2)
- **Console URL**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:healthrisk
- **Username**: sa
- **Password**: (empty)

## Security Considerations for Production
1. **CORS**: Restrict origins to specific domains
2. **Authentication**: Implement JWT or OAuth2
3. **Validation**: Sanitize input data
4. **Rate Limiting**: Implement API rate limiting
5. **HTTPS**: Use SSL/TLS in production
6. **Database**: Use production database (PostgreSQL, MySQL)

## Testing the API
Use the provided test request file:
```bash
# Create test-request.json with snake_case fields
curl -X POST http://localhost:8080/api/v1/health-risk-assessment \
  -H "Content-Type: application/json" \
  -d @test-request.json
```

## Contact & Support
For any issues or questions regarding the API, please refer to the source code or contact the development team.