package com.f1legends.vista;

/**
 * Evento visual temporal que se dibuja sobre el canvas durante unos segundos.
 */
public class EventoVisual {
    public enum Tipo { ACCIDENTE, BOXES, ADELANTAMIENTO, CLIMA, FINALIZACION }

    public final Tipo tipo;
    public final String texto;
    public double x, y;          // posición en canvas
    public double tiempoRestante; // segundos que queda visible

    public EventoVisual(Tipo tipo, String texto, double x, double y, double duracion) {
        this.tipo = tipo;
        this.texto = texto;
        this.x = x;
        this.y = y;
        this.tiempoRestante = duracion;
    }
}