package com.f1legends.patrones.estado;

import com.f1legends.modelo.carreras.Carrera;

public class EstadoInicio implements EstadoCarrera {
    @Override
    public void iniciar(Carrera carrera) {
        carrera.cambiarEstado(new EstadoBanderaVerde());
    }

    @Override
    public void actualizar(Carrera carrera, double deltaTiempo) {
        // La carrera todavia no comenzo, por eso no avanza.
    }

    @Override
    public void pausar(Carrera carrera) {
        // No se puede pausar una carrera que todavia no inicio.
    }

    @Override
    public void reanudar(Carrera carrera) {
        iniciar(carrera);
    }

    @Override
    public void abandonar(Carrera carrera) {
        carrera.cambiarEstado(new EstadoFinalizada());
    }

    @Override
    public String getNombre() {
        return "Inicio";
    }
}
