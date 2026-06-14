package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.auto.Auto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutoDAO {

    // Alta de auto
    public void insertar(Auto auto) {
        String sql = "INSERT INTO Autos (modelo, velocidad_base, escuderia_id) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auto.getModelo());
            pstmt.setDouble(2, auto.getVelocidadBase());
            pstmt.setInt(3, auto.getEscuderia().getId()); // FK hacia escudería
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Baja de auto
    public void eliminar(int id) {
        String sql = "DELETE FROM Autos WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Modificación de auto
    public void actualizar(Auto auto) {
        String sql = "UPDATE Autos SET modelo = ?, velocidad_base = ?, escuderia_id = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auto.getModelo());
            pstmt.setDouble(2, auto.getVelocidadBase());
            pstmt.setInt(3, auto.getEscuderia().getId());
            pstmt.setInt(4, auto.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Consulta por ID
    public Auto obtenerPorId(int id) {
        String sql = "SELECT id, modelo, velocidad_base, escuderia_id FROM Autos WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Auto(
                        rs.getInt("id"),
                        rs.getString("modelo"),
                        rs.getDouble("velocidad_base"),
                        // acá deberías recuperar la escudería con EscuderiaDAO
                        null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Listar todos
    public List<Auto> obtenerTodos() {
        List<Auto> lista = new ArrayList<>();
        String sql = "SELECT id, modelo, velocidad_base, escuderia_id FROM Autos";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Auto(
                        rs.getInt("id"),
                        rs.getString("modelo"),
                        rs.getDouble("velocidad_base"),
                        // recuperar escudería con EscuderiaDAO
                        null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public Auto obtenerPorEscuderiaId(int escuderiaId) {
        String sql = "SELECT id, modelo, velocidad_base, escuderia_id FROM Autos WHERE escuderia_id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, escuderiaId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Auto(
                        rs.getInt("id"),
                        rs.getString("modelo"),
                        rs.getDouble("velocidad_base"),
                        null // acá podrías usar EscuderiaDAO para traer la escudería completa
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
