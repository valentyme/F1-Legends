package com.f1legends.servicios;

import com.f1legends.DAO.modeloDAO.AutoDAO;
import com.f1legends.DAO.modeloDAO.EscuderiaDAO;
import com.f1legends.modelo.Escuderias.Escuderia;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.patrones.factory.FabricaAuto;
import com.f1legends.patrones.factory.TipoAuto;

import java.util.List;

public class AutoService {
    private final AutoDAO autoDAO;
    private final EscuderiaDAO escuderiaDAO;
    private final FabricaAuto fabricaAuto;

    public AutoService() {
        this.autoDAO = new AutoDAO();
        this.escuderiaDAO = new EscuderiaDAO();
        this.fabricaAuto = new FabricaAuto(autoDAO, escuderiaDAO);
    }

    public Auto altaAuto(String modelo, double velocidadBase, int escuderiaId, TipoAuto tipoAuto) {
        validarDatosAuto(modelo, velocidadBase);
        validarTipoAuto(tipoAuto);
        validarReglamentoDisponible(escuderiaId, tipoAuto, 0);
        return fabricaAuto.crearAutoPersistido(tipoAuto, modelo, velocidadBase, escuderiaId);
    }

    public void modificarAuto(int id, String modelo, double velocidadBase, int escuderiaId, TipoAuto tipoAuto) {
        validarDatosAuto(modelo, velocidadBase);
        validarTipoAuto(tipoAuto);
        Escuderia escuderia = escuderiaDAO.obtenerPorId(escuderiaId);
        if (escuderia == null) {
            throw new IllegalArgumentException("No existe una escuderia con ID " + escuderiaId + ".");
        }

        Auto auto = autoDAO.obtenerPorId(id);
        if (auto == null) {
            throw new IllegalArgumentException("No existe un auto con ID " + id + ".");
        }

        auto.setModelo(modelo);
        auto.setVelocidadBase(velocidadBase);
        auto.setEscuderia(escuderia);
        validarReglamentoDisponible(escuderiaId, tipoAuto, id);
        auto.setTipoAuto(tipoAuto);
        autoDAO.actualizar(auto);
    }

    public void bajaAuto(int id) {
        if (autoDAO.obtenerPorId(id) == null) {
            throw new IllegalArgumentException("No existe un auto con ID " + id + ".");
        }
        autoDAO.eliminar(id);
    }

    public Auto obtenerAuto(int id) {
        return autoDAO.obtenerPorId(id);
    }

    public List<Auto> consultarAutos() {
        return autoDAO.obtenerTodos();
    }

    private void validarDatosAuto(String modelo, double velocidadBase) {
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("El modelo del auto no puede estar vacio.");
        }
        if (velocidadBase <= 0) {
            throw new IllegalArgumentException("La velocidad base debe ser mayor a cero.");
        }
    }

    private void validarTipoAuto(TipoAuto tipoAuto) {
        if (tipoAuto == null) {
            throw new IllegalArgumentException("Debe seleccionar una reglamentacion para el auto.");
        }
    }

    private void validarReglamentoDisponible(int escuderiaId, TipoAuto tipoAuto, int autoIdActual) {
        boolean yaExiste = autoDAO.obtenerTodosPorEscuderiaId(escuderiaId).stream()
                .anyMatch(auto -> auto.getTipoAuto() == tipoAuto && auto.getId() != autoIdActual);

        if (yaExiste) {
            throw new IllegalArgumentException("La escuderia ya tiene un auto para " + tipoAuto + ".");
        }
    }
}
