package com.f1legends.patrones.observer;

import com.f1legends.modelo.carreras.EventoCarrera;

public interface ObservableCarrera {
    void agregarObservador(ObservadorCarrera observador);

    void eliminarObservador(ObservadorCarrera observador);

    void notificarObservadores(EventoCarrera evento);
}
