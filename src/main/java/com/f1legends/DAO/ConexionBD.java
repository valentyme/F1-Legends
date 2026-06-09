package com.f1legends.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:sqlite:BaseDeDatos/f1_legends.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontro el driver SQLite", e);
        }
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
