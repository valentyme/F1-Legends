package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.utiles.ColorUtil;
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
    public static List<Escuderia> obtenerTodos() {
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
    // Alta
    public void insertar(Escuderia escuderia) {
        String sql = "INSERT INTO Escuderias (nombre, color) VALUES (?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, escuderia.getNombre());
            pstmt.setString(2, escuderia.getColor().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Baja
    public void eliminar(int id) {
        try (Connection conn = ConexionBD.conectar()) {
            // 1. Eliminar pilotos de la escudería
            String sqlPilotos = "DELETE FROM Pilotos WHERE escuderia_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlPilotos)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            // 2. Eliminar autos de la escudería
            String sqlAutos = "DELETE FROM Autos WHERE escuderia_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlAutos)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            // 3. Eliminar la escudería
            String sqlEscuderia = "DELETE FROM Escuderias WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlEscuderia)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void actualizar(Escuderia escuderia) {
        String sql = "UPDATE Escuderias SET nombre = ?, color = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, escuderia.getNombre());
            pstmt.setString(2, ColorUtil.toHex(escuderia.getColor()));
            pstmt.setInt(3, escuderia.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }



    }


}
