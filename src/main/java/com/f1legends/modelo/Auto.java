package com.f1legends.modelo;

import javafx.scene.paint.Color;

public class Auto {
    private int id;                // PK en la BD
    private String modelo;         // modelo del auto
    private double velocidadBase;  // velocidad_base en la BD
    private Escuderia escuderia;   // relación con Escuderia (FK)

    // Atributo adicional para simulación
    private double progreso;

    public Auto(int id, String modelo, double velocidadBase, Escuderia escuderia) {
        this.id = id;
        this.modelo = modelo;
        this.velocidadBase = velocidadBase;
        this.escuderia = escuderia;
        this.progreso = 0.0;
    }

    // Método de simulación
    public void avanzar(double deltaTiempo) {
        progreso += velocidadBase * deltaTiempo;
        if (progreso > 1.0) progreso -= 1.0;
    }

    // Getters
    public int getId() { return id; }
    public String getModelo() { return modelo; }
    public double getVelocidadBase() { return velocidadBase; }
    public Escuderia getEscuderia() { return escuderia; }
    public double getProgreso() { return progreso; }

    // Setters opcionales
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setVelocidadBase(double velocidadBase) { this.velocidadBase = velocidadBase; }
    public void setEscuderia(Escuderia escuderia) { this.escuderia = escuderia; }
}
