package com.f1legends.patrones.observer;

import com.f1legends.modelo.auto.Auto;
import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.modelo.carreras.EventoCarrera;

import java.util.HashMap;
import java.util.Map;

public class EstadisticaObserver implements ObservadorCarrera {
    private final Map<Integer, Integer> vueltasPorAuto = new HashMap<>();
    private int cambiosEstado;
    private int boxes;
    private int accidentes;
    private int adelantamientos;

    @Override
    public void actualizar(EventoCarrera evento, Carrera carrera) {
        if ("VUELTA_COMPLETADA".equals(evento.getTipoEvento())) {
            for (Auto auto : carrera.getAutos()) {
                vueltasPorAuto.put(auto.getId(), auto.getVueltasCompletadas());
            }
        }
        if ("CAMBIO_ESTADO".equals(evento.getTipoEvento())) {
            cambiosEstado++;
        }
        if ("BOXES".equals(evento.getTipoEvento())) {
            boxes++;
        }
        if ("ACCIDENTE".equals(evento.getTipoEvento())) {
            accidentes++;
        }
        if ("ADELANTAMIENTO".equals(evento.getTipoEvento())) {
            adelantamientos++;
        }
    }

    public int getVueltasAuto(int autoId) {
        return vueltasPorAuto.getOrDefault(autoId, 0);
    }

    public int getCambiosEstado() {
        return cambiosEstado;
    }

    public int getBoxes() {
        return boxes;
    }

    public int getAccidentes() {
        return accidentes;
    }

    public int getAdelantamientos() {
        return adelantamientos;
    }
}
