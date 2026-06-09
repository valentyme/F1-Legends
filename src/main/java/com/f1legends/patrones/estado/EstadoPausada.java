package com.f1legends.patrones.estado;

import com.f1legends.modelo.carreras.Carrera;

public class EstadoPausada implements EstadoCarrera {
    @Override
    public void iniciar(Carrera carrera) {
        reanudar(carrera);
    }

    @Override
    public void actualizar(Carrera carrera, double deltaTiempo) {
        // Carrera pausada: no se actualizan posiciones.
    }

    @Override
    public void pausar(Carrera carrera) {
        // Ya esta pausada.
    }

    @Override
    public void reanudar(Carrera carrera) {
        carrera.cambiarEstado(new EstadoBanderaVerde());
    }

    @Override
    public void abandonar(Carrera carrera) {
        carrera.cambiarEstado(new EstadoFinalizada());
    }

    @Override
    public String getNombre() {
        return "Pausada";
    }
}
