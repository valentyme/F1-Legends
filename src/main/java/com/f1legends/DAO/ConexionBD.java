package com.f1legends.DAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConexionBD {
    private static final String DB_PATH = "BaseDeDatos/f1_legends.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;
    private static volatile boolean inicializada = false;

    static {
        try {
            // Registrar el driver explícitamente
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontró el driver SQLite", e);
        }
    }

    public static Connection conectar() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        inicializarBaseSiEsNecesario(conn);
        return conn;
    }

    private static void inicializarBaseSiEsNecesario(Connection conn) throws SQLException {
        if (inicializada) {
            return;
        }

        synchronized (ConexionBD.class) {
            if (inicializada) {
                return;
            }

            crearTablasSiNoExisten(conn);

            String verificarDatos = "SELECT COUNT(*) FROM Pilotos";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(verificarDatos)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    cargarDataset(conn);
                }
            }

            inicializada = true;
        }
    }

    private static void crearTablasSiNoExisten(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Escuderias (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "color TEXT" +
                    ")");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Autos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "modelo TEXT NOT NULL," +
                    "velocidad_base REAL NOT NULL," +
                    "escuderia_id INTEGER NOT NULL," +
                    "FOREIGN KEY (escuderia_id) REFERENCES Escuderias(id)" +
                    ")");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Pilotos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "escuderia_id INTEGER NOT NULL," +
                    "auto_id INTEGER NOT NULL," +
                    "habilidad INTEGER NOT NULL," +
                    "FOREIGN KEY (escuderia_id) REFERENCES Escuderias(id)," +
                    "FOREIGN KEY (auto_id) REFERENCES Autos(id)" +
                    ")");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Circuitos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "pais TEXT NOT NULL," +
                    "vueltas INTEGER NOT NULL" +
                    ")");
        }
    }

    private static void cargarDataset(Connection conn) throws SQLException {
        Path datasetPath = Path.of("BaseDeDatos", "datasetf1.sql");
        try {
            String script = Files.readString(datasetPath, StandardCharsets.UTF_8);
            try (Statement stmt = conn.createStatement()) {
                for (String sql : splitSqlStatements(script)) {
                    String statement = sql.trim();
                    if (!statement.isEmpty()) {
                        stmt.executeUpdate(statement);
                    }
                }
            }
        } catch (IOException e) {
            throw new SQLException("No se pudo cargar el dataset inicial desde " + datasetPath, e);
        }
    }

    private static List<String> splitSqlStatements(String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = 0; i < script.length(); i++) {
            char currentChar = script.charAt(i);
            char nextChar = (i + 1 < script.length()) ? script.charAt(i + 1) : '\0';

            if (inLineComment) {
                if (currentChar == '\n') {
                    inLineComment = false;
                    currentStatement.append(currentChar);
                }
                continue;
            }

            if (inBlockComment) {
                if (currentChar == '*' && nextChar == '/') {
                    inBlockComment = false;
                    i++;
                }
                continue;
            }

            if (!inSingleQuote && currentChar == '-' && nextChar == '-') {
                inLineComment = true;
                i++;
                continue;
            }

            if (!inSingleQuote && currentChar == '/' && nextChar == '*') {
                inBlockComment = true;
                i++;
                continue;
            }

            if (currentChar == '\'' && inSingleQuote && nextChar == '\'') {
                currentStatement.append(currentChar).append(nextChar);
                i++;
                continue;
            }

            if (currentChar == '\'') {
                inSingleQuote = !inSingleQuote;
            }

            if (currentChar == ';' && !inSingleQuote) {
                statements.add(currentStatement.toString());
                currentStatement.setLength(0);
            } else {
                currentStatement.append(currentChar);
            }
        }

        if (!currentStatement.isEmpty()) {
            statements.add(currentStatement.toString());
        }
        return statements;
    }
}
