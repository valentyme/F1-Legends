package com.f1legends.patrones.estado;

import com.f1legends.modelo.Carrera;

public class EstadoFinalizada implements EstadoCarrera {
    @Override
    public void iniciar(Carrera carrera) {
        // Una carrera finalizada no vuelve a iniciar.
    }

    @Override
    public void actualizar(Carrera carrera, double deltaTiempo) {
        // La carrera termino, no hay mas avances.
    }

    @Override
    public void pausar(Carrera carrera) {
        // No se puede pausar una carrera finalizada.
    }

    @Override
    public void reanudar(Carrera carrera) {
        // No se puede reanudar una carrera finalizada.
    }

    @Override
    public void abandonar(Carrera carrera) {
        // Ya esta finalizada.
    }

    @Override
    public String getNombre() {
        return "Finalizada";
    }
}
