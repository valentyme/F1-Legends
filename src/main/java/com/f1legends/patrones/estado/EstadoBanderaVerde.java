package com.f1legends.patrones.estado;

import com.f1legends.modelo.carreras.Carrera;

public class EstadoBanderaVerde implements EstadoCarrera {
    @Override
    public void iniciar(Carrera carrera) {
        // La carrera ya esta en marcha.
    }

    @Override
    public void actualizar(Carrera carrera, double deltaTiempo) {
        carrera.avanzarAutos(deltaTiempo);
        if (carrera.estaCompletada()) {
            carrera.cambiarEstado(new EstadoFinalizada());
        }
    }

    @Override
    public void pausar(Carrera carrera) {
        carrera.cambiarEstado(new EstadoPausada());
    }

    @Override
    public void reanudar(Carrera carrera) {
        // Ya esta corriendo.
    }

    @Override
    public void abandonar(Carrera carrera) {
        carrera.cambiarEstado(new EstadoFinalizada());
    }

    @Override
    public String getNombre() {
        return "Bandera verde";
    }
}
