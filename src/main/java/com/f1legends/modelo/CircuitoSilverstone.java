package com.f1legends.modelo;

import javafx.scene.canvas.GraphicsContext;

public class CircuitoSilverstone extends Circuito {
    public CircuitoSilverstone(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }

    @Override
    public void dibujar(GraphicsContext gc) {
        // TODO: implementar trazado de Silverstone
    }

    @Override
    public Punto calcularPosicion(double t) {
        return new Punto(0,0); // placeholder
    }
}

