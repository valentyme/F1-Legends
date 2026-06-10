package com.f1legends.modelo.carreras;

public class Ranking {
    private int posicion;
    private int puntosTotales;

    public void setPosicion(int posicionJugador) {
        this.posicion = posicionJugador;
    }

    public void setPuntosTotales(int puntosTotales) {
        this.puntosTotales = puntosTotales;
    }

    public int getPuntosTotales() {
        return puntosTotales;
    }

    public Object getPosicion() {
        return posicion;
    }
}
