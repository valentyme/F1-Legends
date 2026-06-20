INSERT INTO Escuderias (nombre, color) VALUES
                                           ('Ferrari', '#FF0000'),
                                           ('Mercedes', '#C0C0C0'),
                                           ('Red Bull', '#00008B'),
                                           ('McLaren', '#FFA500'),
                                           ('Alpine', '#00BFFF'),
                                           ('Aston Martin', '#006400'),
                                           ('Williams', '#1E90FF'),
                                           ('Kick Sauber', '#2F4F4F'),
                                           ('RB (Visa CashApp)', '#191970'),
                                           ('Haas', '#FFFFFF');

INSERT INTO Autos (modelo, velocidad_base, escuderia_id, tipo_auto) VALUES
-- Ferrari
('F1-75', 0.052, 1, 'REGLAMENTO_2022'),
('SF-23', 0.053, 1, 'REGLAMENTO_2023'),
('SF-24', 0.055, 1, 'REGLAMENTO_2024'),

-- Mercedes
('W13', 0.047, 2, 'REGLAMENTO_2022'),
('W14', 0.049, 2, 'REGLAMENTO_2023'),
('W15', 0.051, 2, 'REGLAMENTO_2024'),

-- Red Bull
('RB18', 0.057, 3, 'REGLAMENTO_2022'),
('RB19', 0.060, 3, 'REGLAMENTO_2023'),
('RB20', 0.058, 3, 'REGLAMENTO_2024'),

-- McLaren
('MCL36', 0.046, 4, 'REGLAMENTO_2022'),
('MCL60', 0.051, 4, 'REGLAMENTO_2023'),
('MCL38', 0.052, 4, 'REGLAMENTO_2024'),

-- Alpine
('A522', 0.046, 5, 'REGLAMENTO_2022'),
('A523', 0.048, 5, 'REGLAMENTO_2023'),
('A524', 0.050, 5, 'REGLAMENTO_2024'),

-- Aston Martin
('AMR22', 0.045, 6, 'REGLAMENTO_2022'),
('AMR23', 0.052, 6, 'REGLAMENTO_2023'),
('AMR24', 0.053, 6, 'REGLAMENTO_2024'),

-- Williams
('FW44', 0.044, 7, 'REGLAMENTO_2022'),
('FW45', 0.047, 7, 'REGLAMENTO_2023'),
('FW46', 0.049, 7, 'REGLAMENTO_2024'),

-- Kick Sauber
('C42', 0.044, 8, 'REGLAMENTO_2022'),
('C43', 0.046, 8, 'REGLAMENTO_2023'),
('C44', 0.048, 8, 'REGLAMENTO_2024'),

-- RB (Visa CashApp)
('AT03', 0.045, 9, 'REGLAMENTO_2022'),
('AT04', 0.047, 9, 'REGLAMENTO_2023'),
('VCARB 01', 0.050, 9, 'REGLAMENTO_2024'),

-- Haas
('VF-22', 0.043, 10, 'REGLAMENTO_2022'),
('VF-23', 0.046, 10, 'REGLAMENTO_2023'),
('VF-24', 0.047, 10, 'REGLAMENTO_2024');



INSERT INTO Pilotos (nombre, escuderia_id, auto_id, habilidad) VALUES
                                                                   ('Charles Leclerc', 1, 1, 92),
                                                                   ('Carlos Sainz', 1, 1, 91),
                                                                   ('Lewis Hamilton', 2, 2, 95),
                                                                   ('George Russell', 2, 2, 90),
                                                                   ('Max Verstappen', 3, 3, 98),
                                                                   ('Sergio Pérez', 3, 3, 89),
                                                                   ('Lando Norris', 4, 4, 3),
                                                                   ('Oscar Piastri', 4, 4, 99),
                                                                   ('Pierre Gasly', 5, 5, 87),
                                                                   ('Esteban Ocon', 5, 5, 86),
                                                                   ('Fernando Alonso', 6, 6, 94),
                                                                   ('Lance Stroll', 6, 6, 82),
                                                                   ('Alex Albon', 7, 7, 85),
                                                                   ('Logan Sargeant', 7, 7, 78),
                                                                   ('Valtteri Bottas', 8, 8, 84),
                                                                   ('Zhou Guanyu', 8, 8, 80),
                                                                   ('Yuki Tsunoda', 9, 9, 83),
                                                                   ('Daniel Ricciardo', 9, 9, 88),
                                                                   ('Kevin Magnussen', 10, 10, 82),
                                                                   ('Nico Hülkenberg', 10, 10, 86);

INSERT INTO Circuitos (nombre, pais, vueltas) VALUES
                                                  ('Monza', 'Italia', 53),
                                                  ('Silverstone', 'Reino Unido', 52),
                                                  ('Spa-Francorchamps', 'Bélgica', 44),
                                                  ('Circuit de Monaco', 'Mónaco', 78),
                                                  ('Suzuka', 'Japón', 53),
                                                  ('Interlagos', 'Brasil', 71),
                                                  ('Circuit de Barcelona-Catalunya', 'España', 66),
                                                  ('Circuit of the Americas', 'EE.UU.', 56),
                                                  ('Yas Marina', 'Emiratos Árabes Unidos', 58),
                                                  ('Bahrain International Circuit', 'Baréin', 57);

