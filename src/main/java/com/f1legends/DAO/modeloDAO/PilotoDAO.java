package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.Piloto.Piloto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PilotoDAO {

    // Alta de piloto
    public void insertar(Piloto piloto) {
        String sql = "INSERT INTO Pilotos (nombre, habilidad, escuderia_id, auto_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, piloto.getNombre());
            pstmt.setInt(2, piloto.getHabilidad());
            pstmt.setInt(3, piloto.getEscuderiaId());
            pstmt.setInt(4, piloto.getAutoId()); // nuevo campo obligatorio
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Baja de piloto
    public void eliminar(int id) {
        String sql = "DELETE FROM Pilotos WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modificación de piloto
    public void actualizar(Piloto piloto) {
        String sql = "UPDATE Pilotos SET nombre = ?, habilidad = ?, escuderia_id = ?, auto_id = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, piloto.getNombre());
            pstmt.setInt(2, piloto.getHabilidad());
            pstmt.setInt(3, piloto.getEscuderiaId());
            pstmt.setInt(4, piloto.getAutoId()); // nuevo campo
            pstmt.setInt(5, piloto.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Consulta por nombre
    public Piloto obtenerPorNombre(String nombre) {
        String sql = "SELECT id, nombre, habilidad, escuderia_id, auto_id FROM Pilotos WHERE nombre = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Piloto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("habilidad"),
                        rs.getInt("escuderia_id"),
                        rs.getInt("auto_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Consulta por ID
    public static Piloto obtenerPorId(int id) {
        String sql = "SELECT id, nombre, habilidad, escuderia_id, auto_id FROM Pilotos WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Piloto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("habilidad"),
                        rs.getInt("escuderia_id"),
                        rs.getInt("auto_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Listar todos
    public static List<Piloto> obtenerTodos() {
        List<Piloto> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, habilidad, escuderia_id, auto_id FROM Pilotos";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Piloto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("habilidad"),
                        rs.getInt("escuderia_id"),
                        rs.getInt("auto_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
