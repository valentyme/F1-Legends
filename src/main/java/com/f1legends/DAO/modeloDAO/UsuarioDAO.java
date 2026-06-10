package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.Usuarios.Administrador;
import com.f1legends.modelo.Usuarios.Jugador;
import com.f1legends.modelo.Usuarios.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para la tabla Usuarios.
 *
 * Soporta: CU01, CU02, CU05, CU06.
 *
 * SOLID : SRP — solo persistencia de usuarios.
 *         DIP — los servicios deben depender de una interfaz IUsuarioDAO (recomendado).
 * GRASP : Pure Fabrication, Low Coupling, High Cohesion.
 */
public class UsuarioDAO {

    // ──────────────────────────────────────────────
    // CU02 — Crear perfil
    // ──────────────────────────────────────────────
    public void crear(String username, String password, String rol) {
        String sql = "INSERT INTO Usuarios (username, password, rol, fecha_registro) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, rol);
            ps.setString(4, LocalDate.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear usuario: " + e.getMessage(), e);
        }
        if (rol == "Jugador"){
            int id = obtenerIdPorUsername(username);
            RankingGlobalDAO.EntradaRanking aux = new RankingGlobalDAO.EntradaRanking(id, username, 0 );
            RankingGlobalDAO hola = new RankingGlobalDAO();
            hola.crearRankGlobUsuario(aux);
        }

    }

    // ──────────────────────────────────────────────
    // CU01 — Buscar por username (para login)
    // ──────────────────────────────────────────────
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT id, username, password, rol, fecha_registro FROM Usuarios WHERE username = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public int obtenerIdPorUsername(String username) {
        String sql = "SELECT id FROM Usuarios WHERE username = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

            throw new RuntimeException(
                    "No existe un usuario con username: " + username);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al obtener ID del usuario: " + e.getMessage(), e);
        }
    }

    // ──────────────────────────────────────────────
    // Buscar por id
    // ──────────────────────────────────────────────
    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT id, username, password, rol, fecha_registro FROM Usuarios WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ──────────────────────────────────────────────
    // Listar todos
    // ──────────────────────────────────────────────
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, username, password, rol, fecha_registro FROM Usuarios ORDER BY username";
        try (Connection conn = ConexionBD.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios: " + e.getMessage(), e);
        }
        return lista;
    }

    // ──────────────────────────────────────────────
    // CU05 — Modificar perfil (username y/o password)
    // ──────────────────────────────────────────────
    public void actualizar(int id, String nuevoUsername, String nuevaPassword) {
        String sql = "UPDATE Usuarios SET username = ?, password = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoUsername);
            ps.setString(2, nuevaPassword);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    // ──────────────────────────────────────────────
    // CU06 — Eliminar perfil
    // ──────────────────────────────────────────────
    public void eliminar(int id) {
        // Primero se eliminan entradas dependientes (ranking)
        String sqlRanking = "DELETE FROM RankingGlobal WHERE usuario_id = ?";
        String sqlUsuario = "DELETE FROM Usuarios WHERE id = ?";
        try (Connection conn = ConexionBD.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlRanking);
                 PreparedStatement ps2 = conn.prepareStatement(sqlUsuario)) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
                ps2.setInt(1, id);
                ps2.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    // ──────────────────────────────────────────────
    // Mapeo ResultSet → entidad de dominio
    // ──────────────────────────────────────────────
    private Usuario mapear(ResultSet rs) throws SQLException {
        int    id      = rs.getInt("id");
        String user    = rs.getString("username");
        String pass    = rs.getString("password");
        String rol     = rs.getString("rol");
        String fecha   = rs.getString("fecha_registro");

        return switch (rol) {
            case "Jugador"       -> {
                Jugador j = new Jugador(id, user, pass, fecha);
                yield j;
            }
            case "Administrador" -> new Administrador(id, user, pass,LocalDate.now().toString());
            default -> throw new IllegalStateException("Rol desconocido: " + rol);
        };
    }
}
