package com.f1legends.modelo.carreras;

public class EventoCarrera {
    private int id;
    private String tipoEvento;
    private String descripcion;
    private int vuelta;

    public EventoCarrera(String tipoEvento, String descripcion, int vuelta) {
        this(0, tipoEvento, descripcion, vuelta);
    }

    public EventoCarrera(int id, String tipoEvento, String descripcion, int vuelta) {
        this.id = id;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
        this.vuelta = vuelta;
    }

    public int getId() {
        return id;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getVuelta() {
        return vuelta;
    }

    @Override
    public String toString() {
        return "[" + tipoEvento + "] Vuelta " + vuelta + ": " + descripcion;
    }

    public String getTipo() { return tipoEvento;
    }
}
