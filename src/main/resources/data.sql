-- Sample data for testing the Health Risk Assessment API
-- This data will be loaded automatically when the application starts

INSERT INTO health_risk_assessments (
    age, sex, bmi, hereditary_diseases, number_of_dependents, is_smoker, city, 
    blood_pressure, has_diabetes, regular_exercise, job_title, risk_score, risk_category,
    created_at, updated_at
) VALUES 
(35, 'MALE', 25.5, 'Hypertension, Diabetes', 2, false, 'New York', 'NORMAL', false, true, 'Software Engineer', 25.0, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(45, 'FEMALE', 28.2, 'Heart Disease', 3, true, 'Los Angeles', 'HIGH', true, false, 'Marketing Manager', 75.0, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(29, 'MALE', 22.1, '', 1, false, 'Chicago', 'NORMAL', false, true, 'Teacher', 15.0, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(52, 'FEMALE', 32.5, 'Obesity, Diabetes', 4, false, 'Houston', 'HYPERTENSION_STAGE_1', true, false, 'Nurse', 80.0, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(38, 'OTHER', 24.8, 'None', 0, false, 'Phoenix', 'NORMAL', false, true, 'Graphic Designer', 20.0, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);