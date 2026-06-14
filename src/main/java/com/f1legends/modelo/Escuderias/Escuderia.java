package com.f1legends.modelo.Escuderias;

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

    @Override
    public String toString() {
        String colorHex = (color != null)
                ? String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255))
                : "N/A";

        return String.format("ID: %d | %-15s | Color: %s", id, nombre, colorHex);
    }


}
