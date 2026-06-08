package com.f1legends.DAO.modeloDAO;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.modelo.Piloto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        // Buscar por ID
        public static Piloto obtenerPorId(int id) {
            String sql = "SELECT id, nombre, habilidad FROM Pilotos WHERE id = ?";
            try (Connection conn = ConexionBD.conectar();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);
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

        // Listar todos los pilotos
        public static List<Piloto> obtenerTodos() {
            List<Piloto> lista = new ArrayList<>();
            String sql = "SELECT id, nombre, habilidad FROM Pilotos";
            try (Connection conn = ConexionBD.conectar();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    lista.add(new Piloto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getInt("habilidad")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return lista;
        }
    }


