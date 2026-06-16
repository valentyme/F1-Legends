package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.utiles.ColorUtil;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EscuderiaDAO {

    public Escuderia obtenerPorId(int id) {
        String sql = "SELECT id, nombre, color FROM Escuderias WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearEscuderia(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener la escuderia.", e);
        }
        return null;
    }

    public Escuderia obtenerPorNombre(String nombre) {
        String sql = "SELECT id, nombre, color FROM Escuderias WHERE nombre = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearEscuderia(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener la escuderia.", e);
        }
        return null;
    }

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
            throw new IllegalStateException("No se pudieron listar las escuderias.", e);
        }
        return lista;
    }

    public void insertar(Escuderia escuderia) {
        String sql = "INSERT INTO Escuderias (nombre, color) VALUES (?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, escuderia.getNombre());
            pstmt.setString(2, ColorUtil.toHex(escuderia.getColor()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo insertar la escuderia.", e);
        }
    }

    public void eliminar(int id) {
        try (Connection conn = ConexionBD.conectar()) {
            conn.setAutoCommit(false);
            try {
                eliminarPilotosPorEscuderia(conn, id);
                eliminarAutosPorEscuderia(conn, id);
                eliminarEscuderia(conn, id);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar la escuderia y sus autos asociados.", e);
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
            throw new IllegalStateException("No se pudo actualizar la escuderia.", e);
        }
    }

    private Escuderia mapearEscuderia(ResultSet rs) throws SQLException {
        return new Escuderia(
                rs.getInt("id"),
                rs.getString("nombre"),
                Color.web(rs.getString("color"))
        );
    }

    private void eliminarPilotosPorEscuderia(Connection conn, int escuderiaId) throws SQLException {
        String sql = "DELETE FROM Pilotos WHERE escuderia_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, escuderiaId);
            pstmt.executeUpdate();
        }
    }

    private void eliminarAutosPorEscuderia(Connection conn, int escuderiaId) throws SQLException {
        String sql = "DELETE FROM Autos WHERE escuderia_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, escuderiaId);
            pstmt.executeUpdate();
        }
    }

    private void eliminarEscuderia(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM Escuderias WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
