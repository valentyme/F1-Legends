package com.f1legends.modelo;

import com.f1legends.servicios.ConfiguracionCarrera;

public class Carrera {
    private int id;
    private Circuito circuito;
    private String fecha;
    private int vueltas;
    private String climaInicial;

    public Carrera(int id, Circuito circuito, String fecha, int vueltas, String climaInicial) {
        this.id = id;
        this.circuito = circuito;
        this.fecha = fecha;
        this.vueltas = vueltas;
        this.climaInicial = climaInicial;
    }

    public Carrera(ConfiguracionCarrera configuracionCarrera) {

    }
}
