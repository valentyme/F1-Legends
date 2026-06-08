package com.f1legends.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class ConexionBD {
    private static final String URL = "jdbc:sqlite:BaseDeDatos/f1_legends.db"; // tu archivo .db

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
        try {
            inicializarBaseSiEsNecesario(conn); // create schema + seed if empty
            return conn;
        } catch (SQLException e) {
            conn.close();
            throw e;
        }
    }

    private static void inicializarBaseSiEsNecesario(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Escuderias (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, color TEXT NOT NULL)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Autos (id INTEGER PRIMARY KEY AUTOINCREMENT, modelo TEXT NOT NULL, velocidad_base REAL NOT NULL, escuderia_id INTEGER NOT NULL)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Pilotos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, escuderia_id INTEGER NOT NULL, auto_id INTEGER NOT NULL, habilidad INTEGER NOT NULL)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Circuitos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, pais TEXT NOT NULL, vueltas INTEGER NOT NULL)");
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Pilotos")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        try {
            String sql = Files.readString(Path.of("BaseDeDatos", "datasetf1.sql"));
            try (Statement stmt = conn.createStatement()) {
                for (String query : Arrays.stream(sql.split(";"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList()) {
                    stmt.executeUpdate(query);
                }
            }
        } catch (Exception e) {
            throw new SQLException("No se pudo inicializar la base de datos", e);
        }
    }
}
