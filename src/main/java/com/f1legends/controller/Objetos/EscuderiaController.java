package com.f1legends.controller.Objetos;

import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.patrones.facade.SistemaCarreraFacade;
import javafx.scene.paint.Color;

import java.util.List;


public class EscuderiaController {

    private final SistemaCarreraFacade facade;
    private final EscuderiaDAO escuderiaDAO;

    public EscuderiaController(SistemaCarreraFacade facade, EscuderiaDAO escuderiaDAO) {
        this.facade = facade;
        this.escuderiaDAO = escuderiaDAO;
    }

    public void altaEscuderia(String nombre, Color color) {
        Escuderia escuderia = new Escuderia(0, nombre, color);
        escuderiaDAO.insertar(escuderia);
    }

    public void bajaEscuderia(int id) {
        facade.bajaEscuderia(id);
    }

    public void modificarEscuderia(int id, String nombre, String colorHex) {
        facade.modificarEscuderia(id, nombre, colorHex);
    }

    public List<Escuderia> consultarEscuderias() {
        return facade.consultarEscuderias();
    }
}