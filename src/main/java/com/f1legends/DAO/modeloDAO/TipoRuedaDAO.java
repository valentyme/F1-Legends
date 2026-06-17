package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.TipoRueda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TipoRuedaDAO {
    public List<TipoRueda> obtenerTodos() {
        asegurarDatosBase();
        List<TipoRueda> ruedas = new ArrayList<>();
        String sql = "SELECT id, nombre, durabilidad, rendimiento FROM TiposRueda ORDER BY id";

        try (Connection conn = ConexionBD.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ruedas.add(new TipoRueda(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("durabilidad"),
                        rs.getDouble("rendimiento")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener tipos de rueda: " + e.getMessage(), e);
        }
        return ruedas;
    }

    private void asegurarDatosBase() {
        String countSql = "SELECT COUNT(*) FROM TiposRueda";
        try (Connection conn = ConexionBD.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar tipos de rueda: " + e.getMessage(), e);
        }

        insertarBase("Blanda", 45, 1.08);
        insertarBase("Media", 65, 1.00);
        insertarBase("Dura", 85, 0.94);
        insertarBase("Intermedia", 70, 0.88);
        insertarBase("Lluvia Extrema", 80, 0.82);
    }

    private void insertarBase(String nombre, int durabilidad, double rendimiento) {
        String sql = "INSERT INTO TiposRueda (nombre, durabilidad, rendimiento) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, durabilidad);
            ps.setDouble(3, rendimiento);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar tipo de rueda: " + e.getMessage(), e);
        }
    }
}
