package com.f1legends.patrones.fabricaEscuderia;

import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import javafx.scene.paint.Color;

public class FabricaEscuderia {

    private EscuderiaDAO escuderiaDAO;

    public FabricaEscuderia() {
        this.escuderiaDAO = new EscuderiaDAO();
    }

    public Escuderia crearEscuderia(int id) {
        // Buscar escudería en la BD
        Escuderia escuderia = escuderiaDAO.obtenerPorId(id);
        if (escuderia == null) {
            throw new IllegalArgumentException("No existe escudería con id=" + id);
        }

        // Convertir el color hex guardado en BD a javafx.scene.paint.Color
        Color color = Color.web(escuderia.getColor().toString());

        return new Escuderia(escuderia.getId(), escuderia.getNombre(), color);
    }
}
