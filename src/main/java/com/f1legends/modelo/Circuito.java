package com.f1legends.modelo;

public abstract class Circuito {
    private int id;          // PK en la BD
    private String nombre;   // nombre del circuito
    private String pais;     // país del circuito
    private int vueltas;     // cantidad de vueltas

    public Circuito(int id, String nombre, String pais, int vueltas) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.vueltas = vueltas;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPais() { return pais; }
    public int getVueltas() { return vueltas; }

    // Métodos abstractos: cada circuito define su trazado y posiciones
    public abstract void dibujar(javafx.scene.canvas.GraphicsContext gc);
    public abstract Punto calcularPosicion(double progreso);
}
