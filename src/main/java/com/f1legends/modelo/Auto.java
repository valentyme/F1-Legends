package com.f1legends.modelo;

public class Auto {
    private int id;
    private String modelo;
    private double velocidadBase;
    private Escuderia escuderia;
    private double progreso;
    private int vueltasCompletadas;

    public Auto(int id, String modelo, double velocidadBase, Escuderia escuderia) {
        this.id = id;
        this.modelo = modelo;
        this.velocidadBase = velocidadBase;
        this.escuderia = escuderia;
        this.progreso = 0.0;
        this.vueltasCompletadas = 0;
    }

    public void avanzar(double deltaTiempo) {
        progreso += velocidadBase * deltaTiempo;
        while (progreso >= 1.0) {
            progreso -= 1.0;
            vueltasCompletadas++;
        }
    }

    public int getId() { return id; }
    public String getModelo() { return modelo; }
    public double getVelocidadBase() { return velocidadBase; }
    public Escuderia getEscuderia() { return escuderia; }
    public double getProgreso() { return progreso; }
    public int getVueltasCompletadas() { return vueltasCompletadas; }

    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setVelocidadBase(double velocidadBase) { this.velocidadBase = velocidadBase; }
    public void setEscuderia(Escuderia escuderia) { this.escuderia = escuderia; }
}
