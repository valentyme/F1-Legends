package com.f1legends.modelo;

import javafx.scene.paint.Color;

public class Auto {
    private String nombre;
    private double velocidad;
    private double progreso;
    private Color color;

    public Auto(String nombre, double velocidad, Color color) {
        this.nombre = nombre;
        this.velocidad = velocidad;
        this.progreso = 0.0;
        this.color = color;
    }

    public void avanzar(double deltaTiempo) {
        progreso += velocidad * deltaTiempo;
        if (progreso > 1.0) progreso -= 1.0;
    }

    public double getProgreso() { return progreso; }
    public String getNombre() { return nombre; }
    public double getVelocidad() { return velocidad; }
    public Color getColor() { return color; }
}
