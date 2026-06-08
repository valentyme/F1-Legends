package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CircuitoDAO {

    // Buscar circuito por ID
    public CircuitoDTO obtenerPorId(int id) {
        String sql = "SELECT id, nombre, pais, vueltas FROM Circuitos WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new CircuitoDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("pais"),
                        rs.getInt("vueltas")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Buscar circuito por nombre
    public CircuitoDTO obtenerPorNombre(String nombre) {
        String sql = "SELECT id, nombre, pais, vueltas FROM Circuitos WHERE nombre = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new CircuitoDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("pais"),
                        rs.getInt("vueltas")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Listar todos los circuitos
    public List<CircuitoDTO> obtenerTodos() {
        List<CircuitoDTO> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, pais, vueltas FROM Circuitos";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new CircuitoDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("pais"),
                        rs.getInt("vueltas")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
