package com.f1legends.modelo;

public abstract class Circuito {
    private String nombre;

    public Circuito(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }

    // Cada circuito define cómo dibujar y calcular posiciones
    public abstract void dibujar(javafx.scene.canvas.GraphicsContext gc);
    public abstract Punto calcularPosicion(double progreso);
}
