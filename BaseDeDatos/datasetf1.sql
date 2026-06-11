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
INSERT INTO Autos (modelo, velocidad_base, escuderia_id) VALUES
                                                             ('SF-24', 0.055, 1),
                                                             ('W15', 0.051, 2),
                                                             ('RB20', 0.058, 3),
                                                             ('MCL38', 0.052, 4),
                                                             ('A524', 0.050, 5),
                                                             ('AMR24', 0.053, 6),
                                                             ('FW46', 0.049, 7),
                                                             ('C44', 0.048, 8),
                                                             ('VCARB 01', 0.050, 9),
                                                             ('VF-24', 0.047, 10);
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

insert into Usuarios (username, password, rol, fecha_registro) values
                                        ('Juan', 'password123)', 'Jugador', '2024-01-15');