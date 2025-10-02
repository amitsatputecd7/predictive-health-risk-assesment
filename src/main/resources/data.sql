-- Sample data for testing the Health Risk Assessment API
-- This data will be loaded automatically when the application starts

INSERT INTO health_risk_assessments (
    age, sex, weight, height, bmi, hereditary_diseases, number_of_dependents, is_smoker, city, 
    blood_pressure, has_diabetes, regular_exercise, job_title, risk_score, risk_category,
    created_at, updated_at
) VALUES 
(35, 'MALE', 75.0, 1.75, 25.5, 'Hypertension, Diabetes', 2, false, 'New York', 'NORMAL', 'No', 'yes', 'Software Engineer', 25.0, 'LOW', NOW(), NOW()),
(45, 'FEMALE', 68.5, 1.68, 28.2, 'Heart Disease', 3, true, 'Los Angeles', 'HIGH', 'Yes', 'no', 'Marketing Manager', 75.0, 'HIGH', NOW(), NOW()),
(29, 'MALE', 65.2, 1.72, 22.1, '', 1, false, 'Chicago', 'NORMAL', 'No', 'yes', 'Teacher', 15.0, 'LOW', NOW(), NOW()),
(52, 'FEMALE', 80.0, 1.65, 32.5, 'Obesity, Diabetes', 4, false, 'Houston', 'HYPERTENSION_STAGE_1', 'Yes', 'no', 'Nurse', 80.0, 'HIGH', NOW(), NOW()),
(38, 'OTHER', 70.3, 1.73, 24.8, 'None', 0, false, 'Phoenix', 'NORMAL', 'No', 'yes', 'Graphic Designer', 20.0, 'LOW', NOW(), NOW());