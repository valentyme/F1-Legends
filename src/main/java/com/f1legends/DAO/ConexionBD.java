package com.f1legends.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:sqlite:f1_legends.db"; // tu archivo .db

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
