package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.modelo.auto.Auto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AutoDAO {
    private final EscuderiaDAO escuderiaDAO = new EscuderiaDAO();

    public int insertar(Auto auto) {
        String sql = "INSERT INTO Autos (modelo, velocidad_base, escuderia_id) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, auto.getModelo());
            pstmt.setDouble(2, auto.getVelocidadBase());
            pstmt.setInt(3, obtenerEscuderiaId(auto));
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo insertar el auto.", e);
        }
        return 0;
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM Autos WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar el auto.", e);
        }
    }

    public void actualizar(Auto auto) {
        String sql = "UPDATE Autos SET modelo = ?, velocidad_base = ?, escuderia_id = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, auto.getModelo());
            pstmt.setDouble(2, auto.getVelocidadBase());
            pstmt.setInt(3, obtenerEscuderiaId(auto));
            pstmt.setInt(4, auto.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el auto.", e);
        }
    }

    public Auto obtenerPorId(int id) {
        String sql = "SELECT id, modelo, velocidad_base, escuderia_id FROM Autos WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearAuto(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener el auto.", e);
        }
        return null;
    }

    public List<Auto> obtenerTodos() {
        List<Auto> lista = new ArrayList<>();
        String sql = "SELECT id, modelo, velocidad_base, escuderia_id FROM Autos";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearAuto(rs));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los autos.", e);
        }
        return lista;
    }

    public Auto obtenerPorEscuderiaId(int escuderiaId) {
        List<Auto> autos = obtenerTodosPorEscuderiaId(escuderiaId);
        return autos.isEmpty() ? null : autos.get(0);
    }

    public List<Auto> obtenerTodosPorEscuderiaId(int escuderiaId) {
        List<Auto> lista = new ArrayList<>();
        String sql = "SELECT id, modelo, velocidad_base, escuderia_id FROM Autos WHERE escuderia_id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, escuderiaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAuto(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron obtener los autos de la escuderia.", e);
        }
        return lista;
    }

    public void eliminarPorEscuderiaId(int escuderiaId) {
        String sql = "DELETE FROM Autos WHERE escuderia_id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, escuderiaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron eliminar los autos de la escuderia.", e);
        }
    }

    private Auto mapearAuto(ResultSet rs) throws SQLException {
        Escuderia escuderia = escuderiaDAO.obtenerPorId(rs.getInt("escuderia_id"));
        return new Auto(
                rs.getInt("id"),
                rs.getString("modelo"),
                rs.getDouble("velocidad_base"),
                escuderia
        );
    }

    private int obtenerEscuderiaId(Auto auto) {
        if (auto.getEscuderia() == null) {
            throw new IllegalArgumentException("El auto debe tener una escuderia asignada.");
        }
        return auto.getEscuderia().getId();
    }
}
