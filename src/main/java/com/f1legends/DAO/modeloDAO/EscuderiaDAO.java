package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.Escuderias.Escuderia;
import javafx.scene.paint.Color;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EscuderiaDAO {

    // Buscar escudería por ID
    public Escuderia obtenerPorId(int id) {
        String sql = "SELECT id, nombre, color FROM Escuderias WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Escuderia(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        Color.web(rs.getString("color")) // convierte HEX a Color
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Buscar escudería por nombre
    public Escuderia obtenerPorNombre(String nombre) {
        String sql = "SELECT id, nombre, color FROM Escuderias WHERE nombre = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Escuderia(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        Color.web(rs.getString("color"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Listar todas las escuderías
    public List<Escuderia> obtenerTodos() {
        List<Escuderia> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, color FROM Escuderias";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Escuderia(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        Color.web(rs.getString("color"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
