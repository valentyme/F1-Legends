package com.f1legends.DAO.modelo;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.Piloto;
import java.sql.*;

public class PilotoDAO {
    public Piloto obtenerPorNombre(String nombre) {
        String sql = "SELECT id, nombre, habilidad FROM Pilotos WHERE nombre = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Piloto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("habilidad")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
