package com.f1legends.patrones.observer;

import com.f1legends.modelo.carreras.Carrera;
import com.f1legends.modelo.carreras.EventoCarrera;

public interface ObservadorCarrera {
    void actualizar(EventoCarrera evento, Carrera carrera);
}
