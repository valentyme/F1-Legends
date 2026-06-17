package com.f1legends.patrones.observer;

import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.modelo.carreras.EventoCarrera;

public class VistaCarreraObserver implements ObservadorCarrera {
    @Override
    public void actualizar(EventoCarrera evento, Carrera carrera) {
        if ("CAMBIO_ESTADO".equals(evento.getTipoEvento())
                || "FINALIZACION".equals(evento.getTipoEvento())
                || "ABANDONO".equals(evento.getTipoEvento())
                || "BOXES".equals(evento.getTipoEvento())
                || "ACCIDENTE".equals(evento.getTipoEvento())
                || "CAMBIO_CLIMA".equals(evento.getTipoEvento())
                || "ADELANTAMIENTO".equals(evento.getTipoEvento())) {
            System.out.println("  Evento: " + evento.getDescripcion());
        }
    }
}
