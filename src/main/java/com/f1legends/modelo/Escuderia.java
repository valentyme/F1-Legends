package com.f1legends.modelo;

import javafx.scene.paint.Color;

public class Escuderia {
    private int id;
    private String nombre;
    private Color color;

    public Escuderia(int id, String nombre, Color color) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public Color getColor() { return color; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setColor(Color color) { this.color = color; }
}
