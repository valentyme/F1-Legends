package com.f1legends.patrones.factory;

import com.f1legends.DAO.modeloDAO.AutoDAO;
import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.auto.AutoFerrari;
import com.f1legends.modelo.auto.AutoMercedes;
import com.f1legends.modelo.auto.AutoRedBull;

import java.util.List;

public class FabricaAuto {
    private final AutoDAO autoDAO;
    private final EscuderiaDAO escuderiaDAO;

    public FabricaAuto() {
        this(new AutoDAO(), new EscuderiaDAO());
    }

    public FabricaAuto(AutoDAO autoDAO, EscuderiaDAO escuderiaDAO) {
        this.autoDAO = autoDAO;
        this.escuderiaDAO = escuderiaDAO;
    }

    public Auto crearAuto(TipoAuto tipoAuto) {
        Auto autoPersistido = obtenerAutoPersistido(tipoAuto);
        if (autoPersistido != null) {
            return autoPersistido;
        }

        return switch (tipoAuto) {
            case FERRARI -> new AutoFerrari();
            case MERCEDES -> new AutoMercedes();
            case RED_BULL -> new AutoRedBull();
        };
    }

    public Auto crearAuto(String modelo, double velocidadBase, int escuderiaId) {
        Escuderia escuderia = escuderiaDAO.obtenerPorId(escuderiaId);
        if (escuderia == null) {
            throw new IllegalArgumentException("No existe una escuderia con ID " + escuderiaId + ".");
        }
        return new Auto(0, modelo, velocidadBase, escuderia);
    }

    public Auto crearAutoPersistido(String modelo, double velocidadBase, int escuderiaId) {
        Auto auto = crearAuto(modelo, velocidadBase, escuderiaId);
        int id = autoDAO.insertar(auto);
        return new Auto(id, auto.getModelo(), auto.getVelocidadBase(), auto.getEscuderia());
    }

    public Auto obtenerAuto(int id) {
        return autoDAO.obtenerPorId(id);
    }

    public List<Auto> obtenerAutos() {
        return autoDAO.obtenerTodos();
    }

    public void actualizarAuto(Auto auto) {
        autoDAO.actualizar(auto);
    }

    public void eliminarAuto(int id) {
        autoDAO.eliminar(id);
    }

    private Auto obtenerAutoPersistido(TipoAuto tipoAuto) {
        return switch (tipoAuto) {
            case FERRARI -> autoDAO.obtenerPorEscuderiaId(1);
            case MERCEDES -> autoDAO.obtenerPorEscuderiaId(2);
            case RED_BULL -> autoDAO.obtenerPorEscuderiaId(3);
        };
    }
}
