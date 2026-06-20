package com.f1legends.controller.Objetos;

import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.patrones.factory.TipoAuto;
import com.f1legends.servicios.AutoService;

import java.util.List;


public class AutoController {
    private final AutoService autoService;
    private final EscuderiaDAO escuderiaDAO;

    public AutoController(AutoService autoService, EscuderiaDAO escuderiaDAO) {
        this.autoService = autoService;
        this.escuderiaDAO = escuderiaDAO;
    }

    public Auto altaAuto(String modelo, double velocidadBase, int escuderiaId, TipoAuto tipoAuto) {
        return autoService.altaAuto(modelo, velocidadBase, escuderiaId, tipoAuto);
    }

    public void bajaAuto(int id) {
        autoService.bajaAuto(id);
    }

    public void modificarAuto(int id, String modelo, double velocidadBase, int escuderiaId, TipoAuto tipoAuto) {
        autoService.modificarAuto(id, modelo, velocidadBase, escuderiaId, tipoAuto);
    }

    public List<Auto> consultarAutos() {
        return autoService.consultarAutos();
    }

    public Auto obtenerAuto(int id) {
        return autoService.obtenerAuto(id);
    }
}