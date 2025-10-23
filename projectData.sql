CREATE DATABASE IF NOT EXISTS `prescription_system` DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
USE `prescription_system`;

    -- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS prescription_items;
DROP TABLE IF EXISTS prescriptions;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS pharmacists;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;

-- Create tables in order of dependencies
CREATE TABLE doctors ( id VARCHAR(10) PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       password VARCHAR(50) NOT NULL,
                       specialty VARCHAR(50) NOT NULL
);

CREATE TABLE patients ( id VARCHAR(10) PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        password VARCHAR(50),
                        birth_date VARCHAR(20) NOT NULL,
                        phone_number VARCHAR(20) NOT NULL
);

CREATE TABLE pharmacists ( id VARCHAR(10) PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           password VARCHAR(50) NOT NULL,
                           shift VARCHAR(50) NOT NULL
);

CREATE TABLE medicines ( code VARCHAR(10) PRIMARY KEY,
                         name VARCHAR(100) NOT NULL,
                         presentation VARCHAR(50) NOT NULL
);

CREATE TABLE prescriptions ( id VARCHAR(20) PRIMARY KEY,
                             patient_id VARCHAR(10) NOT NULL,
                             doctor_id VARCHAR(10) NOT NULL,
                             creation_date DATETIME NOT NULL,
                             withdrawal_date DATETIME,
                             status VARCHAR(20) NOT NULL,
                             FOREIGN KEY (patient_id) REFERENCES patients(id),
                             FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

CREATE TABLE prescription_items ( prescription_id VARCHAR(20),
                                  medicine_code VARCHAR(10),
                                  quantity INT NOT NULL,
                                  instructions TEXT NOT NULL,
                                  duration_days INT NOT NULL,
                                  PRIMARY KEY (prescription_id, medicine_code),
                                  FOREIGN KEY (prescription_id) REFERENCES prescriptions(id),
                                  FOREIGN KEY (medicine_code) REFERENCES medicines(code)
);

-- Insertar Doctores
INSERT INTO doctors (id, name, password, specialty) VALUES
                                                        ('DOC-001', 'Jenna Klirk', '001', 'Surgery'),
                                                        ('DOC-002', 'Michael Carter', '002', 'Cardiology'),
                                                        ('DOC-003', 'Emily Turner', '003', 'Pediatrics'),
                                                        ('DOC-004', 'Christopher Hayes', '004', 'Neurology'),
                                                        ('DOC-005', 'Sarah Mitchell', '005', 'Dermatology'),
                                                        ('DOC-006', 'James Anderson', '006', 'Orthopedics'),
                                                        ('DOC-007', 'Olivia Scott', '007', 'Oncology'),
                                                        ('DOC-008', 'Daniel Harris', '008', 'Radiology'),
                                                        ('DOC-009', 'Amanda Collins', '009', 'Ophthalmology'),
                                                        ('DOC-010', 'Robert Wilson', '010', 'Gastroenterology'),
                                                        ('DOC-011', 'Hannah Rogers', '011', 'Gynecology'),
                                                        ('DOC-012', 'Matthew Brooks', '012', 'Psychiatry'),
                                                        ('DOC-013', 'Sophia Bennett', '013', 'Endocrinology'),
                                                        ('DOC-014', 'William Edwards', '014', 'Urology'),
                                                        ('DOC-015', 'Chloe Morgan', '015', 'Emergency Medicine');

-- Insertar Medicamentos
INSERT INTO medicines (code, name, presentation) VALUES
                                                     ('med01', 'Ibuprofen', 'Tablets'),
                                                     ('med02', 'Doxycycline', 'Capsules'),
                                                     ('med03', 'Paracetamol', 'Tablets'),
                                                     ('med04', 'Amoxicillin', 'Capsules'),
                                                     ('med05', 'Azithromycin', 'Tablets'),
                                                     ('med06', 'Ciprofloxacin', 'Tablets'),
                                                     ('med07', 'Fluconazole', 'Capsules'),
                                                     ('med08', 'Amlodipine', 'Tablets'),
                                                     ('med09', 'Losartan', 'Tablets'),
                                                     ('med10', 'Atorvastatin', 'Tablets'),
                                                     ('med11', 'Metformin', 'Tablets'),
                                                     ('med12', 'Insulin', 'Injection'),
                                                     ('med13', 'Omeprazole', 'Capsules'),
                                                     ('med14', 'Ondansetron', 'Tablets'),
                                                     ('med15', 'Sertraline', 'Tablets'),
                                                     ('med16', 'Fluoxetine', 'Capsules'),
                                                     ('med17', 'Diazepam', 'Tablets'),
                                                     ('med18', 'Lorazepam', 'Tablets'),
                                                     ('med19', 'Carbamazepine', 'Tablets'),
                                                     ('med20', 'Salbutamol', 'Inhaler'),
                                                     ('med21', 'Budesonide', 'Inhaler'),
                                                     ('med22', 'Montelukast', 'Tablets'),
                                                     ('med23', 'Prednisone', 'Tablets'),
                                                     ('med24', 'Levothyroxine', 'Tablets'),
                                                     ('med25', 'Warfarin', 'Tablets'),
                                                     ('med26', 'Heparin', 'Injection'),
                                                     ('med27', 'Epinephrine', 'Auto-injector'),
                                                     ('med28', 'Naproxen', 'Tablets'),
                                                     ('med29', 'Clotrimazole', 'Cream'),
                                                     ('med30', 'Ranitidine', 'Tablets');

-- Insertar Pacientes
INSERT INTO patients (id, name, password, birth_date, phone_number) VALUES
                                                                        ('PAT-001', 'Chris Evans', '001', '13-06-1981', '8475944758'),
                                                                        ('PAT-002', 'Scarlett Johansson', '002', '22-11-1984', '3128459675'),
                                                                        ('PAT-003', 'Robert Downey Jr.', '003', '04-04-1965', '2134987541'),
                                                                        ('PAT-004', 'Jennifer Lawrence', '004', '15-08-1990', '6467581290'),
                                                                        ('PAT-005', 'Leonardo DiCaprio', '005', '11-11-1974', '9175648392'),
                                                                        ('PAT-006', 'Emma Watson', '006', '15-04-1990', '3472891045'),
                                                                        ('PAT-007', 'Tom Holland', '007', '01-06-1996', '7185930472'),
                                                                        ('PAT-008', 'Zendaya Coleman', '008', '01-09-1996', '4245938720'),
                                                                        ('PAT-009', 'Johnny Depp', '009', '09-06-1963', '2138495723'),
                                                                        ('PAT-010', 'Angelina Jolie', '010', '04-06-1975', '3107482951'),
                                                                        ('PAT-011', 'Brad Pitt', '011', '18-12-1963', '2136758492'),
                                                                        ('PAT-012', 'Margot Robbie', '012', '02-07-1990', '6462098573'),
                                                                        ('PAT-013', 'Ryan Reynolds', '013', '23-10-1976', '9172039845'),
                                                                        ('PAT-014', 'Blake Lively', '014', '25-08-1987', '2125738495'),
                                                                        ('PAT-015', 'Will Smith', '015', '25-09-1968', '3237482095'),
                                                                        ('PAT-016', 'Jada Pinkett Smith', '016', '18-09-1971', '3104928573'),
                                                                        ('PAT-017', 'Dwayne Johnson', '017', '02-05-1972', '3057482930'),
                                                                        ('PAT-018', 'Kevin Hart', '018', '06-07-1979', '2678492039'),
                                                                        ('PAT-019', 'Gal Gadot', '019', '30-04-1985', '6467582039'),
                                                                        ('PAT-020', 'Henry Cavill', '020', '05-05-1983', '9173849205'),
                                                                        ('PAT-021', 'Tom Cruise', '021', '03-07-1962', '3104958203'),
                                                                        ('PAT-022', 'Keanu Reeves', '022', '02-09-1964', '2137485093'),
                                                                        ('PAT-023', 'Anne Hathaway', '023', '12-11-1982', '6462839475'),
                                                                        ('PAT-024', 'Natalie Portman', '024', '09-06-1981', '9178492057'),
                                                                        ('PAT-025', 'Chris Hemsworth', '025', '11-08-1983', '4248573940'),
                                                                        ('PAT-026', 'Liam Hemsworth', '026', '13-01-1990', '2139485720'),
                                                                        ('PAT-027', 'Mila Kunis', '027', '14-08-1983', '3109584720'),
                                                                        ('PAT-028', 'Ashton Kutcher', '028', '07-02-1978', '2129384750'),
                                                                        ('PAT-029', 'Sandra Bullock', '029', '26-07-1964', '3238574930'),
                                                                        ('PAT-030', 'Hugh Jackman', '030', '12-10-1968', '6467584930');

-- Insertar Farmacéuticos
INSERT INTO pharmacists (id, name, password, shift) VALUES
                                                        ('PHA-001', 'Daniel Solis', '1', 'Biopharmacy'),
                                                        ('PHA-002', 'Michael Smith', '002', 'Hospital Pharmacy'),
                                                        ('PHA-003', 'Jessica Miller', '003', 'Clinical Pharmacy'),
                                                        ('PHA-004', 'Christopher Davis', '004', 'Community Pharmacy'),
                                                        ('PHA-005', 'Ashley Wilson', '005', 'Industrial Pharmacy'),
                                                        ('PHA-006', 'Matthew Taylor', '006', 'Hospital Pharmacy'),
                                                        ('PHA-007', 'Amanda Brown', '007', 'Clinical Pharmacy'),
                                                        ('PHA-008', 'Joshua Anderson', '008', 'Research Pharmacy'),
                                                        ('PHA-009', 'Samantha Thomas', '009', 'Community Pharmacy'),
                                                        ('PHA-010', 'Daniel Martinez', '010', 'Industrial Pharmacy');

-- Insertar Prescripciones
INSERT INTO prescriptions (id, patient_id, doctor_id, creation_date, withdrawal_date, status) VALUES
                                                                                                  ('P1757692228866', 'PAT-007', 'DOC-002', '2025-09-12 09:50:28', '2025-09-17 00:00:00', 'Issued'),
                                                                                                  ('P1757704746451', 'PAT-005', 'DOC-001', '2025-09-12 13:19:06', '2025-09-15 00:00:00', 'Picked'),
                                                                                                  ('P1757889139998', 'PAT-024', 'DOC-005', '2025-09-14 16:32:20', '2025-09-17 00:00:00', 'Issued'),
                                                                                                  ('P1757889244328', 'PAT-010', 'DOC-006', '2025-09-14 16:34:04', '2025-09-17 00:00:00', 'Issued'),
                                                                                                  ('P1757889289464', 'PAT-015', 'DOC-006', '2025-09-14 16:34:49', '2025-09-17 00:00:00', 'Issued');

-- Insertar Items de Prescripción
INSERT INTO prescription_items (prescription_id, medicine_code, quantity, instructions, duration_days) VALUES
-- Prescripción P1757692228866
('P1757692228866', 'med06', 2, '1 at morning, 1 at night', 1),
('P1757692228866', 'med27', 1, 'Only 1, in the last day of treatment', 1),
-- Prescripción P1757704746451
('P1757704746451', 'med04', 6, '1 each 8 hrs', 3),
-- Prescripción P1757889139998
('P1757889139998', 'med01', 3, '2 each 8 hrs', 8),
('P1757889139998', 'med29', 1, 'apply on the afected zone', 8),
('P1757889139998', 'med07', 4, '1 each 4 days', 12),
-- Prescripción P1757889244328
('P1757889244328', 'med01', 4, '1 each 8 hrs', 6),
('P1757889244328', 'med17', 1, '1 eahc 12 hrs', 4),
-- Prescripción P1757889289464
('P1757889289464', 'med01', 2, '1 each 6 hrs', 2),
('P1757889289464', 'med18', 1, '1 each 12 hrs', 2);
