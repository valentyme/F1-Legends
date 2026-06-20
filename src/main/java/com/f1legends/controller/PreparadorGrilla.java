package com.f1legends.controller;

import com.f1legends.modelo.Piloto;
import com.f1legends.modelo.Usuarios.Participante;
import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.patrones.facade.SistemaCarreraFacade;

import java.util.List;


public class PreparadorGrilla {

    public boolean prepararAutosParticipantes(Carrera carrera, SistemaCarreraFacade facade) {
        List<Participante> participantes = facade.getConfiguracionCarrera().getParticipantes();

        if (participantes == null || participantes.isEmpty()) {
            return false;
        }

        for (Participante p : participantes) {
            if (p == null || p.getJugador() == null || p.getPiloto() == null || p.getAuto() == null) {
                return false;
            }

            Auto auto = p.getAuto();
            Piloto piloto = p.getPiloto();

            double condicionesPista = obtenerCondicionesPista(carrera.getClimaInicial());
            double factorHabilidad = 0.85 + (piloto.getHabilidad() / 100.0) * 0.30;
            double rendimientoBase = piloto.getHabilidad() * condicionesPista;
            double rendimientoConEstrategia = piloto.calcularRendimiento(condicionesPista);

            if (rendimientoBase > 0) {
                auto.setFactorEstrategia(factorHabilidad * (rendimientoConEstrategia / rendimientoBase));
            } else {
                auto.setFactorEstrategia(factorHabilidad);
            }

            carrera.agregarAuto(auto);
        }
        return true;
    }

    private double obtenerCondicionesPista(String clima) {
        if (clima == null) return 1.0;
        return switch (clima) {
            case "Lluvioso" -> 0.7;
            case "Nublado"  -> 0.9;
            default         -> 1.0;
        };
    }
}