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

    // Constructor que recibe la configuración
    public Carrera(ConfiguracionCarrera configuracionCarrera) {
        this.circuito = configuracionCarrera.getCircuito();
        this.vueltas = configuracionCarrera.getVueltas();
        this.climaInicial = configuracionCarrera.getClimaInicial();
    }

    // Getters
    public int getId() { return id; }
    public Circuito getCircuito() { return circuito; }
    public String getFecha() { return fecha; }
    public int getVueltas() { return vueltas; }
    public String getClimaInicial() { return climaInicial; }
}
