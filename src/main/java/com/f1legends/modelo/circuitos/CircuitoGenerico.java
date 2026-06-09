package com.f1legends.modelo.circuitos;

import com.f1legends.modelo.Punto;
import javafx.scene.canvas.GraphicsContext;

public class CircuitoGenerico extends Circuito {
    public CircuitoGenerico(int id, String nombre, String pais, int vueltas) {
        super(id, nombre, pais, vueltas);
    }

    @Override
    public void dibujar(GraphicsContext gc) {
        // TODO: trazado genérico
    }

    @Override
    public Punto calcularPosicion(double t) {
        return new Punto(0,0); // placeholder
    }
}
