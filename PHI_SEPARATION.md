# PHI Separation Implementation

## Overview
This document describes the implementation of separate tables for Protected Health Information (PHI) to improve HIPAA compliance and data security.

## Problem Statement
Previously, all health data including PHI was stored in a single `health_risk_assessments` table. This approach:
- Mixed sensitive PHI with non-sensitive risk assessment data
- Made it difficult to apply different security policies to PHI vs non-PHI data
- Did not follow healthcare industry best practices for data separation

## Solution
Created two separate tables:

### 1. `health_data` Table (PHI)
Stores all Protected Health Information:
- Patient demographics (age, sex)
- Physical measurements (weight, height, BMI)
- Health conditions (diabetes, blood pressure)
- Lifestyle factors (smoking status, exercise habits)
- Personal information (city, job title, dependents)
- Hereditary diseases

**Note on Data Types**: The `has_diabetes` and `regular_exercise` fields use VARCHAR with mixed case values ('Yes'/'No' and 'yes'/'no') for backward compatibility with the existing API. While BOOLEAN would be more efficient, maintaining the current format ensures no breaking changes for API consumers.

### 2. `health_risk_assessments` Table (Non-PHI)
Stores only risk assessment results:
- Risk score (calculated value 0-100)
- Risk category (LOW, MEDIUM, HIGH)
- Timestamps (created_at, updated_at)
- Relationships (health_data_id, user_id)

## Database Schema

```
┌──────────────────┐
│     users        │
│                  │
│  id (PK)         │
│  email           │
│  username        │
│  password        │
│  ...             │
└─────┬────────────┘
      │
      │ 1
      │
      ├─────────────────────────────┐
      │                             │
      │ N                           │ N
┌─────▼────────────┐         ┌──────▼───────────────────┐
│   health_data    │    1:1  │ health_risk_assessments  │
│                  │◄────────┤                          │
│  id (PK)         │         │  id (PK)                 │
│  age             │         │  risk_score              │
│  sex             │         │  risk_category           │
│  weight          │         │  health_data_id (FK)     │
│  height          │         │  user_id (FK)            │
│  bmi             │         │  created_at              │
│  blood_pressure  │         │  updated_at              │
│  has_diabetes    │         └──────────────────────────┘
│  is_smoker       │
│  city            │
│  job_title       │
│  ...             │
│  user_id (FK)    │
│  created_at      │
│  updated_at      │
└──────────────────┘

Relationships:
- One User can have many HealthData records (1:N)
- One User can have many HealthRiskAssessments (1:N)
- One HealthData has exactly one HealthRiskAssessment (1:1)
- One HealthRiskAssessment references exactly one HealthData (1:1)
```

## Benefits

### 1. **Enhanced Security**
- PHI data can be encrypted at rest separately from risk assessment data
- Different access control policies can be applied to each table
- Audit logging can focus specifically on PHI access

### 2. **HIPAA Compliance**
- Clear separation of PHI from non-PHI data
- Easier to implement minimum necessary access rules
- Simplified data retention policies

### 3. **Performance**
- Risk assessment queries don't need to access PHI
- Smaller tables improve query performance
- Better indexing strategies for each data type

### 4. **Data Management**
- Easier to anonymize data for research
- Simpler data export/import processes
- Better support for data archival

## Code Changes

### Entity Classes
1. **Created `HealthData.java`**: New entity for storing PHI
2. **Refactored `HealthRiskAssessment.java`**: Now stores only risk scores and categories
3. **Updated `User.java`**: Added relationship to both entities

### Repository Classes
1. **Created `HealthDataRepository.java`**: Repository for managing health data
2. **Updated `HealthRiskAssessmentRepository.java`**: Removed PHI-related queries

### Service Layer
1. **Updated `HealthRiskAssessmentService.java`**: 
   - Creates both `HealthData` and `HealthRiskAssessment` entities
   - Maintains one-to-one relationship between them
   - Risk score calculation now reads from `HealthData`

### API Endpoints
No changes to API endpoints - the separation is transparent to API consumers:
- **POST** `/api/v1/health-risk-assessment` - Still accepts the same request format
- **GET** `/api/v1/health-risk-assessment` - Returns data from both tables
- **GET** `/api/v1/health-risk-assessment/{id}` - Returns combined view
- **PUT** `/api/v1/health-risk-assessment/{id}` - Updates both tables
- **DELETE** `/api/v1/health-risk-assessment/{id}` - Cascades to health_data

## Migration Strategy

### For New Deployments
The application will automatically create both tables using JPA's `ddl-auto=update` setting.

### For Existing Deployments
1. **Backup existing data**
2. **Run migration script** (`V3__separate_phi_tables.sql`)
3. **Verify data integrity**
4. **Update application code**
5. **Test thoroughly**

## Security Recommendations

### 1. Database Level
- Use separate database users with different permissions for PHI vs non-PHI data
- Enable transparent data encryption (TDE) for the `health_data` table
- Implement row-level security if supported by your database

### 2. Application Level
- Implement field-level encryption for highly sensitive PHI fields
- Use separate microservices for PHI and non-PHI data access
- Add audit logging for all PHI access

### 3. Infrastructure Level
- Store PHI data on encrypted volumes
- Use separate backup schedules for PHI data
- Implement data masking for non-production environments

## Testing
- All existing API tests should pass without modification
- The separation is transparent to API consumers
- Risk score calculations remain accurate

## Rollback Plan
If issues arise:
1. Keep the old schema in `health_risk_assessments` table
2. Maintain backward compatibility
3. Use feature flags to toggle between old and new implementations

## Future Enhancements
1. **Encryption**: Implement field-level encryption for sensitive PHI fields
2. **Auditing**: Add comprehensive audit logging for PHI access
3. **Access Control**: Implement role-based access control (RBAC) for PHI data
4. **Anonymization**: Create tools for de-identifying PHI for research purposes
5. **Data Retention**: Implement automated data retention policies

## Compliance Notes
This implementation improves HIPAA compliance by:
- Separating PHI from non-PHI data ✓
- Enabling granular access controls ✓
- Supporting minimum necessary access principle ✓
- Facilitating secure data disposal ✓
- Enabling better audit logging ✓

However, additional measures are still required for full HIPAA compliance:
- Encryption at rest and in transit
- Comprehensive audit logging
- Business Associate Agreements (BAA)
- Regular security assessments
- Incident response procedures

## Contact
For questions or concerns about this implementation, please contact the development team.
