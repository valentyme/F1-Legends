package com.f1legends;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.DAO.modeloDAO.PilotoDAO;
import com.f1legends.modelo.Piloto;

public class Test {
    public static void main(String[] args) {
        try {
            // 1. Probar conexión
            if (ConexionBD.conectar() != null) {
                System.out.println("✅ Conexión a SQLite establecida");
            }

            // 2. Usar el DAO para obtener a Piastri
            PilotoDAO dao = new PilotoDAO();
            Piloto piastri = dao.obtenerPorNombre("Oscar Piastri");

            if (piastri != null) {
                System.out.println("✅ Piloto encontrado:");
                System.out.println("ID: " + piastri.getId());
                System.out.println("Nombre: " + piastri.getNombre());
                System.out.println("Habilidad: " + piastri.getHabilidad());
                System.out.println("EL MEJOR DEL MUNDO");
            } else {
                System.out.println("❌ No se encontró a Piastri en la BD");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
