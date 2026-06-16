package com.f1legends.patrones.factory;

import com.f1legends.DAO.modeloDAO.AutoDAO;
import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.auto.AutoReglamento2022;
import com.f1legends.modelo.auto.AutoReglamento2023;
import com.f1legends.modelo.auto.AutoReglamento2024;

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

    public Auto crearAuto(TipoAuto tipoAuto, String modelo, double velocidadBase, int escuderiaId) {
        Escuderia escuderia = escuderiaDAO.obtenerPorId(escuderiaId);
        if (escuderia == null) {
            throw new IllegalArgumentException("No existe una escuderia con ID " + escuderiaId + ".");
        }
        return crearAuto(tipoAuto, 0, modelo, velocidadBase, escuderia);
    }

    public Auto crearAutoPersistido(TipoAuto tipoAuto, String modelo, double velocidadBase, int escuderiaId) {
        Auto auto = crearAuto(tipoAuto, modelo, velocidadBase, escuderiaId);
        int id = autoDAO.insertar(auto);
        return crearAuto(tipoAuto, id, auto.getModelo(), auto.getVelocidadBase(), auto.getEscuderia());
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

    public Auto crearAuto(TipoAuto tipoAuto, int id, String modelo, double velocidadBase, Escuderia escuderia) {
        return switch (tipoAuto) {
            case REGLAMENTO_2022 -> new AutoReglamento2022(id, modelo, velocidadBase, escuderia);
            case REGLAMENTO_2023 -> new AutoReglamento2023(id, modelo, velocidadBase, escuderia);
            case REGLAMENTO_2024 -> new AutoReglamento2024(id, modelo, velocidadBase, escuderia);
        };
    }
}
