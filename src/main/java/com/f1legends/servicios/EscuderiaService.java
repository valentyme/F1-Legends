package com.f1legends.servicios;

import com.f1legends.DAO.ConexionBD;
import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EscuderiaService {
    private final EscuderiaDAO escuderiaDAO = new EscuderiaDAO();

    public void gestionarEscuderias(String operacion, Escuderia escuderia) {
        switch (operacion) {
            case "alta" -> escuderiaDAO.insertar(escuderia);
            case "baja" -> escuderiaDAO.eliminar(escuderia.getId());
            case "modificar" -> escuderiaDAO.actualizar(escuderia);
            case "consultar" -> escuderiaDAO.obtenerTodos().forEach(System.out::println);
            default -> System.out.println("Operación inválida.");
        }
    }


    // Alta
    public void altaEscuderia(String nombre, String colorHex) {
        try {
            Escuderia escuderia = new Escuderia(0, nombre, Color.web(colorHex));
            escuderiaDAO.insertar(escuderia);
            System.out.println("✅ Escudería registrada: " + nombre);
        } catch (IllegalArgumentException e) {
            System.out.println("⚠ Color inválido. Debe ser en formato #RRGGBB.");
        }
    }

    // Baja
    public void bajaEscuderia(int id) {
        escuderiaDAO.eliminar(id);
        System.out.println("Escudería eliminada.");
    }

    // Modificación
    public void modificarEscuderia(int id, String nuevoNombre, String nuevoColorHex) {
        try {
            Escuderia escuderia = new Escuderia(id, nuevoNombre, Color.web(nuevoColorHex));
            escuderiaDAO.actualizar(escuderia);
            System.out.println("Escudería actualizada.");
        } catch (IllegalArgumentException e) {
            System.out.println("Color inválido. Debe ser en formato #RRGGBB.");
        }
    }

    // Consulta
    public List<Escuderia> consultarEscuderias() {
        return escuderiaDAO.obtenerTodos();
    }
}
