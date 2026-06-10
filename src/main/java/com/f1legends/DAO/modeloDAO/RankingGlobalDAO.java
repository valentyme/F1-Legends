package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla RankingGlobal.
 *
 * Soporta: CU03 (ver ranking global).
 *
 * GRASP: High Cohesion — solo gestiona RankingGlobal.
 *        Low Coupling — la entidad no conoce SQL.
 */
public class RankingGlobalDAO {

    /**
     * Entrada de ranking (clase interna simple para no crear archivo extra).
     */
    public static class EntradaRanking {
        public final int    usuarioId;
        public final String username;
        public final int    puntaje;

        public EntradaRanking(int usuarioId, String username, int puntaje) {
            this.usuarioId = usuarioId;
            this.username  = username;
            this.puntaje   = puntaje;
        }

        public int getPuntaje() {
            return puntaje;
        }

        public int getUsuarioId() {
            return usuarioId;
        }

        public String getUsername() {
            return username;
        }
    }

    // ──────────────────────────────────────────────
    // CU03 — Ranking global ordenado DESC
    // ──────────────────────────────────────────────
    public List<EntradaRanking> obtenerRankingGlobal() {
        List<EntradaRanking> lista = new ArrayList<>();
        String sql = """
            SELECT rg.usuario_id, u.username, rg.puntaje
            FROM RankingGlobal rg
            INNER JOIN Usuarios u ON u.id = rg.usuario_id
            ORDER BY rg.puntaje DESC
        """;
        try (Connection conn = ConexionBD.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new EntradaRanking(
                    rs.getInt("usuario_id"),
                    rs.getString("username"),
                    rs.getInt("puntaje")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener ranking global: " + e.getMessage(), e);
        }
        return lista;
    }

    public void crearRankGlobUsuario(EntradaRanking entradaRanking){
        String sql = "INSERT INTO RankingGlobal (usuario_id, puntaje) VALUES (?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entradaRanking.getUsuarioId());
            ps.setInt(2, 0);
            ps.executeUpdate();
            System.out.println("Creado de forma correcta");
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear Ranking de Usuario: " + e.getMessage(), e);
        }
    }

    // ──────────────────────────────────────────────
    // Sumar puntos al usuario después de una carrera
    // ──────────────────────────────────────────────
    public void sumarPuntos(int usuarioId, int puntos) {
        // Upsert: actualiza si existe, inserta si no
        String sqlSelect = "SELECT id FROM RankingGlobal WHERE usuario_id = ?";
        String sqlUpdate = "UPDATE RankingGlobal SET puntaje = puntaje + ? WHERE usuario_id = ?";
        String sqlInsert = "INSERT INTO RankingGlobal (usuario_id, puntaje) VALUES (?, ?)";

        try (Connection conn = ConexionBD.conectar()) {
            try (PreparedStatement sel = conn.prepareStatement(sqlSelect)) {
                sel.setInt(1, usuarioId);
                ResultSet rs = sel.executeQuery();
                if (rs.next()) {
                    // Ya existe: actualizar
                    try (PreparedStatement upd = conn.prepareStatement(sqlUpdate)) {
                        upd.setInt(1, puntos);
                        upd.setInt(2, usuarioId);
                        upd.executeUpdate();
                    }
                } else {
                    // No existe: insertar
                    try (PreparedStatement ins = conn.prepareStatement(sqlInsert)) {
                        ins.setInt(1, usuarioId);
                        ins.setInt(2, puntos);
                        ins.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al sumar puntos: " + e.getMessage(), e);
        }
    }
}
