-- Migration script to separate PHI (Protected Health Information) into separate tables
-- This improves HIPAA compliance and data security

-- Note: This migration assumes a fresh installation or existing data will be migrated manually
-- For production deployments with existing data, see MIGRATION_GUIDE.md

-- Step 1: Create health_data table to store PHI
CREATE TABLE IF NOT EXISTS health_data (
    id BIGSERIAL PRIMARY KEY,
    age INTEGER NOT NULL CHECK (age >= 0 AND age <= 150),
    sex VARCHAR(10) NOT NULL CHECK (sex IN ('MALE', 'FEMALE', 'OTHER')),
    weight DOUBLE PRECISION NOT NULL CHECK (weight >= 1.0 AND weight <= 500.0),
    height DOUBLE PRECISION NOT NULL CHECK (height >= 0.5 AND height <= 3.0),
    bmi DOUBLE PRECISION NOT NULL CHECK (bmi >= 10.0 AND bmi <= 50.0),
    hereditary_diseases TEXT,
    number_of_dependents INTEGER NOT NULL CHECK (number_of_dependents >= 0),
    is_smoker BOOLEAN NOT NULL,
    city VARCHAR(100) NOT NULL,
    blood_pressure VARCHAR(30) NOT NULL CHECK (blood_pressure IN ('NORMAL', 'HIGH', 'LOW', 'HYPERTENSION_STAGE_1', 'HYPERTENSION_STAGE_2')),
    -- Note: has_diabetes and regular_exercise allow mixed case for backward compatibility with existing API
    has_diabetes VARCHAR(10) NOT NULL CHECK (has_diabetes IN ('Yes', 'No', 'yes', 'no')),
    regular_exercise VARCHAR(10) NOT NULL CHECK (regular_exercise IN ('Yes', 'No', 'yes', 'no')),
    job_title VARCHAR(100) NOT NULL,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_health_data_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Step 2: Create new health_risk_assessments table structure
-- For fresh installations, this creates the table with the new schema
CREATE TABLE IF NOT EXISTS health_risk_assessments (
    id BIGSERIAL PRIMARY KEY,
    risk_score DOUBLE PRECISION,
    risk_category VARCHAR(20) CHECK (risk_category IN ('LOW', 'MEDIUM', 'HIGH')),
    health_data_id BIGINT NOT NULL,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_health_risk_health_data_id FOREIGN KEY (health_data_id) REFERENCES health_data(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Step 3: Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_health_data_user_id ON health_data(user_id);
CREATE INDEX IF NOT EXISTS idx_health_data_city ON health_data(city);
CREATE INDEX IF NOT EXISTS idx_health_risk_assessments_user_id ON health_risk_assessments(user_id);
CREATE INDEX IF NOT EXISTS idx_health_risk_assessments_health_data_id ON health_risk_assessments(health_data_id);
CREATE INDEX IF NOT EXISTS idx_health_risk_assessments_risk_category ON health_risk_assessments(risk_category);

-- Note: For existing deployments with data, a separate migration process is required:
-- 1. Backup all data
-- 2. Create temporary tables
-- 3. Migrate data from old health_risk_assessments to new health_data
-- 4. Create corresponding health_risk_assessments records
-- 5. Verify data integrity
-- 6. Switch tables
-- See MIGRATION_GUIDE.md for detailed steps
