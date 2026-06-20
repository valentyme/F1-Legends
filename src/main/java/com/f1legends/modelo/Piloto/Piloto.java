package com.f1legends.modelo.Piloto;

import com.f1legends.patrones.estrategias.EstrategiaConduccion;

public class
Piloto {
    private int id;
    private String nombre;
    private int habilidad;
    private int escuderiaId;
    private int autoId;
    private EstrategiaConduccion estrategia;

    // Constructor usado por el DAO (sin estrategia)
    public Piloto(int id, String nombre, int habilidad, int escuderiaId,int autoId) {
        this.id = id;
        this.nombre = nombre;
        this.habilidad = habilidad;
        this.estrategia = null;
        this.escuderiaId = escuderiaId;
        this.autoId = autoId;
    }

    public Piloto(int id, String nombre, int habilidad, int escuderiaId, EstrategiaConduccion estrategia, int autoId) {
        this.id = id;
        this.nombre = nombre;
        this.habilidad = habilidad;
        this.escuderiaId = escuderiaId;
        this.estrategia = estrategia;
        this.autoId = autoId;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getHabilidad() { return habilidad; }
    public int getEscuderiaId() { return escuderiaId; }
    public int getAutoId() { return autoId; }


    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setHabilidad(int habilidad) { this.habilidad = habilidad; }
    public void setEscuderiaId(int escuderiaId) { this.escuderiaId = escuderiaId; }
    public void setAutoId(int autoId) { this.autoId = autoId; }


    public void setEstrategiaConduccion(EstrategiaConduccion estrategia) {
        this.estrategia = estrategia;
    }

    public double calcularRendimiento(double condicionesPista) {
        if (estrategia == null) {
            return habilidad * condicionesPista; // default
        }
        return estrategia.calcularRendimiento(this, condicionesPista);
    }

    @Override
    public String toString() {
        return nombre + " (Habilidad: " + habilidad + ")";
    }
}

