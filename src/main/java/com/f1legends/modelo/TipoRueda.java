package com.f1legends.modelo;

public class TipoRueda {
    private final int id;
    private final String nombre;
    private final int durabilidad;
    private final double rendimiento;

    public TipoRueda(int id, String nombre, int durabilidad, double rendimiento) {
        this.id = id;
        this.nombre = nombre;
        this.durabilidad = durabilidad;
        this.rendimiento = rendimiento;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getDurabilidad() { return durabilidad; }
    public double getRendimiento() { return rendimiento; }
}
