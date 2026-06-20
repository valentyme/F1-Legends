package com.f1legends.controller;

import com.f1legends.DAO.modeloDAO.AutoDAO;
import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.DAO.modeloDAO.PilotoDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.modelo.Piloto;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.patrones.facade.SistemaCarreraFacade;

import java.util.List;

public class PilotoController {

    private final SistemaCarreraFacade facade;
    private final AutoDAO autoDAO;

    public PilotoController(SistemaCarreraFacade facade, AutoDAO autoDAO) {
        this.facade = facade;
        this.autoDAO = autoDAO;
    }

    /** Crea un piloto y lo asocia al auto de la escudería indicada. */
    public Piloto altaPiloto(String nombre, int habilidad, int escuderiaId) {
        Auto auto = autoDAO.obtenerPorEscuderiaId(escuderiaId);
        int autoId = auto.getId();
        Piloto piloto = new Piloto(0, nombre, habilidad, escuderiaId, autoId);
        facade.gestionarPilotos("alta", piloto);
        return piloto;
    }

    public void bajaPiloto(int id) {
        Piloto piloto = new Piloto(id, "", 0, 0, 0);
        facade.gestionarPilotos("baja", piloto);
    }

    public void modificarPiloto(int id, String nombre, int habilidad, int escuderiaId) {
        Auto auto = autoDAO.obtenerPorEscuderiaId(escuderiaId);
        int autoId = auto.getId();
        Piloto piloto = new Piloto(id, nombre, habilidad, escuderiaId, autoId);
        facade.gestionarPilotos("modificacion", piloto);
    }

    public void consultarPiloto(int id) {
        Piloto piloto = new Piloto(id, "", 0, 0, 0);
        facade.gestionarPilotos("consulta", piloto);
    }

    public List<Piloto> obtenerTodos() {
        return PilotoDAO.obtenerTodos();
    }

    public Piloto obtenerPorId(int id) {
        return PilotoDAO.obtenerPorId(id);
    }

    public List<Escuderia> obtenerEscuderias() {
        return EscuderiaDAO.obtenerTodos();
    }
}