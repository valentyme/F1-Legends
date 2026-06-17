-- 1. Tablas independientes (no dependen de nadie)
create table Usuarios
(
    id             INTEGER primary key autoincrement,
    username       TEXT not null unique,
    password       TEXT not null,
    rol            TEXT not null,
    fecha_registro TEXT,
    check (rol IN ('Jugador', 'Administrador'))
);

create table TiposRueda
(
    id          INTEGER primary key autoincrement,
    nombre      TEXT not null,
    durabilidad INTEGER,
    rendimiento REAL
);

create table Escuderias
(
    id     INTEGER primary key autoincrement,
    nombre TEXT not null,
    color  TEXT not null
);

create table Circuitos
(
    id      INTEGER primary key autoincrement,
    nombre  TEXT not null,
    pais    TEXT,
    vueltas INTEGER not null
);

-- 2. Tablas de segundo nivel (dependen de las anteriores)
create table Carreras
(
    id            INTEGER primary key autoincrement,
    circuito_id   INTEGER not null references Circuitos,
    fecha         TEXT not null,
    vueltas       INTEGER not null,
    clima_inicial TEXT,
    modo_juego    TEXT default 'Singleplayer' not null,
    check (modo_juego IN ('Singleplayer', 'Multiplayer'))
);

DROP TABLE IF EXISTS Autos;

CREATE TABLE Autos (
                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                       modelo TEXT NOT NULL,
                       velocidad_base REAL NOT NULL,
                       escuderia_id INTEGER NOT NULL,
                       tipo_auto TEXT NOT NULL CHECK (
                           tipo_auto IN ('REGLAMENTO_2022', 'REGLAMENTO_2023', 'REGLAMENTO_2024')
                           ),
                       FOREIGN KEY (escuderia_id) REFERENCES Escuderias(id),
                       UNIQUE (escuderia_id, tipo_auto)
);
-- 3. Tablas de tercer nivel (dependen de Autos y Escuderías)
create table Pilotos
(
    id           INTEGER primary key autoincrement,
    nombre       TEXT not null,
    escuderia_id INTEGER not null references Escuderias,
    auto_id      INTEGER not null references Autos,
    habilidad    INTEGER,
    check (habilidad BETWEEN 0 AND 100)
);

-- 4. Tablas transaccionales / finales (dependen de Carreras y Pilotos)
create table RankingFinal
(
    id               INTEGER primary key autoincrement,
    carrera_id       INTEGER not null references Carreras,
    piloto_id        INTEGER not null references Pilotos,
    usuario_id       INTEGER references Usuarios,
    posicion_final   INTEGER not null,
    puntos_obtenidos INTEGER not null
);

create table ParadasBoxes
(
    id            INTEGER primary key autoincrement,
    carrera_id    INTEGER not null references Carreras,
    piloto_id     INTEGER not null references Pilotos,
    vuelta        INTEGER not null,
    tiempo_parada REAL,
    motivo        TEXT,
    rueda_id      INTEGER references TiposRueda
);

create table EstadisticasPilotoCarrera
(
    id                  INTEGER primary key autoincrement,
    carrera_id          INTEGER not null references Carreras,
    piloto_id           INTEGER not null references Pilotos,
    posicion_final      INTEGER,
    posiciones_ganadas  INTEGER default 0,
    posiciones_perdidas INTEGER default 0,
    entradas_boxes      INTEGER default 0,
    vueltas_completadas INTEGER default 0,
    estado_final        TEXT,
    check (estado_final IN ('Finalizó', 'Abandono', 'Accidente'))
);

create table Adelantamientos
(
    id                   INTEGER primary key autoincrement,
    carrera_id           INTEGER not null references Carreras,
    piloto_id            INTEGER not null references Pilotos,
    piloto_adelantado_id INTEGER not null references Pilotos,
    vuelta               INTEGER not null,
    nueva_posicion       INTEGER not null
);

create table PilotosAccidente
(
    id          INTEGER primary key autoincrement,
    piloto_id   INTEGER not null references Pilotos,
    estado      TEXT,
    carrera_id  INTEGER not null references Carreras, -- Corregido: se agregó la relación
    vuelta      INTEGER not null,
    descripcion TEXT,
    check (estado IN ('Abandono', 'Continuó', 'Accidente Grave'))
);
create table RankingGlobal
(
    id               INTEGER
        primary key autoincrement,
    usuario_id      INTEGER not null
        references Usuarios,
    puntaje INTEGER not null
);