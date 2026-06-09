package com.f1legends.patrones.estado;

import com.f1legends.modelo.carreras.Carrera;

public interface EstadoCarrera {
    void iniciar(Carrera carrera);

    void actualizar(Carrera carrera, double deltaTiempo);

    void pausar(Carrera carrera);

    void reanudar(Carrera carrera);

    void abandonar(Carrera carrera);

    String getNombre();
}
