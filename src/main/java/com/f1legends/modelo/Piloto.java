package com.f1legends.modelo;

public class Piloto {
    private int id;
    private String nombre;
    private int habilidad;

    public Piloto(int id, String nombre, int habilidad) {
        this.id = id;
        this.nombre = nombre;
        this.habilidad = habilidad;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getHabilidad() { return habilidad; }

    @Override
    public String toString() {
        return nombre + " (Habilidad: " + habilidad + ")";
    }
}

