package com.f1legends.modelo.auto;

import com.f1legends.modelo.Escuderias.Escuderia;

public class Auto {
    private int id;
    private String modelo;
    private double velocidadBase;
    private Escuderia escuderia;
    private double progreso;
    private int vueltasCompletadas;

    private final double factorAleatorio;
    private double variacionMomento;
    private int ticksHastaProximaVariacion;

    public Auto(int id, String modelo, double velocidadBase, Escuderia escuderia) {
        this.id = id;
        this.modelo = modelo;
        this.velocidadBase = velocidadBase;
        this.escuderia = escuderia;
        this.progreso = 0.0;
        this.vueltasCompletadas = 0;
        this.factorAleatorio = 0.80 + Math.random() * 0.40;
        this.variacionMomento = 1.0;
        this.ticksHastaProximaVariacion = 0;
    }

    public void avanzar(double deltaTiempo) {
        actualizarVariacionMomento();

        double velocidadReal = velocidadBase * factorAleatorio * variacionMomento;
        progreso += velocidadReal * deltaTiempo;

        while (progreso >= 1.0) {
            progreso -= 1.0;
            vueltasCompletadas++;
        }
    }

    private void actualizarVariacionMomento() {
        ticksHastaProximaVariacion--;
        if (ticksHastaProximaVariacion <= 0) {
            variacionMomento = 0.85 + Math.random() * 0.30;
            ticksHastaProximaVariacion = 20 + (int) (Math.random() * 30);
        }
    }

    public int getId() { return id; }
    public String getModelo() { return modelo; }
    public double getVelocidadBase() { return velocidadBase; }
    public Escuderia getEscuderia() { return escuderia; }
    public double getProgreso() { return progreso; }
    public int getVueltasCompletadas() { return vueltasCompletadas; }
    public double getFactorAleatorio() { return factorAleatorio; }

    public double getVelocidadEfectiva() {
        return velocidadBase * factorAleatorio * variacionMomento;
    }

    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setVelocidadBase(double velocidadBase) { this.velocidadBase = velocidadBase; }
    public void setEscuderia(Escuderia escuderia) { this.escuderia = escuderia; }
    public void setProgreso(double progreso) { this.progreso = progreso; }
}
