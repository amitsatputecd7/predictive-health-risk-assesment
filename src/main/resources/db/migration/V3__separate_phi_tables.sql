-- Migration script to separate PHI (Protected Health Information) into separate tables
-- This improves HIPAA compliance and data security

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
    has_diabetes VARCHAR(10) NOT NULL CHECK (has_diabetes IN ('Yes', 'No', 'yes', 'no')),
    regular_exercise VARCHAR(10) NOT NULL CHECK (regular_exercise IN ('Yes', 'No', 'yes', 'no')),
    job_title VARCHAR(100) NOT NULL,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_health_data_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Step 2: Migrate existing data from health_risk_assessments to health_data
-- (If there is existing data in health_risk_assessments table)
-- INSERT INTO health_data (age, sex, weight, height, bmi, hereditary_diseases, 
--     number_of_dependents, is_smoker, city, blood_pressure, has_diabetes, 
--     regular_exercise, job_title, user_id, created_at, updated_at)
-- SELECT age, sex, weight, height, bmi, hereditary_diseases, 
--     number_of_dependents, is_smoker, city, blood_pressure, has_diabetes, 
--     regular_exercise, job_title, user_id, created_at, updated_at
-- FROM health_risk_assessments;

-- Step 3: Create new health_risk_assessments table structure
-- Drop the old table and recreate with new schema (only if migrating from old structure)
-- DROP TABLE IF EXISTS health_risk_assessments CASCADE;

CREATE TABLE IF NOT EXISTS health_risk_assessments_new (
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

-- Step 4: Create indexes for better query performance
CREATE INDEX idx_health_data_user_id ON health_data(user_id);
CREATE INDEX idx_health_data_city ON health_data(city);
CREATE INDEX idx_health_risk_assessments_user_id ON health_risk_assessments_new(user_id);
CREATE INDEX idx_health_risk_assessments_health_data_id ON health_risk_assessments_new(health_data_id);
CREATE INDEX idx_health_risk_assessments_risk_category ON health_risk_assessments_new(risk_category);

-- Note: The actual migration requires careful planning based on existing data
-- This script provides the schema structure for the separated tables
-- Production migration should include:
-- 1. Backup of existing data
-- 2. Data migration from old to new structure
-- 3. Verification of data integrity
-- 4. Rename health_risk_assessments_new to health_risk_assessments after migration
